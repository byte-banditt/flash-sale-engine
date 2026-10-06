# Verification results — 2026-10-06 UTC

## Environment

Commands run:

```bash
lscpu | rg 'Architecture:|CPU\(s\):|Model name:|Core\(s\) per socket:|Thread\(s\) per core:|Hypervisor vendor:'
rg '^MemTotal:|^SwapTotal:' /proc/meminfo
cat /etc/os-release | rg 'PRETTY_NAME|VERSION_ID'
uname -r
java -version
./mvnw -version
docker version --format 'Client {{.Client.Version}} / Server {{.Server.Version}}'
docker compose version
k6 version
```

Raw output (relevant lines):

```text
Architecture:                            x86_64
CPU(s):                                  16
Model name:                              AMD Ryzen 7 7840HS w/ Radeon 780M Graphics
Thread(s) per core:                      2
Core(s) per socket:                      8
Hypervisor vendor:                       Microsoft
MemTotal:        3636900 kB
SwapTotal:       1048576 kB
PRETTY_NAME="Debian GNU/Linux 13 (trixie)"
VERSION_ID="13"
6.18.33.2-microsoft-standard-wsl2
openjdk version "21.0.12.1" 2026-08-18
Apache Maven 3.9.16 (2bdd9fddda4b155ebf8000e807eb73fd829a51d5)
Client 29.8.0 / Server 29.8.0
Docker Compose version v5.5.1
k6 v2.2.0 (commit/00a9a1b7f5, go1.26.5, linux/amd64)
```

Docker Desktop reported version 4.91.0; Testcontainers reported version 2.0.5 in test log below.

## Integration tests

Oversell: M=100 stock, N=2000 concurrent requests with distinct keys, 64 client threads. Same-key test: K=40 concurrent requests. Other tests cover HTTP validation, exhausted consumer retries and duplicate DLQ delivery, and publish failure. Assertions require exactly M accepted and CONFIRMED, Redis stock zero, one same-key order/decrement, and exactly one rollback.

Command run:

```bash
./mvnw test -q > /tmp/flash-sale-test-final.log 2>&1; code=$?; tail -40 /tmp/flash-sale-test-final.log; exit "$code"
```

Exit code: 0. Surefire XML reported `tests="5" errors="0" skipped="0" failures="0" flakes="0"`. Raw command log follows. RabbitMQ connection reset lines occurred during container shutdown after tests passed.

```text
17:49:17.005 [main] INFO org.testcontainers.images.PullPolicy -- Image pull policy will be performed by: DefaultPullPolicy()
17:49:17.012 [main] INFO org.testcontainers.utility.ImageNameSubstitutor -- Image name substitution will be performed by: DefaultImageNameSubstitutor (composite of 'ConfigurationFileImageNameSubstitutor' and 'PrefixingImageNameSubstitutor')
17:49:17.034 [main] INFO org.testcontainers.DockerClientFactory -- Testcontainers version: 2.0.5
17:49:17.487 [main] INFO org.testcontainers.dockerclient.DockerClientProviderStrategy -- Loaded org.testcontainers.dockerclient.UnixSocketClientProviderStrategy from ~/.testcontainers.properties, will try it first
17:49:18.377 [main] INFO org.testcontainers.dockerclient.DockerClientProviderStrategy -- Found Docker environment with local Unix socket (unix:///var/run/docker.sock)
17:49:18.382 [main] INFO org.testcontainers.DockerClientFactory -- Docker host IP address is localhost
17:49:18.482 [main] INFO org.testcontainers.DockerClientFactory -- Connected to docker:
  Server Version: 29.8.0
  API Version: 1.56
  Operating System: Docker Desktop
  Total Memory: 3551 MB
  Labels:
    com.docker.desktop.address=unix:///var/run/docker-cli.sock
17:49:18.894 [main] INFO tc.testcontainers/ryuk:0.14.0 -- Creating container for image: testcontainers/ryuk:0.14.0
17:49:19.179 [main] INFO org.testcontainers.utility.RegistryAuthLocator -- Credential helper/store (docker-credential-desktop.exe) does not have credentials for https://index.docker.io/v1/
17:49:19.726 [main] INFO tc.testcontainers/ryuk:0.14.0 -- Container testcontainers/ryuk:0.14.0 is starting: 86e4b1715c39e3290a9ab99ed9c57faf5e20de88c4a36eb627829d3aa1dd53a6
17:49:21.006 [main] INFO tc.testcontainers/ryuk:0.14.0 -- Container testcontainers/ryuk:0.14.0 started in PT2.110892404S
17:49:21.024 [main] INFO org.testcontainers.utility.RyukResourceReaper -- Ryuk started - will monitor and terminate Testcontainers containers on JVM exit
17:49:21.025 [main] INFO org.testcontainers.DockerClientFactory -- Checking the system...
17:49:21.026 [main] INFO org.testcontainers.DockerClientFactory -- ✔︎ Docker server version should be at least 1.6.0
17:49:21.028 [main] INFO tc.rabbitmq:3.13-management-alpine -- Creating container for image: rabbitmq:3.13-management-alpine
17:49:21.197 [main] INFO tc.rabbitmq:3.13-management-alpine -- Container rabbitmq:3.13-management-alpine is starting: cfe67487d23d628e34ae13ccdbbd4b0339fa75fec308fa1ac93dd53a5b613933
17:49:29.556 [main] INFO tc.rabbitmq:3.13-management-alpine -- Container rabbitmq:3.13-management-alpine started in PT8.527699348S
17:49:29.564 [main] INFO tc.redis:7-alpine -- Creating container for image: redis:7-alpine
17:49:29.645 [main] INFO tc.redis:7-alpine -- Container redis:7-alpine is starting: 4b7d212a76142c667027ed70d00b736da6d1a14d21af6eb02e244d44b91b3e6b
17:49:30.020 [main] INFO tc.redis:7-alpine -- Container redis:7-alpine started in PT0.455569393S
17:49:30.021 [main] INFO tc.postgres:16-alpine -- Creating container for image: postgres:16-alpine
17:49:30.115 [main] INFO tc.postgres:16-alpine -- Container postgres:16-alpine is starting: bd86750788af58d508cab04c537e4281fe110f1f10b297d3eed883a08fa7c95b
17:49:32.238 [main] INFO tc.postgres:16-alpine -- Container postgres:16-alpine started in PT2.217571976S
17:49:32.240 [main] INFO tc.postgres:16-alpine -- Container is started (JDBC URL: jdbc:postgresql://localhost:55738/test?loggerLevel=OFF)
17:49:32.546 [main] INFO org.springframework.test.context.support.AnnotationConfigContextLoaderUtils -- Could not detect default configuration classes for test class [com.flashsale.engine.OrderFlowIntegrationTests]: OrderFlowIntegrationTests does not declare any static, non-private, non-final, nested classes annotated with @Configuration.
17:49:32.721 [main] INFO org.springframework.boot.test.context.SpringBootTestContextBootstrapper -- Found @SpringBootConfiguration com.flashsale.engine.FlashSaleEngineApplication for test class com.flashsale.engine.OrderFlowIntegrationTests
17:49:32.819 [main] INFO org.springframework.test.context.support.AnnotationConfigContextLoaderUtils -- Could not detect default configuration classes for test class [com.flashsale.engine.OrderFlowIntegrationTests]: OrderFlowIntegrationTests does not declare any static, non-private, non-final, nested classes annotated with @Configuration.
17:49:32.822 [main] INFO org.springframework.boot.test.context.SpringBootTestContextBootstrapper -- Found @SpringBootConfiguration com.flashsale.engine.FlashSaleEngineApplication for test class com.flashsale.engine.OrderFlowIntegrationTests

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v4.1.1)

2026-10-06T17:49:33.589Z  INFO 28275 --- [flash-sale-engine] [           main] c.f.engine.OrderFlowIntegrationTests     : Starting OrderFlowIntegrationTests using Java 21.0.12.1 with PID 28275 (started by ayussh in /home/ayussh/projects/flash-sale-engine)
2026-10-06T17:49:33.591Z  INFO 28275 --- [flash-sale-engine] [           main] c.f.engine.OrderFlowIntegrationTests     : No active profile set, falling back to 1 default profile: "default"
2026-10-06T17:49:34.250Z  INFO 28275 --- [flash-sale-engine] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Multiple Spring Data modules found, entering strict repository configuration mode
2026-10-06T17:49:34.254Z  INFO 28275 --- [flash-sale-engine] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data JPA repositories in DEFAULT mode.
2026-10-06T17:49:34.433Z  INFO 28275 --- [flash-sale-engine] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 168 ms. Found 2 JPA repository interfaces.
2026-10-06T17:49:34.510Z  INFO 28275 --- [flash-sale-engine] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Multiple Spring Data modules found, entering strict repository configuration mode
2026-10-06T17:49:34.512Z  INFO 28275 --- [flash-sale-engine] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data Redis repositories in DEFAULT mode.
2026-10-06T17:49:34.534Z  INFO 28275 --- [flash-sale-engine] [           main] .RepositoryConfigurationExtensionSupport : Spring Data Redis - Could not safely identify store assignment for repository candidate interface com.flashsale.engine.repository.OrderRepository; If you want this repository to be a Redis repository, consider annotating your entities with one of these annotations: org.springframework.data.redis.core.RedisHash (preferred), or consider extending one of the following types with your repository: org.springframework.data.keyvalue.repository.KeyValueRepository
2026-10-06T17:49:34.534Z  INFO 28275 --- [flash-sale-engine] [           main] .RepositoryConfigurationExtensionSupport : Spring Data Redis - Could not safely identify store assignment for repository candidate interface com.flashsale.engine.repository.ProductRepository; If you want this repository to be a Redis repository, consider annotating your entities with one of these annotations: org.springframework.data.redis.core.RedisHash (preferred), or consider extending one of the following types with your repository: org.springframework.data.keyvalue.repository.KeyValueRepository
2026-10-06T17:49:34.535Z  INFO 28275 --- [flash-sale-engine] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 6 ms. Found 0 Redis repository interfaces.
2026-10-06T17:49:35.222Z  INFO 28275 --- [flash-sale-engine] [           main] o.s.boot.tomcat.TomcatWebServer          : Tomcat initialized with port 0 (http)
2026-10-06T17:49:35.241Z  INFO 28275 --- [flash-sale-engine] [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2026-10-06T17:49:35.241Z  INFO 28275 --- [flash-sale-engine] [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/11.0.24]
2026-10-06T17:49:35.291Z  INFO 28275 --- [flash-sale-engine] [           main] b.w.c.s.WebApplicationContextInitializer : Root WebApplicationContext: initialization completed in 1679 ms
2026-10-06T17:49:35.495Z  INFO 28275 --- [flash-sale-engine] [           main] org.hibernate.orm.jpa                    : HHH008540: Processing PersistenceUnitInfo [name: default]
2026-10-06T17:49:35.586Z  INFO 28275 --- [flash-sale-engine] [           main] org.hibernate.orm.core                   : HHH000001: Hibernate ORM core version 7.4.5.Final
2026-10-06T17:49:36.053Z  INFO 28275 --- [flash-sale-engine] [           main] o.s.o.j.p.SpringPersistenceUnitInfo      : No LoadTimeWeaver setup: ignoring JPA class transformer
2026-10-06T17:49:36.098Z  INFO 28275 --- [flash-sale-engine] [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2026-10-06T17:49:36.341Z  INFO 28275 --- [flash-sale-engine] [           main] com.zaxxer.hikari.pool.HikariPool        : HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@6d2693f
2026-10-06T17:49:36.342Z  INFO 28275 --- [flash-sale-engine] [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2026-10-06T17:49:36.373Z  WARN 28275 --- [flash-sale-engine] [           main] org.hibernate.orm.deprecation            : HHH90000025: PostgreSQLDialect does not need to be specified explicitly using 'hibernate.dialect' (remove the property setting and it will be selected by default)
2026-10-06T17:49:36.393Z  INFO 28275 --- [flash-sale-engine] [           main] org.hibernate.orm.connections.pooling    : HHH10001005: Database info:
	Database JDBC URL [jdbc:postgresql://localhost:55738/test?loggerLevel=OFF]
	Database driver: PostgreSQL JDBC Driver
	Database dialect: PostgreSQLDialect
	Database version: 16.15
	Default catalog/schema: test/public
	Autocommit mode: undefined/unknown
	Isolation level: READ_COMMITTED [default READ_COMMITTED]
	JDBC fetch size: none
	Pool: DataSourceConnectionProvider
	Minimum pool size: undefined/unknown
	Maximum pool size: undefined/unknown
2026-10-06T17:49:37.202Z  INFO 28275 --- [flash-sale-engine] [           main] org.hibernate.orm.core                   : HHH000489: No JTA platform available (set 'hibernate.transaction.jta.platform' to enable JTA platform integration)
2026-10-06T17:49:37.275Z  WARN 28275 --- [flash-sale-engine] [           main] org.hibernate.orm.jdbc.warn              : HHH000247: ErrorCode: 0, SQLState: 00000
2026-10-06T17:49:37.275Z  WARN 28275 --- [flash-sale-engine] [           main] org.hibernate.orm.jdbc.warn              : constraint "ukd1kkvl4hi9hp3peub1umk2xeo" of relation "orders" does not exist, skipping
2026-10-06T17:49:37.278Z  WARN 28275 --- [flash-sale-engine] [           main] org.hibernate.orm.jdbc.warn              : HHH000247: ErrorCode: 0, SQLState: 00000
2026-10-06T17:49:37.278Z  WARN 28275 --- [flash-sale-engine] [           main] org.hibernate.orm.jdbc.warn              : constraint "ukhmsk25beh6atojvle1xuymjj0" of relation "orders" does not exist, skipping
2026-10-06T17:49:37.280Z  INFO 28275 --- [flash-sale-engine] [           main] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'default'
Mockito is currently self-attaching to enable the inline-mock-maker. This will no longer work in future releases of the JDK. Please add Mockito as an agent to your build as described in Mockito's documentation: https://javadoc.io/doc/org.mockito/mockito-core/latest/org.mockito/org/mockito/Mockito.html#0.3
OpenJDK 64-Bit Server VM warning: Sharing is only supported for boot loader classes because bootstrap classpath has been appended
WARNING: A Java agent has been loaded dynamically (/home/ayussh/.m2/repository/net/bytebuddy/byte-buddy-agent/1.18.11/byte-buddy-agent-1.18.11.jar)
WARNING: If a serviceability tool is in use, please run with -XX:+EnableDynamicAgentLoading to hide this warning
WARNING: If a serviceability tool is not in use, please run with -Djdk.instrument.traceUsage for more information
WARNING: Dynamic loading of agents will be disallowed by default in a future release
2026-10-06T17:49:38.850Z  INFO 28275 --- [flash-sale-engine] [           main] o.s.d.j.r.query.QueryEnhancerFactories   : Hibernate is in classpath; If applicable, HQL parser will be used.
2026-10-06T17:49:39.173Z  WARN 28275 --- [flash-sale-engine] [           main] JpaBaseConfiguration$JpaWebConfiguration : spring.jpa.open-in-view is enabled by default. Therefore, database queries may be performed during view rendering. Explicitly configure spring.jpa.open-in-view to disable this warning
2026-10-06T17:49:39.939Z  INFO 28275 --- [flash-sale-engine] [           main] o.s.boot.tomcat.TomcatWebServer          : Tomcat started on port 40785 (http) with context path '/'
2026-10-06T17:49:39.945Z  INFO 28275 --- [flash-sale-engine] [           main] o.s.a.r.c.CachingConnectionFactory       : Attempting to connect to: [localhost:56817]
2026-10-06T17:49:40.020Z  INFO 28275 --- [flash-sale-engine] [           main] o.s.a.r.c.CachingConnectionFactory       : Created new connection: rabbitConnectionFactory#46ae1e6b:0/SimpleConnection@f4e4cb2 [delegate=amqp://guest@127.0.0.1:56817/, localPort=33750]
2026-10-06T17:49:40.150Z  INFO 28275 --- [flash-sale-engine] [           main] c.f.engine.OrderFlowIntegrationTests     : Started OrderFlowIntegrationTests in 7.233 seconds (process running for 24.959)
2026-10-06T17:49:40.932Z  INFO 28275 --- [flash-sale-engine] [o-auto-1-exec-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
2026-10-06T17:49:40.932Z  INFO 28275 --- [flash-sale-engine] [o-auto-1-exec-1] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2026-10-06T17:49:40.933Z  INFO 28275 --- [flash-sale-engine] [o-auto-1-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 1 ms
2026-10-06T17:49:49.781Z  WARN 28275 --- [flash-sale-engine] [ntContainer#1-1] o.s.a.r.retry.RepublishMessageRecoverer  : Republishing failed message to exchange 'order.dlx' with routing key order.failed
2026-10-06T17:49:49.862Z ERROR 28275 --- [flash-sale-engine] [ntContainer#0-1] c.flashsale.engine.consumer.DLQConsumer  : Order processing permanently failed after retries. Stock rollback applied=true. orderId=1ebe375c-0dbc-484b-819a-e68f65dbaccc, productId=101, quantity=1, idempotencyKey=fail-55850836-d2f1-4b0b-80d9-cc72f82a0ae0
2026-10-06T17:49:50.054Z ERROR 28275 --- [flash-sale-engine] [ntContainer#0-1] c.flashsale.engine.consumer.DLQConsumer  : Order processing permanently failed after retries. Stock rollback applied=false. orderId=1ebe375c-0dbc-484b-819a-e68f65dbaccc, productId=101, quantity=1, idempotencyKey=fail-55850836-d2f1-4b0b-80d9-cc72f82a0ae0
2026-10-06T17:49:50.061Z ERROR 28275 --- [flash-sale-engine] [ntContainer#0-1] c.flashsale.engine.consumer.DLQConsumer  : Order processing permanently failed after retries. Stock rollback applied=false. orderId=1ebe375c-0dbc-484b-819a-e68f65dbaccc, productId=101, quantity=1, idempotencyKey=fail-55850836-d2f1-4b0b-80d9-cc72f82a0ae0
2026-10-06T17:49:51.862Z  INFO 28275 --- [flash-sale-engine] [xecutorLoop-1-1] i.l.core.protocol.ConnectionWatchdog     : Reconnecting, last destination was localhost/127.0.0.1:55737
2026-10-06T17:49:51.902Z  WARN 28275 --- [flash-sale-engine] [ioEventLoop-4-2] i.l.core.protocol.ConnectionWatchdog     : Cannot reconnect to [localhost/<unresolved>:55737]: Connection closed prematurely

io.lettuce.core.RedisConnectionException: Connection closed prematurely
	at io.lettuce.core.protocol.RedisHandshakeHandler.channelInactive(RedisHandshakeHandler.java:76) ~[lettuce-core-7.5.2.RELEASE.jar:7.5.2.RELEASE/5728917]
	at io.netty.channel.AbstractChannelHandlerContext.fireChannelInactive(AbstractChannelHandlerContext.java:251) ~[netty-transport-4.2.17.Final.jar:4.2.17.Final]
	at io.netty.channel.ChannelInboundHandlerAdapter.channelInactive(ChannelInboundHandlerAdapter.java:81) ~[netty-transport-4.2.17.Final.jar:4.2.17.Final]
	at io.lettuce.core.ChannelGroupListener.channelInactive(ChannelGroupListener.java:54) ~[lettuce-core-7.5.2.RELEASE.jar:7.5.2.RELEASE/5728917]
	at io.netty.channel.AbstractChannelHandlerContext.fireChannelInactive(AbstractChannelHandlerContext.java:251) ~[netty-transport-4.2.17.Final.jar:4.2.17.Final]
	at io.netty.channel.DefaultChannelPipeline$HeadContext.channelInactive(DefaultChannelPipeline.java:1424) ~[netty-transport-4.2.17.Final.jar:4.2.17.Final]
	at io.netty.channel.DefaultChannelPipeline.fireChannelInactive(DefaultChannelPipeline.java:876) ~[netty-transport-4.2.17.Final.jar:4.2.17.Final]
	at io.netty.channel.AbstractChannel$AbstractUnsafe$6.run(AbstractChannel.java:676) ~[netty-transport-4.2.17.Final.jar:4.2.17.Final]
	at io.netty.util.concurrent.AbstractEventExecutor.runTask(AbstractEventExecutor.java:148) ~[netty-common-4.2.17.Final.jar:4.2.17.Final]
	at io.netty.util.concurrent.AbstractEventExecutor.safeExecute(AbstractEventExecutor.java:141) ~[netty-common-4.2.17.Final.jar:4.2.17.Final]
	at io.netty.util.concurrent.SingleThreadEventExecutor.runAllTasks(SingleThreadEventExecutor.java:535) ~[netty-common-4.2.17.Final.jar:4.2.17.Final]
	at io.netty.channel.SingleThreadIoEventLoop.run(SingleThreadIoEventLoop.java:201) ~[netty-transport-4.2.17.Final.jar:4.2.17.Final]
	at io.netty.util.concurrent.SingleThreadEventExecutor$5.run(SingleThreadEventExecutor.java:1204) ~[netty-common-4.2.17.Final.jar:4.2.17.Final]
	at io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74) ~[netty-common-4.2.17.Final.jar:4.2.17.Final]
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30) ~[netty-common-4.2.17.Final.jar:4.2.17.Final]
	at java.base/java.lang.Thread.run(Thread.java:1583) ~[na:na]

2026-10-06T17:49:52.792Z  WARN 28275 --- [flash-sale-engine] [ntContainer#1-1] o.s.a.r.l.SimpleMessageListenerContainer : Consumer raised exception, processing can restart if the connection factory supports it

com.rabbitmq.client.ShutdownSignalException: connection error
	at com.rabbitmq.client.impl.AMQConnection.startShutdown(AMQConnection.java:1014) ~[amqp-client-5.30.0.jar:5.30.0]
	at com.rabbitmq.client.impl.AMQConnection.shutdown(AMQConnection.java:1004) ~[amqp-client-5.30.0.jar:5.30.0]
	at com.rabbitmq.client.impl.AMQConnection.handleFailure(AMQConnection.java:804) ~[amqp-client-5.30.0.jar:5.30.0]
	at com.rabbitmq.client.impl.AMQConnection.access$500(AMQConnection.java:49) ~[amqp-client-5.30.0.jar:5.30.0]
	at com.rabbitmq.client.impl.AMQConnection$MainLoop.run(AMQConnection.java:703) ~[amqp-client-5.30.0.jar:5.30.0]
	at java.base/java.lang.Thread.run(Thread.java:1583) ~[na:na]
Caused by: java.io.EOFException
	at java.base/java.io.DataInputStream.readUnsignedByte(DataInputStream.java:297) ~[na:na]
	at com.rabbitmq.client.impl.Frame.readFrom(Frame.java:92) ~[amqp-client-5.30.0.jar:5.30.0]
	at com.rabbitmq.client.impl.SocketFrameHandler.readFrame(SocketFrameHandler.java:199) ~[amqp-client-5.30.0.jar:5.30.0]
	at com.rabbitmq.client.impl.AMQConnection$MainLoop.run(AMQConnection.java:694) ~[amqp-client-5.30.0.jar:5.30.0]
	... 1 common frames omitted

2026-10-06T17:49:52.811Z  INFO 28275 --- [flash-sale-engine] [ntContainer#1-1] o.s.a.r.l.SimpleMessageListenerContainer : Restarting Consumer@37994ff: tags=[[amq.ctag-OlLr2iui4IqXoJc5cZj-NQ]], channel=Cached Rabbit Channel: AMQChannel(amqp://guest@127.0.0.1:56817/,2), conn: Proxy@6bb37a5 Shared Rabbit Connection: SimpleConnection@f4e4cb2 [delegate=amqp://guest@127.0.0.1:56817/, localPort=33750], acknowledgeMode=AUTO local queue size=0
2026-10-06T17:49:52.836Z  INFO 28275 --- [flash-sale-engine] [ntContainer#1-2] o.s.a.r.c.CachingConnectionFactory       : Attempting to connect to: [localhost:56817]
2026-10-06T17:49:52.849Z ERROR 28275 --- [flash-sale-engine] [ntContainer#1-2] o.s.a.r.l.SimpleMessageListenerContainer : Failed to check/redeclare auto-delete queue(s).

org.springframework.amqp.AmqpConnectException: java.net.ConnectException: Connection refused
	at org.springframework.amqp.rabbit.support.RabbitExceptionTranslator.convertRabbitAccessException(RabbitExceptionTranslator.java:60) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.connection.AbstractConnectionFactory.createBareConnection(AbstractConnectionFactory.java:629) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.connection.CachingConnectionFactory.createConnection(CachingConnectionFactory.java:746) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.connection.ConnectionFactoryUtils.createConnection(ConnectionFactoryUtils.java:256) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.core.RabbitTemplate.doExecute(RabbitTemplate.java:2239) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.core.RabbitTemplate.execute(RabbitTemplate.java:2211) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.core.RabbitTemplate.execute(RabbitTemplate.java:2191) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.core.RabbitAdmin.getQueueInfo(RabbitAdmin.java:472) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.core.RabbitAdmin.getQueueProperties(RabbitAdmin.java:456) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer.attemptDeclarations(AbstractMessageListenerContainer.java:1978) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer.redeclareElementsIfNecessary(AbstractMessageListenerContainer.java:1941) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer$AsyncMessageProcessingConsumer.initialize(SimpleMessageListenerContainer.java:1602) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer$AsyncMessageProcessingConsumer.run(SimpleMessageListenerContainer.java:1400) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at java.base/java.lang.Thread.run(Thread.java:1583) ~[na:na]
Caused by: java.net.ConnectException: Connection refused
	at java.base/sun.nio.ch.Net.pollConnect(Native Method) ~[na:na]
	at java.base/sun.nio.ch.Net.pollConnectNow(Net.java:694) ~[na:na]
	at java.base/sun.nio.ch.NioSocketImpl.timedFinishConnect(NioSocketImpl.java:542) ~[na:na]
	at java.base/sun.nio.ch.NioSocketImpl.connect(NioSocketImpl.java:592) ~[na:na]
	at java.base/java.net.SocksSocketImpl.connect(SocksSocketImpl.java:327) ~[na:na]
	at java.base/java.net.Socket.connect(Socket.java:751) ~[na:na]
	at com.rabbitmq.client.impl.SocketFrameHandlerFactory.create(SocketFrameHandlerFactory.java:61) ~[amqp-client-5.30.0.jar:5.30.0]
	at com.rabbitmq.client.ConnectionFactory.newConnection(ConnectionFactory.java:1355) ~[amqp-client-5.30.0.jar:5.30.0]
	at com.rabbitmq.client.ConnectionFactory.newConnection(ConnectionFactory.java:1297) ~[amqp-client-5.30.0.jar:5.30.0]
	at org.springframework.amqp.rabbit.connection.AbstractConnectionFactory.connectAddresses(AbstractConnectionFactory.java:676) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.connection.AbstractConnectionFactory.connect(AbstractConnectionFactory.java:644) ~[spring-rabbit-4.1.1.jar:4.1.1]
	at org.springframework.amqp.rabbit.connection.AbstractConnectionFactory.createBareConnection(AbstractConnectionFactory.java:590) ~[spring-rabbit-4.1.1.jar:4.1.1]
	... 12 common frames omitted

2026-10-06T17:49:52.857Z  INFO 28275 --- [flash-sale-engine] [ntContainer#1-2] o.s.a.r.c.CachingConnectionFactory       : Attempting to connect to: [localhost:56817]
```

## k6 load test

Commands run:

```bash
docker compose up -d
./mvnw spring-boot:run > /tmp/flash-sale-app.log 2>&1
k6 inspect scripts/load_test.js
k6 run scripts/load_test.js > /tmp/flash-sale-k6-final.log 2>&1; code=$?; tail -45 /tmp/flash-sale-k6-final.log; exit "$code"
```

Final k6 exit code: 0. Scenario ramps 0 → 20 → 100 → 0 VUs over 2 minutes, with 100000 initial stock and unique keys per run. `accepted_orders` rate counts HTTP 202 only. HTTP 409 is an expected sold-out response; it does not count as an HTTP error. Raw final k6 summary:

```text
  █ THRESHOLDS

    http_req_duration
    ✓ 'p(99)<1000' p(99)=21.15ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS

    CUSTOM
    accepted_orders................: 51385 427.793126/s
    unexpected_responses...........: 0.00% 0 out of 51385

    HTTP
    http_req_duration..............: avg=3.68ms   min=1.16ms   med=2.69ms   max=333.35ms p(90)=4.2ms    p(95)=5.63ms
      { expected_response:true }...: avg=3.68ms   min=1.16ms   med=2.69ms   max=333.35ms p(90)=4.2ms    p(95)=5.63ms
    http_req_failed................: 0.00% 0 out of 51386
    http_reqs......................: 51386 427.801451/s

    EXECUTION
    iteration_duration.............: avg=104.58ms min=101.44ms med=103.45ms max=434.45ms p(90)=105.05ms p(95)=106.66ms
    iterations.....................: 51385 427.793126/s
    vus............................: 1     min=0          max=99
    vus_max........................: 100   min=100        max=100

    NETWORK
    data_received..................: 12 MB 101 kB/s
    data_sent......................: 11 MB 94 kB/s




running (2m00.1s), 000/100 VUs, 51385 complete and 0 interrupted iterations
orders ✓ [ 100% ] 000/100 VUs  2m0s
```

## Earlier failed runs and correction

- Initial `./mvnw test -q` failed: `Connection to localhost:5432 refused` because local PostgreSQL was stopped.
- After adding Testcontainers, `./mvnw test -q` failed: `Could not find a valid Docker environment`. Docker Desktop was started; the same test command then passed.
- First `k6 run scripts/load_test.js` failed during setup: `connect: connection refused` at `localhost:8080`. Compose services and Spring Boot were started.
- First live k6 run exhausted stock early: 10000 accepted; built-in `http_req_failed` showed 98.26% because it classified expected HTTP 409 responses as errors. Script now classifies HTTP 409 as expected and uses a fresh key prefix per run. Final run above passed both error-rate thresholds.

## Post-load queue drain and persistence

Commands run after k6 finished:

```bash
docker exec flash_rabbitmq rabbitmqctl list_queues name messages_ready messages_unacknowledged
docker exec flash_postgres psql -U postgres -d flashsale_db -tAc "select count(*) from orders where idempotency_key ~ '^k6-[0-9]{13}-[0-9]+-[0-9]+-[0-9]+$' and status='CONFIRMED'"
docker exec flash_redis redis-cli GET product:101:stock
```

Raw output:

```text
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
51385
48615
```

All 51385 orders accepted in final run were CONFIRMED after queue drain; 100000 - 51385 = 48615 Redis stock.
