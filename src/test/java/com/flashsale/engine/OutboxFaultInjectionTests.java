package com.flashsale.engine;

import java.nio.file.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.testcontainers.containers.*;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.MountableFile;
import static org.junit.jupiter.api.Assertions.*;

/** Reuses the existing HTTP/k6 harness with isolated, capped Testcontainers replicas. */
@EnabledIfSystemProperty(named="flashsale.run-container-faults", matches="true")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OutboxFaultInjectionTests {
    static final Path ROOT=Path.of(System.getProperty("flashsale.fault-results", "bench/results/outbox-session/testcontainers"));
    static final Network NET=Network.newNetwork();
    static final PostgreSQLContainer<?> PG=new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("flashsale_db").withUsername("postgres").withPassword("password")
            .withNetwork(NET).withNetworkAliases("postgres")
            .withCreateContainerCmdModifier(c -> c.getHostConfig().withMemory(384L*1024*1024).withMemorySwap(384L*1024*1024));
    static final GenericContainer<?> REDIS=new GenericContainer<>("redis:7-alpine")
            .withNetwork(NET).withNetworkAliases("redis").withExposedPorts(6379)
            .withCreateContainerCmdModifier(c -> c.getHostConfig().withMemory(384L*1024*1024).withMemorySwap(384L*1024*1024));
    static final RabbitMQContainer BROKER=new RabbitMQContainer("rabbitmq:3.13-management-alpine")
            .withNetwork(NET).withNetworkAliases("rabbitmq")
            .withCreateContainerCmdModifier(c -> c.getHostConfig().withMemory(384L*1024*1024).withMemorySwap(384L*1024*1024));
    static GenericContainer<?> app() {
        return new GenericContainer<>("flash-sale-engine:local")
            .withNetwork(NET).withNetworkAliases("app").withExposedPorts(8080)
            .withEnv("JAVA_TOOL_OPTIONS","-Xmx256m")
            .withEnv("SPRING_DATASOURCE_URL","jdbc:postgresql://postgres:5432/flashsale_db")
            .withEnv("SPRING_DATA_REDIS_HOST","redis").withEnv("SPRING_RABBITMQ_HOST","rabbitmq")
            .withEnv("SPRING_APPLICATION_JSON","{\"logging\":{\"level\":{\"com.flashsale.engine.service.OutboxRelay\":\"DEBUG\"}}}")
            .withCreateContainerCmdModifier(c -> c.getHostConfig().withMemory(640L*1024*1024).withMemorySwap(640L*1024*1024))
            .waitingFor(Wait.forLogMessage(".*Started FlashSaleEngineApplication.*\\n",1))
            .withStartupTimeout(Duration.ofMinutes(2));
    }
    static final GenericContainer<?> A=app(), B=app();
    static final GenericContainer<?> PROXY=new GenericContainer<>("nginx:1.28-alpine")
            .withNetwork(NET).withExposedPorts(8080)
            .withCopyFileToContainer(MountableFile.forHostPath("deploy/nginx.conf"),"/etc/nginx/nginx.conf")
            .withCreateContainerCmdModifier(c -> c.getHostConfig().withMemory(64L*1024*1024).withMemorySwap(64L*1024*1024));

    @BeforeAll static void start() throws Exception {
        Files.createDirectories(ROOT);
        PG.start(); REDIS.start(); BROKER.start(); A.start(); B.start(); PROXY.start();
        host(ROOT,"fixture-inspect.log","docker","inspect",PG.getContainerId(),REDIS.getContainerId(),BROKER.getContainerId(),A.getContainerId(),B.getContainerId(),PROXY.getContainerId());
    }
    @AfterAll static void stop() {
        for (var c: List.of(PROXY,B,A,BROKER,REDIS,PG)) c.stop();
        NET.close();
    }
    static Map<String,String> env() {
        return Map.of("OUTBOX_MODE","true","BASE_URL","http://"+PROXY.getHost()+":"+PROXY.getMappedPort(8080),
                "BENCH_POSTGRES",PG.getContainerId(),"BENCH_REDIS",REDIS.getContainerId(),"BENCH_RABBITMQ",BROKER.getContainerId(),
                "BENCH_APP_CONTAINERS",A.getContainerId()+" "+B.getContainerId());
    }
    static void host(Path dir,String file,String...args) throws Exception {
        Files.createDirectories(dir);
        var builder=new ProcessBuilder(args).redirectErrorStream(true).redirectOutput(dir.resolve(file).toFile());
        builder.environment().putAll(env());
        var proc=builder.start();
        if (!proc.waitFor(10,TimeUnit.MINUTES)) {
            proc.descendants().forEach(ProcessHandle::destroyForcibly);
            proc.destroyForcibly();
            fail("Command timeout: "+Arrays.toString(args));
        }
        assertEquals(0,proc.exitValue(),"Command failed; raw output: "+dir.resolve(file));
    }
    static String sql(String query) throws Exception {
        var output=PG.execInContainer("psql","-U","postgres","-d","flashsale_db","-tAc",query);
        assertEquals(0,output.getExitCode(),output.getStderr()); return output.getStdout().trim();
    }
    static void settled(Path dir) throws Exception {
        host(dir,"invariants-command.log","python3","bench/outbox_reconcile.py",dir.toString());
        var summary=tools.jackson.databind.json.JsonMapper.builder().build()
                .readTree(Files.readString(dir.resolve("reconciliation.json")));
        assertTrue(summary.path("drained").asBoolean(),"Queue/outbox drain not observed: "+dir);
        System.out.println("OUTBOX_RUN "+dir+" PASS "+Files.readString(dir.resolve("outbox-invariants.json")));
    }
    @Test @Order(1) void existingOversellAndIdempotencyHarness() throws Exception {
        host(ROOT.resolve("http"),"command.log","python3","bench/http_concurrency.py",ROOT.resolve("http").toString());
    }
    @RepeatedTest(10) @Order(2) void brokerRestartsDuringSustainedLoad(RepetitionInfo repetition) throws Exception {
        var dir=ROOT.resolve("rabbit-"+repetition.getCurrentRepetition());
        try {
            host(dir,"experiment-command.log","python3","bench/experiment.py",dir.toString(),"rabbit");
            settled(dir);
        } finally { save(dir); }
    }
    @RepeatedTest(3) @Order(3) void killedReplicaRecovers(RepetitionInfo repetition) throws Exception {
        var dir=ROOT.resolve("kill-"+repetition.getCurrentRepetition());
        try {
            host(dir,"experiment-command.log","python3","bench/experiment.py",dir.toString(),"kill");
            settled(dir);
        } finally { save(dir); }
    }
    static void save(Path dir) throws Exception {
        Files.createDirectories(dir);
        // Bounded aggregate only. Detailed rows are streamed by the Python oracle.
        Files.writeString(dir.resolve("postgres-final.json"),sql("select jsonb_build_object('orders',(select count(*) from orders),'confirmed',(select count(*) from orders where status='CONFIRMED'),'pending_orders',(select count(*) from orders where status='PENDING'),'pending_events',(select count(*) from outbox_events where status='PENDING'),'stock',(select available_stock from products where id=101))"));
        for (int i=0;i<5;i++) {
            var c=List.of(A,B,PG,REDIS,BROKER).get(i);
            var log=dir.resolve("post-oracle-cgroup-"+i+".log");
            var proc=new ProcessBuilder("docker","exec",c.getContainerId(),"sh","-c","cat /sys/fs/cgroup/cpu.stat; cat /sys/fs/cgroup/memory.events")
                    .redirectErrorStream(true).redirectOutput(log.toFile()).start();
            if (proc.waitFor(60,TimeUnit.SECONDS)) Files.writeString(log,"\nexit="+proc.exitValue()+"\n",StandardOpenOption.APPEND);
            else { proc.destroyForcibly(); Files.writeString(log,"\nexit=null; observation timed out\n",StandardOpenOption.APPEND); }
        }
        String since=Files.exists(dir.resolve("run-started.txt"))?Files.readString(dir.resolve("run-started.txt")).trim():"0";
        for (var c:List.of(A,B,BROKER)) host(dir,c==A?"app-a.log":c==B?"app-b.log":"broker.log",
                "docker","logs","--since",since,c.getContainerId());
    }
    @Test @Order(4) void killPollerAfterAckBeforeBatchCommitDoesNotDropOrDoubleProcess() throws Exception {
        var dir=ROOT.resolve("poller-crash"); Files.createDirectories(dir);
        Files.writeString(dir.resolve("run-started.txt"),java.time.Instant.now().toString());
        sql("TRUNCATE orders CASCADE");
        // A database trigger blocks the relay AFTER a real publisher ack, before
        // the PUBLISHED update commits. No production fault hook is needed.
        sql("create function pause_outbox_test() returns trigger language plpgsql as $$ begin perform pg_sleep(60); return NEW; end $$");
        sql("create trigger pause_outbox_test before update on outbox_events for each row when (NEW.status='PUBLISHED') execute function pause_outbox_test()");
        try {
            var client=java.net.http.HttpClient.newHttpClient();
            String base=env().get("BASE_URL");
            var init=java.net.http.HttpRequest.newBuilder(java.net.URI.create(base+"/api/v1/orders/init?productId=101&stock=1")).POST(java.net.http.HttpRequest.BodyPublishers.noBody()).build();
            assertEquals(200,client.send(init,java.net.http.HttpResponse.BodyHandlers.ofString()).statusCode());
            var req=java.net.http.HttpRequest.newBuilder(java.net.URI.create(base+"/api/v1/orders"))
                .header("Content-Type","application/json").POST(java.net.http.HttpRequest.BodyPublishers.ofString("{\"productId\":101,\"quantity\":1,\"idempotencyKey\":\"poller-crash\"}")).build();
            assertEquals(202,client.send(req,java.net.http.HttpResponse.BodyHandlers.ofString()).statusCode());
            long deadline=System.nanoTime()+Duration.ofSeconds(30).toNanos();
            String pid="";
            while(System.nanoTime()<deadline) {
                pid=sql("select host(client_addr) from pg_stat_activity where wait_event='PgSleep' and query like 'update outbox_events%' limit 1");
                if(!pid.isBlank()) break; Thread.sleep(100);
            }
            assertFalse(pid.isBlank(),"Did not observe relay blocked after ack");
            Files.writeString(dir.resolve("mid-batch-postgres-activity.json"),sql("select jsonb_agg(a) from pg_stat_activity a where wait_event='PgSleep'"));
            String aIP=A.getContainerInfo().getNetworkSettings().getNetworks().values().iterator().next().getIpAddress();
            var victim=pid.equals(aIP)?A:B;
            var survivor=victim==A?B:A;
            assertTrue(pid.equals(aIP) || pid.equals(B.getContainerInfo().getNetworkSettings().getNetworks().values().iterator().next().getIpAddress()), "Unknown relay address");
            Files.writeString(dir.resolve("victim.txt"),victim.getContainerId()+"\nclient_addr="+pid+"\n");
            // Isolate the deterministic crash window from the other relay while
            // removing the test trigger; otherwise it could enter the same sleep.
            host(dir,"stop-other-relay.log","docker","stop",survivor.getContainerId());
            host(dir,"kill.log","docker","kill","-s","KILL",victim.getContainerId());
            // Killing the app does not necessarily cancel pg_sleep immediately;
            // terminate its DB session to model detection of the broken connection.
            sql("select pg_terminate_backend(pid) from pg_stat_activity where host(client_addr)='"+pid+"' and wait_event='PgSleep'");
            sql("drop trigger pause_outbox_test on outbox_events"); sql("drop function pause_outbox_test()");
            host(dir,"restart.log","docker","start",victim.getContainerId());
            host(dir,"restart-other-relay.log","docker","start",survivor.getContainerId());
            deadline=System.nanoTime()+Duration.ofMinutes(2).toNanos();
            while(System.nanoTime()<deadline && !sql("select count(*) from outbox_events where status='PENDING'").equals("0")) Thread.sleep(100);
            assertEquals("1|1|0",sql("select count(*),count(*) filter(where status='CONFIRMED'),(select available_stock from products where id=101) from orders"));
            assertEquals("1|0",sql("select count(*),count(*) filter(where status<>'PUBLISHED') from outbox_events"));
            save(dir);
            String event=sql("select event_id::text from outbox_events");
            // Read only this case's already-captured logs, one line at a time.
            // Fetching the entire long-lived fixture history exceeded the capped
            // test JVM heap in the retained first poller-crash attempt.
            long acknowledgements=0;
            for (String name:List.of("app-a.log","app-b.log")) {
                try (var lines=Files.lines(dir.resolve(name))) {
                    acknowledgements+=lines.filter(line->line.contains("OUTBOX_ACK eventId="+event)).count();
                }
            }
            assertTrue(acknowledgements>=2,"Expected an actual republish after the uncommitted ack");
            Files.writeString(dir.resolve("result.json"),"{\"pass\":true,\"broker_ack_observations\":"+acknowledgements+",\"confirmed_orders\":1,\"available_stock\":0,\"pending_events\":0}\n");
        } finally {
            sql("drop trigger if exists pause_outbox_test on outbox_events");
            sql("drop function if exists pause_outbox_test()");
            save(dir);
        }
    }
}
