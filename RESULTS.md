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

## Measured baseline for distributed/fault-tolerance work

All measurements below were executed in this session. Load generator and system share one laptop. Baseline revision: `fcc43e010a672b8a3c52a9bc7a6db381f11768b5`. Existing comment-only RabbitMQConfig diff was present before work, recorded in `bench/results/baseline/preexisting.diff`, and is excluded from commits. No source was edited before this baseline.

Commands: `JAVA_TOOL_OPTIONS=-Xmx256m ./mvnw test`; `docker compose up -d`; `docker update --memory 384m --memory-swap 384m flash_postgres flash_redis flash_rabbitmq`; `JAVA_TOOL_OPTIONS=-Xmx256m ./mvnw package -DskipTests`; `JAVA_TOOL_OPTIONS=-Xmx256m java -jar target/flash-sale-engine-0.0.1-SNAPSHOT.jar`; `python3 bench/baseline.py`. The existing `scripts/load_test.js` was unchanged. Every run uses clean orders and Redis state, then waits for ready/unacknowledged queues to reach zero.

Raw environment:

```text
2026-10-09T07:37:37+00:00
fcc43e010a672b8a3c52a9bc7a6db381f11768b5
 M src/main/java/com/flashsale/engine/config/RabbitMQConfig.java
?? bench/
Linux DESKTOP-P6C785V 6.18.40.1-microsoft-standard-WSL2 #1 SMP PREEMPT_DYNAMIC Fri Jul 31 22:12:15 UTC 2026 x86_64 GNU/Linux
PRETTY_NAME="Debian GNU/Linux 13 (trixie)"
NAME="Debian GNU/Linux"
VERSION_ID="13"
VERSION="13 (trixie)"
VERSION_CODENAME=trixie
DEBIAN_VERSION_FULL=13.4
ID=debian
HOME_URL="https://www.debian.org/"
SUPPORT_URL="https://www.debian.org/support"
BUG_REPORT_URL="https://bugs.debian.org/"
               total        used        free      shared  buff/cache   available
Mem:      5157515264  1369636864  2064973824    53964800  1946628096  3787878400
Swap:     4294967296           0  4294967296
Architecture:                            x86_64
CPU op-mode(s):                          32-bit, 64-bit
Address sizes:                           48 bits physical, 48 bits virtual
Byte Order:                              Little Endian
CPU(s):                                  16
On-line CPU(s) list:                     0-15
Vendor ID:                               AuthenticAMD
Model name:                              AMD Ryzen 7 7840HS w/ Radeon 780M Graphics
CPU family:                              25
Model:                                   116
Thread(s) per core:                      2
Core(s) per socket:                      8
Socket(s):                               1
Stepping:                                1
BogoMIPS:                                7585.87
Flags:                                   fpu vme de pse tsc msr pae mce cx8 apic sep mtrr pge mca cmov pat pse36 clflush mmx fxsr sse sse2 ht syscall nx mmxext fxsr_opt pdpe1gb rdtscp lm constant_tsc rep_good nopl xtopology tsc_reliable nonstop_tsc cpuid extd_apicid tsc_known_freq pni pclmulqdq ssse3 fma cx16 sse4_1 sse4_2 movbe popcnt aes xsave avx f16c rdrand hypervisor lahf_lm cmp_legacy svm cr8_legacy abm sse4a misalignsse 3dnowprefetch osvw topoext perfctr_core ssbd ibrs ibpb stibp vmmcall fsgsbase bmi1 avx2 smep bmi2 erms invpcid avx512f avx512dq rdseed adx smap avx512ifma clflushopt clwb avx512cd sha_ni avx512bw avx512vl xsaveopt xsavec xgetbv1 xsaves avx512_bf16 clzero xsaveerptr arat npt nrip_save tsc_scale vmcb_clean flushbyasid decodeassists pausefilter pfthreshold avx512vbmi umip avx512_vbmi2 gfni vaes vpclmulqdq avx512_vnni avx512_bitalg avx512_vpopcntdq rdpid fsrm
Virtualization:                          AMD-V
Hypervisor vendor:                       Microsoft
Virtualization type:                     full
L1d cache:                               256 KiB (8 instances)
L1i cache:                               256 KiB (8 instances)
L2 cache:                                8 MiB (8 instances)
L3 cache:                                16 MiB (1 instance)
NUMA node(s):                            1
NUMA node0 CPU(s):                       0-15
Vulnerability Gather data sampling:      Not affected
Vulnerability Ghostwrite:                Not affected
Vulnerability Indirect target selection: Not affected
Vulnerability Itlb multihit:             Not affected
Vulnerability L1tf:                      Not affected
Vulnerability Mds:                       Not affected
Vulnerability Meltdown:                  Not affected
Vulnerability Mmio stale data:           Not affected
Vulnerability Old microcode:             Not affected
Vulnerability Reg file data sampling:    Not affected
Vulnerability Retbleed:                  Not affected
Vulnerability Spec rstack overflow:      Vulnerable: Safe RET, no microcode
Vulnerability Spec store bypass:         Mitigation; Speculative Store Bypass disabled via prctl
Vulnerability Spectre v1:                Mitigation; usercopy/swapgs barriers and __user pointer sanitization
Vulnerability Spectre v2:                Mitigation; Retpolines; IBPB conditional; IBRS_FW; STIBP always-on; RSB filling; PBRSB-eIBRS Not affected; BHI Not affected
Vulnerability Srbds:                     Not affected
Vulnerability Tsa:                       Vulnerable: No microcode
Vulnerability Tsx async abort:           Not affected
Vulnerability Vmscape:                   Not affected
openjdk version "21.0.12.1" 2026-08-18
OpenJDK Runtime Environment (build 21.0.12.1+1-1-deb13u1-Debian)
OpenJDK 64-Bit Server VM (build 21.0.12.1+1-1-deb13u1-Debian, mixed mode, sharing)
Client:
 Version:           29.8.0
 API version:       1.56
 Go version:        go1.26.8
 Git commit:        88096ef
 Built:             Thu Sep  3 21:49:51 2026
 OS/Arch:           linux/amd64
 Context:           default

Server: Docker Desktop 4.91.0 (239619)
 Engine:
  Version:          29.8.0
  API version:      1.56 (minimum version 1.40)
  Go version:       go1.26.8
  Git commit:       3ce5872
  Built:            Thu Sep  3 21:51:20 2026
  OS/Arch:          linux/amd64
  Experimental:     false
 containerd:
  Version:          v2.3.4
  GitCommit:        db8809540e1a7a9da5d518876894933ff55692ab
 runc:
  Version:          1.4.3
  GitCommit:        v1.4.3-0-gbb14dabe
 docker-init:
  Version:          0.19.0
  GitCommit:        de40ad0
Docker Compose version v5.5.1
k6 v2.2.0 (commit/00a9a1b7f5, go1.26.5, linux/amd64)
```

Host app has capped heap but no container memory limit. Raw JVM flags/RSS: [jvm.txt](bench/results/baseline/jvm.txt). Dependencies have memory limits; [limits.txt](bench/results/baseline/limits.txt). Live stats started during the first run; sampling coverage is partial there: [live-stats.log](bench/results/baseline/live-stats.log). Initial Docker-unavailable attempt and recovery: [raw failure](bench/results/baseline/docker-initial-failure.txt). No benchmark was run with Docker unavailable.

Executed test summary:

```text
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 30.27 s -- in com.flashsale.engine.OrderFlowIntegrationTests
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

| Run | HTTP 202 | req/s | p50 ms | p95 ms | p99 ms | error rate | CONFIRMED | Redis stock |
|---|---|---|---|---|---|---|---|---|
| run-1 | 52079 | 433.86762161230314 | 2.273946 | 5.323319649999997 | 7.7905503099999995 | 0 | 52079 | 47921 |
| run-2 | 52168 | 434.51179425973214 | 2.133507 | 4.724691 | 7.137312359999998 | 0 | 52168 | 47832 |
| run-3 | 52180 | 434.630519918471 | 2.167679 | 4.307293 | 6.181404799999997 | 0 | 52180 | 47820 |

### run-1 raw output

```text
  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=7.79ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 52079 433.859291/s
    unexpected_responses...........: 0.00% 0 out of 52079

    HTTP
    http_req_duration..............: avg=2.64ms   min=751.43µs med=2.27ms   max=506.89ms p(90)=4.38ms   p(95)=5.32ms   p(99)=7.79ms  
      { expected_response:true }...: avg=2.64ms   min=751.43µs med=2.27ms   max=506.89ms p(90)=4.38ms   p(95)=5.32ms   p(99)=7.79ms  
    http_req_failed................: 0.00% 0 out of 52080
    http_reqs......................: 52080 433.867622/s

    EXECUTION
    iteration_duration.............: avg=103.19ms min=100.89ms med=102.85ms max=610.03ms p(90)=105.05ms p(95)=106.02ms p(99)=108.56ms
    iterations.....................: 52079 433.859291/s
    vus............................: 1     min=0          max=99 
    vus_max........................: 100   min=100        max=100

    NETWORK
    data_received..................: 12 MB 103 kB/s
    data_sent......................: 12 MB 96 kB/s




running (2m00.0s), 000/100 VUs, 52079 complete and 0 interrupted iterations
orders ✓ [ 100% ] 000/100 VUs  2m0s

exit_code=0
```

```text
$ docker exec flash_rabbitmq rabbitmqctl list_queues name messages_ready messages_unacknowledged
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0

exit_code=0
$ docker exec flash_postgres psql -U postgres -d flashsale_db -tAc select count(*), count(distinct idempotency_key), coalesce(sum(quantity),0) from orders where status='CONFIRMED'
52079|52079|52079

exit_code=0
$ docker exec flash_redis redis-cli GET product:101:stock
47921

exit_code=0
```

### run-2 raw output

```text
  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=7.13ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 52168 434.503465/s
    unexpected_responses...........: 0.00% 0 out of 52168

    HTTP
    http_req_duration..............: avg=2.44ms   min=764.55µs med=2.13ms   max=57.36ms  p(90)=3.77ms   p(95)=4.72ms   p(99)=7.13ms  
      { expected_response:true }...: avg=2.44ms   min=764.55µs med=2.13ms   max=57.36ms  p(90)=3.77ms   p(95)=4.72ms   p(99)=7.13ms  
    http_req_failed................: 0.00% 0 out of 52169
    http_reqs......................: 52169 434.511794/s

    EXECUTION
    iteration_duration.............: avg=103.01ms min=100.94ms med=102.75ms max=157.78ms p(90)=104.39ms p(95)=105.29ms p(99)=107.69ms
    iterations.....................: 52168 434.503465/s
    vus............................: 1     min=0          max=99 
    vus_max........................: 100   min=100        max=100

    NETWORK
    data_received..................: 12 MB 103 kB/s
    data_sent......................: 12 MB 96 kB/s




running (2m00.1s), 000/100 VUs, 52168 complete and 0 interrupted iterations
orders ✓ [ 100% ] 000/100 VUs  2m0s

exit_code=0
```

```text
$ docker exec flash_rabbitmq rabbitmqctl list_queues name messages_ready messages_unacknowledged
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0

exit_code=0
$ docker exec flash_postgres psql -U postgres -d flashsale_db -tAc select count(*), count(distinct idempotency_key), coalesce(sum(quantity),0) from orders where status='CONFIRMED'
52168|52168|52168

exit_code=0
$ docker exec flash_redis redis-cli GET product:101:stock
47832

exit_code=0
```

### run-3 raw output

```text
  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=6.18ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 52180 434.622191/s
    unexpected_responses...........: 0.00% 0 out of 52180

    HTTP
    http_req_duration..............: avg=2.39ms   min=727.79µs med=2.16ms  max=13.36ms  p(90)=3.57ms  p(95)=4.3ms   p(99)=6.18ms  
      { expected_response:true }...: avg=2.39ms   min=727.79µs med=2.16ms  max=13.36ms  p(90)=3.57ms  p(95)=4.3ms   p(99)=6.18ms  
    http_req_failed................: 0.00% 0 out of 52181
    http_reqs......................: 52181 434.63052/s

    EXECUTION
    iteration_duration.............: avg=102.98ms min=100.97ms med=102.8ms max=128.81ms p(90)=104.2ms p(95)=104.9ms p(99)=106.84ms
    iterations.....................: 52180 434.622191/s
    vus............................: 1     min=0          max=99 
    vus_max........................: 100   min=100        max=100

    NETWORK
    data_received..................: 12 MB 103 kB/s
    data_sent......................: 12 MB 96 kB/s




running (2m00.1s), 000/100 VUs, 52180 complete and 0 interrupted iterations
orders ✓ [ 100% ] 000/100 VUs  2m0s

exit_code=0
```

```text
$ docker exec flash_rabbitmq rabbitmqctl list_queues name messages_ready messages_unacknowledged
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0

exit_code=0
$ docker exec flash_postgres psql -U postgres -d flashsale_db -tAc select count(*), count(distinct idempotency_key), coalesce(sum(quantity),0) from orders where status='CONFIRMED'
52180|52180|52180

exit_code=0
$ docker exec flash_redis redis-cli GET product:101:stock
47820

exit_code=0
```

All baseline accepted counts equal CONFIRMED counts; stock conservation holds. Duplicate-key counts are in each raw Postgres output. OOMKilled is false in captured Docker inspect data; captured dependency cgroup snapshots show no OOM kills or CPU throttling. Host app JVM RSS and WSL memory are recorded; host/JVM throttling was not instrumented. Full raw output, including test shutdown warnings: [baseline directory](bench/results/baseline/). Existing RESULTS.md sections above are historical and are not evidence for this task.

## Multi-replica deployment and HTTP concurrency

Executed `JAVA_TOOL_OPTIONS=-Xmx256m ./mvnw test` after adding deployment, then again after capping Testcontainers. Original assertions unchanged. Both executions passed; full logs in [replicas](bench/results/replicas/).

```text
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 36.84 s -- in com.flashsale.engine.OrderFlowIntegrationTests
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

Executed `docker compose --profile distributed up -d --build --scale app=2` and `python3 bench/http_concurrency.py`. This script releases concurrent tasks against nginx, records each HTTP status/upstream, waits for persistence and queue drain, asserts accepted/CONFIRMED/distinct-key counts and remaining stock.

Initial failed burst, with proxy connect timeout before tuning:

```text
{'requests': 2000, 'initial_stock': 100, 'statuses': {409: 1859, 202: 100, 502: 41}, 'postgres': '100|100|100', 'redis_stock': 0}
Traceback (most recent call last):
  File "/home/ayussh/projects/flash-sale-engine/bench/http_concurrency.py", line 37, in <module>
    assert set(counts)<=({200,202} if same else {202,409}),summary
           ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
AssertionError: {'requests': 2000, 'initial_stock': 100, 'statuses': {409: 1859, 202: 100, 502: 41}, 'postgres': '100|100|100', 'redis_stock': 0}
exit_code=1
```

nginx logged `upstream timed out (110: Operation timed out) while connecting to upstream`; full [nginx output](bench/results/replicas/nginx-http.log). Increased connect timeout, retaining all HTTP assertions. Successful attempt without explicit queue/upstream assertions remains in [http-attempt-2](bench/results/replicas/http-attempt-2/). Final run with those assertions:

```text
{'requests': 2000, 'initial_stock': 100, 'statuses': {202: 100, 409: 1900}, 'upstreams': {'172.18.0.5:8080': 1000, '172.18.0.6:8080': 1000}, 'postgres': '100|100|100', 'redis_stock': 0}
{'requests': 40, 'initial_stock': 10, 'statuses': {200: 39, 202: 1}, 'upstreams': {'172.18.0.6:8080': 20, '172.18.0.5:8080': 20}, 'postgres': '1|1|1', 'redis_stock': 9}
HTTP assertions passed
exit_code=0
```

Per-response evidence and queue snapshots: [HTTP raw results](bench/results/replicas/http/). Docker image IDs and in-container Java version: [images](bench/results/replicas/images.json), [Java](bench/results/replicas/java.txt). Host baseline Java and image Java are different distributions; use the replicated runs for matched scaling/confirm comparisons rather than treating host baseline as a controlled container comparison. Container resource limits and app JVM heap are constant across replicated comparisons. Testcontainers memory caps were added after baseline; baseline tests used their existing uncapped infrastructure containers.

## Phase A: replicated k6 ramps

Each row is an executed run. Error rate is k6 HTTP failure fraction; expected sold-out/idempotent statuses do not count as failures. Latencies are milliseconds, from full-precision k6 exports. Threshold failures remain in the table and raw files.

| Run | req/s | p50 | p95 | p99 | errors | HTTP 202 | CONFIRMED | stock | oversold | lost | duplicates | stuck | k6 exit |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| r1-run-1 | 429.93907653071693 | 3.3375595000000002 | 6.189688299999999 | 8.68396245 | 0 | 51593 | 51593 | 48407 | 0 | 0 | 0 | 0 | 0 |
| r1-run-2 | 430.3156403469725 | 3.217983 | 6.3168050000000004 | 8.7737525 | 0 | 51650 | 51650 | 48350 | 0 | 0 | 0 | 0 | 0 |
| r1-run-3 | 429.94684061574856 | 3.322792 | 6.1541383 | 8.281102059999998 | 0 | 51618 | 51618 | 48382 | 0 | 0 | 0 | 0 | 0 |
| r2-run-1 | 426.9330053557005 | 3.4913435 | 6.5029985 | 8.9240443 | 0 | 51495 | 51495 | 48505 | 0 | 0 | 0 | 0 | 0 |
| r2-run-2 | 429.1972420560015 | 3.506063 | 6.3572945 | 8.33397190000001 | 0 | 51510 | 51510 | 48490 | 0 | 0 | 0 | 0 | 0 |
| r2-run-3 | 428.9298256551053 | 3.540799 | 6.5604379999999995 | 8.474353880000013 | 0 | 51488 | 51488 | 48512 | 0 | 0 | 0 | 0 | 0 |
| r3-run-1 | 425.05837109113247 | 3.6753715 | 7.005737949999995 | 10.016573759999968 | 0 | 51337 | 51337 | 48663 | 0 | 0 | 0 | 0 | 0 |
| r3-run-2 | 428.9040413870723 | 3.3403215 | 6.514284549999994 | 10.650055580000007 | 0 | 51481 | 51481 | 48519 | 0 | 0 | 0 | 0 | 0 |
| r3-run-3 | 428.40369484545056 | 3.4626695 | 6.687611899999997 | 9.88536535 | 0 | 51431 | 51431 | 48569 | 0 | 0 | 0 | 0 | 0 |

Maximum observed per-container snapshots (Docker display precision; not guaranteed peaks), generated by executed `python3 bench/summarize.py bench/results/replicas`:

```text
{"run": "r1-run-1", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "23.30%", "max_observed_memory": "13.24MiB / 64MiB"}
{"run": "r1-run-1", "container": "flash-sale-engine-app-1", "max_observed_cpu": "101.82%", "max_observed_memory": "447.2MiB / 640MiB"}
{"run": "r1-run-1", "container": "flash_rabbitmq", "max_observed_cpu": "139.56%", "max_observed_memory": "267.2MiB / 384MiB"}
{"run": "r1-run-1", "container": "flash_redis", "max_observed_cpu": "7.29%", "max_observed_memory": "23.53MiB / 384MiB"}
{"run": "r1-run-1", "container": "flash_postgres", "max_observed_cpu": "39.45%", "max_observed_memory": "65.7MiB / 384MiB"}
{"run": "r1-run-2", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "20.99%", "max_observed_memory": "13.34MiB / 64MiB"}
{"run": "r1-run-2", "container": "flash-sale-engine-app-1", "max_observed_cpu": "82.60%", "max_observed_memory": "447.2MiB / 640MiB"}
{"run": "r1-run-2", "container": "flash_rabbitmq", "max_observed_cpu": "222.58%", "max_observed_memory": "217.3MiB / 384MiB"}
{"run": "r1-run-2", "container": "flash_redis", "max_observed_cpu": "9.40%", "max_observed_memory": "22.85MiB / 384MiB"}
{"run": "r1-run-2", "container": "flash_postgres", "max_observed_cpu": "37.82%", "max_observed_memory": "68.53MiB / 384MiB"}
{"run": "r1-run-3", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "20.76%", "max_observed_memory": "13.43MiB / 64MiB"}
{"run": "r1-run-3", "container": "flash-sale-engine-app-1", "max_observed_cpu": "81.69%", "max_observed_memory": "447.6MiB / 640MiB"}
{"run": "r1-run-3", "container": "flash_rabbitmq", "max_observed_cpu": "192.05%", "max_observed_memory": "270.8MiB / 384MiB"}
{"run": "r1-run-3", "container": "flash_redis", "max_observed_cpu": "6.96%", "max_observed_memory": "23.8MiB / 384MiB"}
{"run": "r1-run-3", "container": "flash_postgres", "max_observed_cpu": "42.46%", "max_observed_memory": "69.36MiB / 384MiB"}
{"run": "r2-run-1", "container": "flash-sale-engine-app-2", "max_observed_cpu": "146.29%", "max_observed_memory": "311.9MiB / 640MiB"}
{"run": "r2-run-1", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "22.09%", "max_observed_memory": "14.45MiB / 64MiB"}
{"run": "r2-run-1", "container": "flash-sale-engine-app-1", "max_observed_cpu": "55.67%", "max_observed_memory": "447.5MiB / 640MiB"}
{"run": "r2-run-1", "container": "flash_rabbitmq", "max_observed_cpu": "203.34%", "max_observed_memory": "269.8MiB / 384MiB"}
{"run": "r2-run-1", "container": "flash_redis", "max_observed_cpu": "6.37%", "max_observed_memory": "22.87MiB / 384MiB"}
{"run": "r2-run-1", "container": "flash_postgres", "max_observed_cpu": "50.11%", "max_observed_memory": "88.84MiB / 384MiB"}
{"run": "r2-run-2", "container": "flash-sale-engine-app-2", "max_observed_cpu": "59.42%", "max_observed_memory": "318MiB / 640MiB"}
{"run": "r2-run-2", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "23.77%", "max_observed_memory": "13.79MiB / 64MiB"}
{"run": "r2-run-2", "container": "flash-sale-engine-app-1", "max_observed_cpu": "57.00%", "max_observed_memory": "447.8MiB / 640MiB"}
{"run": "r2-run-2", "container": "flash_rabbitmq", "max_observed_cpu": "186.77%", "max_observed_memory": "261.9MiB / 384MiB"}
{"run": "r2-run-2", "container": "flash_redis", "max_observed_cpu": "7.05%", "max_observed_memory": "22.42MiB / 384MiB"}
{"run": "r2-run-2", "container": "flash_postgres", "max_observed_cpu": "52.93%", "max_observed_memory": "89.41MiB / 384MiB"}
{"run": "r2-run-3", "container": "flash-sale-engine-app-2", "max_observed_cpu": "65.08%", "max_observed_memory": "317.6MiB / 640MiB"}
{"run": "r2-run-3", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "26.10%", "max_observed_memory": "13.71MiB / 64MiB"}
{"run": "r2-run-3", "container": "flash-sale-engine-app-1", "max_observed_cpu": "67.40%", "max_observed_memory": "447.6MiB / 640MiB"}
{"run": "r2-run-3", "container": "flash_rabbitmq", "max_observed_cpu": "178.67%", "max_observed_memory": "253.6MiB / 384MiB"}
{"run": "r2-run-3", "container": "flash_redis", "max_observed_cpu": "8.73%", "max_observed_memory": "22.34MiB / 384MiB"}
{"run": "r2-run-3", "container": "flash_postgres", "max_observed_cpu": "73.22%", "max_observed_memory": "89.07MiB / 384MiB"}
{"run": "r3-run-1", "container": "flash-sale-engine-app-3", "max_observed_cpu": "126.97%", "max_observed_memory": "314.1MiB / 640MiB"}
{"run": "r3-run-1", "container": "flash-sale-engine-app-2", "max_observed_cpu": "48.93%", "max_observed_memory": "318.3MiB / 640MiB"}
{"run": "r3-run-1", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "26.98%", "max_observed_memory": "13.55MiB / 64MiB"}
{"run": "r3-run-1", "container": "flash-sale-engine-app-1", "max_observed_cpu": "51.29%", "max_observed_memory": "446MiB / 640MiB"}
{"run": "r3-run-1", "container": "flash_rabbitmq", "max_observed_cpu": "215.84%", "max_observed_memory": "256.6MiB / 384MiB"}
{"run": "r3-run-1", "container": "flash_redis", "max_observed_cpu": "13.07%", "max_observed_memory": "23.74MiB / 384MiB"}
{"run": "r3-run-1", "container": "flash_postgres", "max_observed_cpu": "98.20%", "max_observed_memory": "106.2MiB / 384MiB"}
{"run": "r3-run-2", "container": "flash-sale-engine-app-3", "max_observed_cpu": "58.90%", "max_observed_memory": "318.2MiB / 640MiB"}
{"run": "r3-run-2", "container": "flash-sale-engine-app-2", "max_observed_cpu": "52.90%", "max_observed_memory": "319.2MiB / 640MiB"}
{"run": "r3-run-2", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "29.73%", "max_observed_memory": "12.04MiB / 64MiB"}
{"run": "r3-run-2", "container": "flash-sale-engine-app-1", "max_observed_cpu": "53.94%", "max_observed_memory": "446.2MiB / 640MiB"}
{"run": "r3-run-2", "container": "flash_rabbitmq", "max_observed_cpu": "210.31%", "max_observed_memory": "267.7MiB / 384MiB"}
{"run": "r3-run-2", "container": "flash_redis", "max_observed_cpu": "10.58%", "max_observed_memory": "23.16MiB / 384MiB"}
{"run": "r3-run-2", "container": "flash_postgres", "max_observed_cpu": "57.05%", "max_observed_memory": "109.1MiB / 384MiB"}
{"run": "r3-run-3", "container": "flash-sale-engine-app-3", "max_observed_cpu": "47.39%", "max_observed_memory": "318.7MiB / 640MiB"}
{"run": "r3-run-3", "container": "flash-sale-engine-app-2", "max_observed_cpu": "46.07%", "max_observed_memory": "319.5MiB / 640MiB"}
{"run": "r3-run-3", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "27.65%", "max_observed_memory": "12.57MiB / 64MiB"}
{"run": "r3-run-3", "container": "flash-sale-engine-app-1", "max_observed_cpu": "47.27%", "max_observed_memory": "445.9MiB / 640MiB"}
{"run": "r3-run-3", "container": "flash_rabbitmq", "max_observed_cpu": "202.54%", "max_observed_memory": "262.6MiB / 384MiB"}
{"run": "r3-run-3", "container": "flash_redis", "max_observed_cpu": "8.53%", "max_observed_memory": "22.98MiB / 384MiB"}
{"run": "r3-run-3", "container": "flash_postgres", "max_observed_cpu": "55.82%", "max_observed_memory": "113.6MiB / 384MiB"}
```

### r1-run-1 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [r1-run-1](bench/results/replicas/r1-run-1/).

```text
$ git rev-parse HEAD
962bbf2943bb18d76298a47893ac866a14c47fd2

exit_code=0

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=8.68ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 51593 429.930743/s
    unexpected_responses...........: 0.00% 0 out of 51593

    HTTP
    http_req_duration..............: avg=3.59ms   min=937.83µs med=3.33ms   max=15.94ms  p(90)=5.33ms   p(95)=6.18ms   p(99)=8.68ms  
      { expected_response:true }...: avg=3.59ms   min=937.83µs med=3.33ms   max=15.94ms  p(90)=5.33ms   p(95)=6.18ms   p(99)=8.68ms  
    http_req_failed................: 0.00% 0 out of 51594
    http_reqs......................: 51594 429.939077/s

    EXECUTION
    iteration_duration.............: avg=104.16ms min=101.19ms med=103.92ms max=138.68ms p(90)=105.96ms p(95)=106.78ms p(99)=109.19ms
    iterations.....................: 51593 429.930743/s
    vus............................: 1     min=0          max=99 
    vus_max........................: 100   min=100        max=100

    NETWORK
    data_received..................: 16 MB 133 kB/s
    data_sent......................: 11 MB 90 kB/s




exit_code=0

```

Reconciliation:

```json
{
  "http_202": 51593,
  "confirmed": 51593,
  "redis_stock": 48407,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 0
}

```

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```

### r1-run-2 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [r1-run-2](bench/results/replicas/r1-run-2/).

```text
$ git rev-parse HEAD
962bbf2943bb18d76298a47893ac866a14c47fd2

exit_code=0

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=8.77ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 51650 430.307309/s
    unexpected_responses...........: 0.00% 0 out of 51650

    HTTP
    http_req_duration..............: avg=3.5ms    min=999.35µs med=3.21ms   max=14.63ms  p(90)=5.3ms    p(95)=6.31ms   p(99)=8.77ms  
      { expected_response:true }...: avg=3.5ms    min=999.35µs med=3.21ms   max=14.63ms  p(90)=5.3ms    p(95)=6.31ms   p(99)=8.77ms  
    http_req_failed................: 0.00% 0 out of 51651
    http_reqs......................: 51651 430.31564/s

    EXECUTION
    iteration_duration.............: avg=104.04ms min=101.21ms med=103.78ms max=119.71ms p(90)=105.88ms p(95)=106.87ms p(99)=109.32ms
    iterations.....................: 51650 430.307309/s
    vus............................: 1     min=0          max=99 
    vus_max........................: 100   min=100        max=100

    NETWORK
    data_received..................: 16 MB 133 kB/s
    data_sent......................: 11 MB 90 kB/s




exit_code=0

```

Reconciliation:

```json
{
  "http_202": 51650,
  "confirmed": 51650,
  "redis_stock": 48350,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 0
}

```

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```

### r1-run-3 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [r1-run-3](bench/results/replicas/r1-run-3/).

```text
$ git rev-parse HEAD
962bbf2943bb18d76298a47893ac866a14c47fd2

exit_code=0

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=8.28ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 51618 429.938511/s
    unexpected_responses...........: 0.00% 0 out of 51618

    HTTP
    http_req_duration..............: avg=3.55ms   min=931.62µs med=3.32ms  max=13ms     p(90)=5.3ms    p(95)=6.15ms   p(99)=8.28ms  
      { expected_response:true }...: avg=3.55ms   min=931.62µs med=3.32ms  max=13ms     p(90)=5.3ms    p(95)=6.15ms   p(99)=8.28ms  
    http_req_failed................: 0.00% 0 out of 51619
    http_reqs......................: 51619 429.946841/s

    EXECUTION
    iteration_duration.............: avg=104.11ms min=101.11ms med=103.9ms max=124.75ms p(90)=105.84ms p(95)=106.71ms p(99)=108.91ms
    iterations.....................: 51618 429.938511/s
    vus............................: 1     min=0          max=99 
    vus_max........................: 100   min=100        max=100

    NETWORK
    data_received..................: 16 MB 133 kB/s
    data_sent......................: 11 MB 90 kB/s




exit_code=0

```

Reconciliation:

```json
{
  "http_202": 51618,
  "confirmed": 51618,
  "redis_stock": 48382,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 0
}

```

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```

### r2-run-1 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [r2-run-1](bench/results/replicas/r2-run-1/).

```text
$ git rev-parse HEAD
962bbf2943bb18d76298a47893ac866a14c47fd2

exit_code=0

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=8.92ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 51495 426.924715/s
    unexpected_responses...........: 0.00% 0 out of 51495

    HTTP
    http_req_duration..............: avg=3.79ms   min=1.14ms   med=3.49ms   max=521.21ms p(90)=5.59ms   p(95)=6.5ms    p(99)=8.92ms  
      { expected_response:true }...: avg=3.79ms   min=1.14ms   med=3.49ms   max=521.21ms p(90)=5.59ms   p(95)=6.5ms    p(99)=8.92ms  
    http_req_failed................: 0.00% 0 out of 51496
    http_reqs......................: 51496 426.933005/s

    EXECUTION
    iteration_duration.............: avg=104.36ms min=101.45ms med=104.07ms max=606.18ms p(90)=106.21ms p(95)=107.15ms p(99)=109.51ms
    iterations.....................: 51495 426.924715/s
    vus............................: 3     min=0          max=99 
    vus_max........................: 100   min=100        max=100

    NETWORK
    data_received..................: 16 MB 132 kB/s
    data_sent......................: 11 MB 89 kB/s




exit_code=0

```

Reconciliation:

```json
{
  "http_202": 51495,
  "confirmed": 51495,
  "redis_stock": 48505,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 0
}

```

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```

### r2-run-2 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [r2-run-2](bench/results/replicas/r2-run-2/).

```text
$ git rev-parse HEAD
962bbf2943bb18d76298a47893ac866a14c47fd2

exit_code=0

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=8.33ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 51510 429.18891/s
    unexpected_responses...........: 0.00% 0 out of 51510

    HTTP
    http_req_duration..............: avg=3.75ms   min=1.14ms   med=3.5ms    max=89.91ms  p(90)=5.48ms   p(95)=6.35ms   p(99)=8.33ms 
      { expected_response:true }...: avg=3.75ms   min=1.14ms   med=3.5ms    max=89.91ms  p(90)=5.48ms   p(95)=6.35ms   p(99)=8.33ms 
    http_req_failed................: 0.00% 0 out of 51511
    http_reqs......................: 51511 429.197242/s

    EXECUTION
    iteration_duration.............: avg=104.32ms min=101.44ms med=104.09ms max=190.91ms p(90)=106.08ms p(95)=106.93ms p(99)=108.9ms
    iterations.....................: 51510 429.18891/s
    vus............................: 1     min=0          max=99 
    vus_max........................: 100   min=100        max=100

    NETWORK
    data_received..................: 16 MB 133 kB/s
    data_sent......................: 11 MB 90 kB/s




exit_code=0

```

Reconciliation:

```json
{
  "http_202": 51510,
  "confirmed": 51510,
  "redis_stock": 48490,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 0
}

```

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```

### r2-run-3 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [r2-run-3](bench/results/replicas/r2-run-3/).

```text
$ git rev-parse HEAD
962bbf2943bb18d76298a47893ac866a14c47fd2

exit_code=0

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=8.47ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 51488 428.921495/s
    unexpected_responses...........: 0.00% 0 out of 51488

    HTTP
    http_req_duration..............: avg=3.8ms    min=1.15ms  med=3.54ms   max=13.68ms p(90)=5.65ms   p(95)=6.56ms   p(99)=8.47ms  
      { expected_response:true }...: avg=3.8ms    min=1.15ms  med=3.54ms   max=13.68ms p(90)=5.65ms   p(95)=6.56ms   p(99)=8.47ms  
    http_req_failed................: 0.00% 0 out of 51489
    http_reqs......................: 51489 428.929826/s

    EXECUTION
    iteration_duration.............: avg=104.37ms min=101.3ms med=104.11ms max=132.4ms p(90)=106.24ms p(95)=107.13ms p(99)=109.08ms
    iterations.....................: 51488 428.921495/s
    vus............................: 1     min=0          max=99 
    vus_max........................: 100   min=100        max=100

    NETWORK
    data_received..................: 16 MB 133 kB/s
    data_sent......................: 11 MB 90 kB/s




exit_code=0

```

Reconciliation:

```json
{
  "http_202": 51488,
  "confirmed": 51488,
  "redis_stock": 48512,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 0
}

```

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```

### r3-run-1 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [r3-run-1](bench/results/replicas/r3-run-1/).

```text
$ git rev-parse HEAD
962bbf2943bb18d76298a47893ac866a14c47fd2

exit_code=0

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=10.01ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 51337 425.050091/s
    unexpected_responses...........: 0.00% 0 out of 51337

    HTTP
    http_req_duration..............: avg=4.06ms   min=59.36µs  med=3.67ms   max=798.27ms p(90)=5.88ms   p(95)=7ms      p(99)=10.01ms 
      { expected_response:true }...: avg=4.06ms   min=59.36µs  med=3.67ms   max=798.27ms p(90)=5.88ms   p(95)=7ms      p(99)=10.01ms 
    http_req_failed................: 0.00% 0 out of 51338
    http_reqs......................: 51338 425.058371/s

    EXECUTION
    iteration_duration.............: avg=104.67ms min=101.57ms med=104.29ms max=899.25ms p(90)=106.57ms p(95)=107.65ms p(99)=110.66ms
    iterations.....................: 51337 425.050091/s
    vus............................: 3     min=0          max=100
    vus_max........................: 100   min=100        max=100

    NETWORK
    data_received..................: 16 MB 132 kB/s
    data_sent......................: 11 MB 89 kB/s




exit_code=0

```

Reconciliation:

```json
{
  "http_202": 51337,
  "confirmed": 51337,
  "redis_stock": 48663,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 0
}

```

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```

### r3-run-2 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [r3-run-2](bench/results/replicas/r3-run-2/).

```text
$ git rev-parse HEAD
962bbf2943bb18d76298a47893ac866a14c47fd2

exit_code=0

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=10.65ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 51481 428.89571/s
    unexpected_responses...........: 0.00% 0 out of 51481

    HTTP
    http_req_duration..............: avg=3.78ms   min=1.3ms    med=3.34ms   max=591.28ms p(90)=5.45ms   p(95)=6.51ms   p(99)=10.65ms 
      { expected_response:true }...: avg=3.78ms   min=1.3ms    med=3.34ms   max=591.28ms p(90)=5.45ms   p(95)=6.51ms   p(99)=10.65ms 
    http_req_failed................: 0.00% 0 out of 51482
    http_reqs......................: 51482 428.904041/s

    EXECUTION
    iteration_duration.............: avg=104.39ms min=101.62ms med=103.96ms max=691.65ms p(90)=106.09ms p(95)=107.13ms p(99)=111.47ms
    iterations.....................: 51481 428.89571/s
    vus............................: 1     min=0          max=99 
    vus_max........................: 100   min=100        max=100

    NETWORK
    data_received..................: 16 MB 133 kB/s
    data_sent......................: 11 MB 90 kB/s




exit_code=0

```

Reconciliation:

```json
{
  "http_202": 51481,
  "confirmed": 51481,
  "redis_stock": 48519,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 0
}

```

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```

### r3-run-3 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [r3-run-3](bench/results/replicas/r3-run-3/).

```text
$ git rev-parse HEAD
962bbf2943bb18d76298a47893ac866a14c47fd2

exit_code=0

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=9.88ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.00%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.00%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 51431 428.395365/s
    unexpected_responses...........: 0.00% 0 out of 51431

    HTTP
    http_req_duration..............: avg=3.88ms   min=1.15ms   med=3.46ms   max=63.94ms  p(90)=5.66ms   p(95)=6.68ms   p(99)=9.88ms 
      { expected_response:true }...: avg=3.88ms   min=1.15ms   med=3.46ms   max=63.94ms  p(90)=5.66ms   p(95)=6.68ms   p(99)=9.88ms 
    http_req_failed................: 0.00% 0 out of 51432
    http_reqs......................: 51432 428.403695/s

    EXECUTION
    iteration_duration.............: avg=104.49ms min=101.51ms med=104.09ms max=164.94ms p(90)=106.26ms p(95)=107.29ms p(99)=110.4ms
    iterations.....................: 51431 428.395365/s
    vus............................: 1     min=0          max=99 
    vus_max........................: 100   min=100        max=100

    NETWORK
    data_received..................: 16 MB 133 kB/s
    data_sent......................: 11 MB 89 kB/s




exit_code=0

```

Reconciliation:

```json
{
  "http_202": 51431,
  "confirmed": 51431,
  "redis_stock": 48569,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 0
}

```

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```


OOMKilled flags from executed Docker inspect:

```text
('r1-run-1', '/flash-sale-engine-app-1', False)
('r1-run-1', '/flash_postgres', False)
('r1-run-1', '/flash_redis', False)
('r1-run-1', '/flash_rabbitmq', False)
('r1-run-2', '/flash-sale-engine-app-1', False)
('r1-run-2', '/flash_postgres', False)
('r1-run-2', '/flash_redis', False)
('r1-run-2', '/flash_rabbitmq', False)
('r1-run-3', '/flash-sale-engine-app-1', False)
('r1-run-3', '/flash_postgres', False)
('r1-run-3', '/flash_redis', False)
('r1-run-3', '/flash_rabbitmq', False)
('r2-run-1', '/flash-sale-engine-app-1', False)
('r2-run-1', '/flash-sale-engine-app-2', False)
('r2-run-1', '/flash_postgres', False)
('r2-run-1', '/flash_redis', False)
('r2-run-1', '/flash_rabbitmq', False)
('r2-run-2', '/flash-sale-engine-app-1', False)
('r2-run-2', '/flash-sale-engine-app-2', False)
('r2-run-2', '/flash_postgres', False)
('r2-run-2', '/flash_redis', False)
('r2-run-2', '/flash_rabbitmq', False)
('r2-run-3', '/flash-sale-engine-app-1', False)
('r2-run-3', '/flash-sale-engine-app-2', False)
('r2-run-3', '/flash_postgres', False)
('r2-run-3', '/flash_redis', False)
('r2-run-3', '/flash_rabbitmq', False)
('r3-run-1', '/flash-sale-engine-app-1', False)
('r3-run-1', '/flash-sale-engine-app-2', False)
('r3-run-1', '/flash-sale-engine-app-3', False)
('r3-run-1', '/flash_postgres', False)
('r3-run-1', '/flash_redis', False)
('r3-run-1', '/flash_rabbitmq', False)
('r3-run-2', '/flash-sale-engine-app-1', False)
('r3-run-2', '/flash-sale-engine-app-2', False)
('r3-run-2', '/flash-sale-engine-app-3', False)
('r3-run-2', '/flash_postgres', False)
('r3-run-2', '/flash_redis', False)
('r3-run-2', '/flash_rabbitmq', False)
('r3-run-3', '/flash-sale-engine-app-1', False)
('r3-run-3', '/flash-sale-engine-app-2', False)
('r3-run-3', '/flash-sale-engine-app-3', False)
('r3-run-3', '/flash_postgres', False)
('r3-run-3', '/flash_redis', False)
('r3-run-3', '/flash_rabbitmq', False)
```

CPU throttling and memory OOM counters (raw before/after cgroup snapshots):

```text
('r1-run-1', 'before-cgroup-0.txt', 'nr_throttled 0')
('r1-run-1', 'before-cgroup-0.txt', 'throttled_usec 0')
('r1-run-1', 'before-cgroup-0.txt', 'oom 0')
('r1-run-1', 'before-cgroup-0.txt', 'oom_kill 0')
('r1-run-1', 'after-cgroup-0.txt', 'nr_throttled 0')
('r1-run-1', 'after-cgroup-0.txt', 'throttled_usec 0')
('r1-run-1', 'after-cgroup-0.txt', 'oom 0')
('r1-run-1', 'after-cgroup-0.txt', 'oom_kill 0')
('r1-run-1', 'before-cgroup-2.txt', 'nr_throttled 0')
('r1-run-1', 'before-cgroup-2.txt', 'throttled_usec 0')
('r1-run-1', 'before-cgroup-2.txt', 'oom 0')
('r1-run-1', 'before-cgroup-2.txt', 'oom_kill 0')
('r1-run-1', 'before-cgroup-1.txt', 'nr_throttled 0')
('r1-run-1', 'before-cgroup-1.txt', 'throttled_usec 0')
('r1-run-1', 'before-cgroup-1.txt', 'oom 0')
('r1-run-1', 'before-cgroup-1.txt', 'oom_kill 0')
('r1-run-1', 'before-cgroup-3.txt', 'nr_throttled 0')
('r1-run-1', 'before-cgroup-3.txt', 'throttled_usec 0')
('r1-run-1', 'before-cgroup-3.txt', 'oom 0')
('r1-run-1', 'before-cgroup-3.txt', 'oom_kill 0')
('r1-run-1', 'after-cgroup-2.txt', 'nr_throttled 0')
('r1-run-1', 'after-cgroup-2.txt', 'throttled_usec 0')
('r1-run-1', 'after-cgroup-2.txt', 'oom 0')
('r1-run-1', 'after-cgroup-2.txt', 'oom_kill 0')
('r1-run-1', 'after-cgroup-1.txt', 'nr_throttled 0')
('r1-run-1', 'after-cgroup-1.txt', 'throttled_usec 0')
('r1-run-1', 'after-cgroup-1.txt', 'oom 0')
('r1-run-1', 'after-cgroup-1.txt', 'oom_kill 0')
('r1-run-1', 'after-cgroup-3.txt', 'nr_throttled 0')
('r1-run-1', 'after-cgroup-3.txt', 'throttled_usec 0')
('r1-run-1', 'after-cgroup-3.txt', 'oom 0')
('r1-run-1', 'after-cgroup-3.txt', 'oom_kill 0')
('r1-run-2', 'before-cgroup-0.txt', 'nr_throttled 0')
('r1-run-2', 'before-cgroup-0.txt', 'throttled_usec 0')
('r1-run-2', 'before-cgroup-0.txt', 'oom 0')
('r1-run-2', 'before-cgroup-0.txt', 'oom_kill 0')
('r1-run-2', 'after-cgroup-0.txt', 'nr_throttled 0')
('r1-run-2', 'after-cgroup-0.txt', 'throttled_usec 0')
('r1-run-2', 'after-cgroup-0.txt', 'oom 0')
('r1-run-2', 'after-cgroup-0.txt', 'oom_kill 0')
('r1-run-2', 'before-cgroup-2.txt', 'nr_throttled 0')
('r1-run-2', 'before-cgroup-2.txt', 'throttled_usec 0')
('r1-run-2', 'before-cgroup-2.txt', 'oom 0')
('r1-run-2', 'before-cgroup-2.txt', 'oom_kill 0')
('r1-run-2', 'before-cgroup-1.txt', 'nr_throttled 0')
('r1-run-2', 'before-cgroup-1.txt', 'throttled_usec 0')
('r1-run-2', 'before-cgroup-1.txt', 'oom 0')
('r1-run-2', 'before-cgroup-1.txt', 'oom_kill 0')
('r1-run-2', 'before-cgroup-3.txt', 'nr_throttled 0')
('r1-run-2', 'before-cgroup-3.txt', 'throttled_usec 0')
('r1-run-2', 'before-cgroup-3.txt', 'oom 0')
('r1-run-2', 'before-cgroup-3.txt', 'oom_kill 0')
('r1-run-2', 'after-cgroup-2.txt', 'nr_throttled 0')
('r1-run-2', 'after-cgroup-2.txt', 'throttled_usec 0')
('r1-run-2', 'after-cgroup-2.txt', 'oom 0')
('r1-run-2', 'after-cgroup-2.txt', 'oom_kill 0')
('r1-run-2', 'after-cgroup-1.txt', 'nr_throttled 0')
('r1-run-2', 'after-cgroup-1.txt', 'throttled_usec 0')
('r1-run-2', 'after-cgroup-1.txt', 'oom 0')
('r1-run-2', 'after-cgroup-1.txt', 'oom_kill 0')
('r1-run-2', 'after-cgroup-3.txt', 'nr_throttled 0')
('r1-run-2', 'after-cgroup-3.txt', 'throttled_usec 0')
('r1-run-2', 'after-cgroup-3.txt', 'oom 0')
('r1-run-2', 'after-cgroup-3.txt', 'oom_kill 0')
('r1-run-3', 'before-cgroup-0.txt', 'nr_throttled 0')
('r1-run-3', 'before-cgroup-0.txt', 'throttled_usec 0')
('r1-run-3', 'before-cgroup-0.txt', 'oom 0')
('r1-run-3', 'before-cgroup-0.txt', 'oom_kill 0')
('r1-run-3', 'after-cgroup-0.txt', 'nr_throttled 0')
('r1-run-3', 'after-cgroup-0.txt', 'throttled_usec 0')
('r1-run-3', 'after-cgroup-0.txt', 'oom 0')
('r1-run-3', 'after-cgroup-0.txt', 'oom_kill 0')
('r1-run-3', 'before-cgroup-2.txt', 'nr_throttled 0')
('r1-run-3', 'before-cgroup-2.txt', 'throttled_usec 0')
('r1-run-3', 'before-cgroup-2.txt', 'oom 0')
('r1-run-3', 'before-cgroup-2.txt', 'oom_kill 0')
('r1-run-3', 'before-cgroup-1.txt', 'nr_throttled 0')
('r1-run-3', 'before-cgroup-1.txt', 'throttled_usec 0')
('r1-run-3', 'before-cgroup-1.txt', 'oom 0')
('r1-run-3', 'before-cgroup-1.txt', 'oom_kill 0')
('r1-run-3', 'before-cgroup-3.txt', 'nr_throttled 0')
('r1-run-3', 'before-cgroup-3.txt', 'throttled_usec 0')
('r1-run-3', 'before-cgroup-3.txt', 'oom 0')
('r1-run-3', 'before-cgroup-3.txt', 'oom_kill 0')
('r1-run-3', 'after-cgroup-2.txt', 'nr_throttled 0')
('r1-run-3', 'after-cgroup-2.txt', 'throttled_usec 0')
('r1-run-3', 'after-cgroup-2.txt', 'oom 0')
('r1-run-3', 'after-cgroup-2.txt', 'oom_kill 0')
('r1-run-3', 'after-cgroup-1.txt', 'nr_throttled 0')
('r1-run-3', 'after-cgroup-1.txt', 'throttled_usec 0')
('r1-run-3', 'after-cgroup-1.txt', 'oom 0')
('r1-run-3', 'after-cgroup-1.txt', 'oom_kill 0')
('r1-run-3', 'after-cgroup-3.txt', 'nr_throttled 0')
('r1-run-3', 'after-cgroup-3.txt', 'throttled_usec 0')
('r1-run-3', 'after-cgroup-3.txt', 'oom 0')
('r1-run-3', 'after-cgroup-3.txt', 'oom_kill 0')
('r2-run-1', 'before-cgroup-0.txt', 'nr_throttled 0')
('r2-run-1', 'before-cgroup-0.txt', 'throttled_usec 0')
('r2-run-1', 'before-cgroup-0.txt', 'oom 0')
('r2-run-1', 'before-cgroup-0.txt', 'oom_kill 0')
('r2-run-1', 'after-cgroup-0.txt', 'nr_throttled 0')
('r2-run-1', 'after-cgroup-0.txt', 'throttled_usec 0')
('r2-run-1', 'after-cgroup-0.txt', 'oom 0')
('r2-run-1', 'after-cgroup-0.txt', 'oom_kill 0')
('r2-run-1', 'before-cgroup-2.txt', 'nr_throttled 0')
('r2-run-1', 'before-cgroup-2.txt', 'throttled_usec 0')
('r2-run-1', 'before-cgroup-2.txt', 'oom 0')
('r2-run-1', 'before-cgroup-2.txt', 'oom_kill 0')
('r2-run-1', 'before-cgroup-1.txt', 'nr_throttled 0')
('r2-run-1', 'before-cgroup-1.txt', 'throttled_usec 0')
('r2-run-1', 'before-cgroup-1.txt', 'oom 0')
('r2-run-1', 'before-cgroup-1.txt', 'oom_kill 0')
('r2-run-1', 'after-cgroup-4.txt', 'nr_throttled 0')
('r2-run-1', 'after-cgroup-4.txt', 'throttled_usec 0')
('r2-run-1', 'after-cgroup-4.txt', 'oom 0')
('r2-run-1', 'after-cgroup-4.txt', 'oom_kill 0')
('r2-run-1', 'before-cgroup-3.txt', 'nr_throttled 0')
('r2-run-1', 'before-cgroup-3.txt', 'throttled_usec 0')
('r2-run-1', 'before-cgroup-3.txt', 'oom 0')
('r2-run-1', 'before-cgroup-3.txt', 'oom_kill 0')
('r2-run-1', 'after-cgroup-2.txt', 'nr_throttled 0')
('r2-run-1', 'after-cgroup-2.txt', 'throttled_usec 0')
('r2-run-1', 'after-cgroup-2.txt', 'oom 0')
('r2-run-1', 'after-cgroup-2.txt', 'oom_kill 0')
('r2-run-1', 'before-cgroup-4.txt', 'nr_throttled 0')
('r2-run-1', 'before-cgroup-4.txt', 'throttled_usec 0')
('r2-run-1', 'before-cgroup-4.txt', 'oom 0')
('r2-run-1', 'before-cgroup-4.txt', 'oom_kill 0')
('r2-run-1', 'after-cgroup-1.txt', 'nr_throttled 0')
('r2-run-1', 'after-cgroup-1.txt', 'throttled_usec 0')
('r2-run-1', 'after-cgroup-1.txt', 'oom 0')
('r2-run-1', 'after-cgroup-1.txt', 'oom_kill 0')
('r2-run-1', 'after-cgroup-3.txt', 'nr_throttled 0')
('r2-run-1', 'after-cgroup-3.txt', 'throttled_usec 0')
('r2-run-1', 'after-cgroup-3.txt', 'oom 0')
('r2-run-1', 'after-cgroup-3.txt', 'oom_kill 0')
('r2-run-2', 'before-cgroup-0.txt', 'nr_throttled 0')
('r2-run-2', 'before-cgroup-0.txt', 'throttled_usec 0')
('r2-run-2', 'before-cgroup-0.txt', 'oom 0')
('r2-run-2', 'before-cgroup-0.txt', 'oom_kill 0')
('r2-run-2', 'after-cgroup-0.txt', 'nr_throttled 0')
('r2-run-2', 'after-cgroup-0.txt', 'throttled_usec 0')
('r2-run-2', 'after-cgroup-0.txt', 'oom 0')
('r2-run-2', 'after-cgroup-0.txt', 'oom_kill 0')
('r2-run-2', 'before-cgroup-2.txt', 'nr_throttled 0')
('r2-run-2', 'before-cgroup-2.txt', 'throttled_usec 0')
('r2-run-2', 'before-cgroup-2.txt', 'oom 0')
('r2-run-2', 'before-cgroup-2.txt', 'oom_kill 0')
('r2-run-2', 'before-cgroup-1.txt', 'nr_throttled 0')
('r2-run-2', 'before-cgroup-1.txt', 'throttled_usec 0')
('r2-run-2', 'before-cgroup-1.txt', 'oom 0')
('r2-run-2', 'before-cgroup-1.txt', 'oom_kill 0')
('r2-run-2', 'after-cgroup-4.txt', 'nr_throttled 0')
('r2-run-2', 'after-cgroup-4.txt', 'throttled_usec 0')
('r2-run-2', 'after-cgroup-4.txt', 'oom 0')
('r2-run-2', 'after-cgroup-4.txt', 'oom_kill 0')
('r2-run-2', 'before-cgroup-3.txt', 'nr_throttled 0')
('r2-run-2', 'before-cgroup-3.txt', 'throttled_usec 0')
('r2-run-2', 'before-cgroup-3.txt', 'oom 0')
('r2-run-2', 'before-cgroup-3.txt', 'oom_kill 0')
('r2-run-2', 'after-cgroup-2.txt', 'nr_throttled 0')
('r2-run-2', 'after-cgroup-2.txt', 'throttled_usec 0')
('r2-run-2', 'after-cgroup-2.txt', 'oom 0')
('r2-run-2', 'after-cgroup-2.txt', 'oom_kill 0')
('r2-run-2', 'before-cgroup-4.txt', 'nr_throttled 0')
('r2-run-2', 'before-cgroup-4.txt', 'throttled_usec 0')
('r2-run-2', 'before-cgroup-4.txt', 'oom 0')
('r2-run-2', 'before-cgroup-4.txt', 'oom_kill 0')
('r2-run-2', 'after-cgroup-1.txt', 'nr_throttled 0')
('r2-run-2', 'after-cgroup-1.txt', 'throttled_usec 0')
('r2-run-2', 'after-cgroup-1.txt', 'oom 0')
('r2-run-2', 'after-cgroup-1.txt', 'oom_kill 0')
('r2-run-2', 'after-cgroup-3.txt', 'nr_throttled 0')
('r2-run-2', 'after-cgroup-3.txt', 'throttled_usec 0')
('r2-run-2', 'after-cgroup-3.txt', 'oom 0')
('r2-run-2', 'after-cgroup-3.txt', 'oom_kill 0')
('r2-run-3', 'before-cgroup-0.txt', 'nr_throttled 0')
('r2-run-3', 'before-cgroup-0.txt', 'throttled_usec 0')
('r2-run-3', 'before-cgroup-0.txt', 'oom 0')
('r2-run-3', 'before-cgroup-0.txt', 'oom_kill 0')
('r2-run-3', 'after-cgroup-0.txt', 'nr_throttled 0')
('r2-run-3', 'after-cgroup-0.txt', 'throttled_usec 0')
('r2-run-3', 'after-cgroup-0.txt', 'oom 0')
('r2-run-3', 'after-cgroup-0.txt', 'oom_kill 0')
('r2-run-3', 'before-cgroup-2.txt', 'nr_throttled 0')
('r2-run-3', 'before-cgroup-2.txt', 'throttled_usec 0')
('r2-run-3', 'before-cgroup-2.txt', 'oom 0')
('r2-run-3', 'before-cgroup-2.txt', 'oom_kill 0')
('r2-run-3', 'before-cgroup-1.txt', 'nr_throttled 0')
('r2-run-3', 'before-cgroup-1.txt', 'throttled_usec 0')
('r2-run-3', 'before-cgroup-1.txt', 'oom 0')
('r2-run-3', 'before-cgroup-1.txt', 'oom_kill 0')
('r2-run-3', 'after-cgroup-4.txt', 'nr_throttled 0')
('r2-run-3', 'after-cgroup-4.txt', 'throttled_usec 0')
('r2-run-3', 'after-cgroup-4.txt', 'oom 0')
('r2-run-3', 'after-cgroup-4.txt', 'oom_kill 0')
('r2-run-3', 'before-cgroup-3.txt', 'nr_throttled 0')
('r2-run-3', 'before-cgroup-3.txt', 'throttled_usec 0')
('r2-run-3', 'before-cgroup-3.txt', 'oom 0')
('r2-run-3', 'before-cgroup-3.txt', 'oom_kill 0')
('r2-run-3', 'after-cgroup-2.txt', 'nr_throttled 0')
('r2-run-3', 'after-cgroup-2.txt', 'throttled_usec 0')
('r2-run-3', 'after-cgroup-2.txt', 'oom 0')
('r2-run-3', 'after-cgroup-2.txt', 'oom_kill 0')
('r2-run-3', 'before-cgroup-4.txt', 'nr_throttled 0')
('r2-run-3', 'before-cgroup-4.txt', 'throttled_usec 0')
('r2-run-3', 'before-cgroup-4.txt', 'oom 0')
('r2-run-3', 'before-cgroup-4.txt', 'oom_kill 0')
('r2-run-3', 'after-cgroup-1.txt', 'nr_throttled 0')
('r2-run-3', 'after-cgroup-1.txt', 'throttled_usec 0')
('r2-run-3', 'after-cgroup-1.txt', 'oom 0')
('r2-run-3', 'after-cgroup-1.txt', 'oom_kill 0')
('r2-run-3', 'after-cgroup-3.txt', 'nr_throttled 0')
('r2-run-3', 'after-cgroup-3.txt', 'throttled_usec 0')
('r2-run-3', 'after-cgroup-3.txt', 'oom 0')
('r2-run-3', 'after-cgroup-3.txt', 'oom_kill 0')
('r3-run-1', 'before-cgroup-0.txt', 'nr_throttled 0')
('r3-run-1', 'before-cgroup-0.txt', 'throttled_usec 0')
('r3-run-1', 'before-cgroup-0.txt', 'oom 0')
('r3-run-1', 'before-cgroup-0.txt', 'oom_kill 0')
('r3-run-1', 'after-cgroup-0.txt', 'nr_throttled 0')
('r3-run-1', 'after-cgroup-0.txt', 'throttled_usec 0')
('r3-run-1', 'after-cgroup-0.txt', 'oom 0')
('r3-run-1', 'after-cgroup-0.txt', 'oom_kill 0')
('r3-run-1', 'before-cgroup-2.txt', 'nr_throttled 0')
('r3-run-1', 'before-cgroup-2.txt', 'throttled_usec 0')
('r3-run-1', 'before-cgroup-2.txt', 'oom 0')
('r3-run-1', 'before-cgroup-2.txt', 'oom_kill 0')
('r3-run-1', 'before-cgroup-1.txt', 'nr_throttled 0')
('r3-run-1', 'before-cgroup-1.txt', 'throttled_usec 0')
('r3-run-1', 'before-cgroup-1.txt', 'oom 0')
('r3-run-1', 'before-cgroup-1.txt', 'oom_kill 0')
('r3-run-1', 'after-cgroup-4.txt', 'nr_throttled 0')
('r3-run-1', 'after-cgroup-4.txt', 'throttled_usec 0')
('r3-run-1', 'after-cgroup-4.txt', 'oom 0')
('r3-run-1', 'after-cgroup-4.txt', 'oom_kill 0')
('r3-run-1', 'before-cgroup-3.txt', 'nr_throttled 0')
('r3-run-1', 'before-cgroup-3.txt', 'throttled_usec 0')
('r3-run-1', 'before-cgroup-3.txt', 'oom 0')
('r3-run-1', 'before-cgroup-3.txt', 'oom_kill 0')
('r3-run-1', 'after-cgroup-2.txt', 'nr_throttled 0')
('r3-run-1', 'after-cgroup-2.txt', 'throttled_usec 0')
('r3-run-1', 'after-cgroup-2.txt', 'oom 0')
('r3-run-1', 'after-cgroup-2.txt', 'oom_kill 0')
('r3-run-1', 'before-cgroup-4.txt', 'nr_throttled 0')
('r3-run-1', 'before-cgroup-4.txt', 'throttled_usec 0')
('r3-run-1', 'before-cgroup-4.txt', 'oom 0')
('r3-run-1', 'before-cgroup-4.txt', 'oom_kill 0')
('r3-run-1', 'after-cgroup-1.txt', 'nr_throttled 0')
('r3-run-1', 'after-cgroup-1.txt', 'throttled_usec 0')
('r3-run-1', 'after-cgroup-1.txt', 'oom 0')
('r3-run-1', 'after-cgroup-1.txt', 'oom_kill 0')
('r3-run-1', 'after-cgroup-5.txt', 'nr_throttled 0')
('r3-run-1', 'after-cgroup-5.txt', 'throttled_usec 0')
('r3-run-1', 'after-cgroup-5.txt', 'oom 0')
('r3-run-1', 'after-cgroup-5.txt', 'oom_kill 0')
('r3-run-1', 'before-cgroup-5.txt', 'nr_throttled 0')
('r3-run-1', 'before-cgroup-5.txt', 'throttled_usec 0')
('r3-run-1', 'before-cgroup-5.txt', 'oom 0')
('r3-run-1', 'before-cgroup-5.txt', 'oom_kill 0')
('r3-run-1', 'after-cgroup-3.txt', 'nr_throttled 0')
('r3-run-1', 'after-cgroup-3.txt', 'throttled_usec 0')
('r3-run-1', 'after-cgroup-3.txt', 'oom 0')
('r3-run-1', 'after-cgroup-3.txt', 'oom_kill 0')
('r3-run-2', 'before-cgroup-0.txt', 'nr_throttled 0')
('r3-run-2', 'before-cgroup-0.txt', 'throttled_usec 0')
('r3-run-2', 'before-cgroup-0.txt', 'oom 0')
('r3-run-2', 'before-cgroup-0.txt', 'oom_kill 0')
('r3-run-2', 'after-cgroup-0.txt', 'nr_throttled 0')
('r3-run-2', 'after-cgroup-0.txt', 'throttled_usec 0')
('r3-run-2', 'after-cgroup-0.txt', 'oom 0')
('r3-run-2', 'after-cgroup-0.txt', 'oom_kill 0')
('r3-run-2', 'before-cgroup-2.txt', 'nr_throttled 0')
('r3-run-2', 'before-cgroup-2.txt', 'throttled_usec 0')
('r3-run-2', 'before-cgroup-2.txt', 'oom 0')
('r3-run-2', 'before-cgroup-2.txt', 'oom_kill 0')
('r3-run-2', 'before-cgroup-1.txt', 'nr_throttled 0')
('r3-run-2', 'before-cgroup-1.txt', 'throttled_usec 0')
('r3-run-2', 'before-cgroup-1.txt', 'oom 0')
('r3-run-2', 'before-cgroup-1.txt', 'oom_kill 0')
('r3-run-2', 'after-cgroup-4.txt', 'nr_throttled 0')
('r3-run-2', 'after-cgroup-4.txt', 'throttled_usec 0')
('r3-run-2', 'after-cgroup-4.txt', 'oom 0')
('r3-run-2', 'after-cgroup-4.txt', 'oom_kill 0')
('r3-run-2', 'before-cgroup-3.txt', 'nr_throttled 0')
('r3-run-2', 'before-cgroup-3.txt', 'throttled_usec 0')
('r3-run-2', 'before-cgroup-3.txt', 'oom 0')
('r3-run-2', 'before-cgroup-3.txt', 'oom_kill 0')
('r3-run-2', 'after-cgroup-2.txt', 'nr_throttled 0')
('r3-run-2', 'after-cgroup-2.txt', 'throttled_usec 0')
('r3-run-2', 'after-cgroup-2.txt', 'oom 0')
('r3-run-2', 'after-cgroup-2.txt', 'oom_kill 0')
('r3-run-2', 'before-cgroup-4.txt', 'nr_throttled 0')
('r3-run-2', 'before-cgroup-4.txt', 'throttled_usec 0')
('r3-run-2', 'before-cgroup-4.txt', 'oom 0')
('r3-run-2', 'before-cgroup-4.txt', 'oom_kill 0')
('r3-run-2', 'after-cgroup-1.txt', 'nr_throttled 0')
('r3-run-2', 'after-cgroup-1.txt', 'throttled_usec 0')
('r3-run-2', 'after-cgroup-1.txt', 'oom 0')
('r3-run-2', 'after-cgroup-1.txt', 'oom_kill 0')
('r3-run-2', 'after-cgroup-5.txt', 'nr_throttled 0')
('r3-run-2', 'after-cgroup-5.txt', 'throttled_usec 0')
('r3-run-2', 'after-cgroup-5.txt', 'oom 0')
('r3-run-2', 'after-cgroup-5.txt', 'oom_kill 0')
('r3-run-2', 'before-cgroup-5.txt', 'nr_throttled 0')
('r3-run-2', 'before-cgroup-5.txt', 'throttled_usec 0')
('r3-run-2', 'before-cgroup-5.txt', 'oom 0')
('r3-run-2', 'before-cgroup-5.txt', 'oom_kill 0')
('r3-run-2', 'after-cgroup-3.txt', 'nr_throttled 0')
('r3-run-2', 'after-cgroup-3.txt', 'throttled_usec 0')
('r3-run-2', 'after-cgroup-3.txt', 'oom 0')
('r3-run-2', 'after-cgroup-3.txt', 'oom_kill 0')
('r3-run-3', 'before-cgroup-0.txt', 'nr_throttled 0')
('r3-run-3', 'before-cgroup-0.txt', 'throttled_usec 0')
('r3-run-3', 'before-cgroup-0.txt', 'oom 0')
('r3-run-3', 'before-cgroup-0.txt', 'oom_kill 0')
('r3-run-3', 'after-cgroup-0.txt', 'nr_throttled 0')
('r3-run-3', 'after-cgroup-0.txt', 'throttled_usec 0')
('r3-run-3', 'after-cgroup-0.txt', 'oom 0')
('r3-run-3', 'after-cgroup-0.txt', 'oom_kill 0')
('r3-run-3', 'before-cgroup-2.txt', 'nr_throttled 0')
('r3-run-3', 'before-cgroup-2.txt', 'throttled_usec 0')
('r3-run-3', 'before-cgroup-2.txt', 'oom 0')
('r3-run-3', 'before-cgroup-2.txt', 'oom_kill 0')
('r3-run-3', 'before-cgroup-1.txt', 'nr_throttled 0')
('r3-run-3', 'before-cgroup-1.txt', 'throttled_usec 0')
('r3-run-3', 'before-cgroup-1.txt', 'oom 0')
('r3-run-3', 'before-cgroup-1.txt', 'oom_kill 0')
('r3-run-3', 'after-cgroup-4.txt', 'nr_throttled 0')
('r3-run-3', 'after-cgroup-4.txt', 'throttled_usec 0')
('r3-run-3', 'after-cgroup-4.txt', 'oom 0')
('r3-run-3', 'after-cgroup-4.txt', 'oom_kill 0')
('r3-run-3', 'before-cgroup-3.txt', 'nr_throttled 0')
('r3-run-3', 'before-cgroup-3.txt', 'throttled_usec 0')
('r3-run-3', 'before-cgroup-3.txt', 'oom 0')
('r3-run-3', 'before-cgroup-3.txt', 'oom_kill 0')
('r3-run-3', 'after-cgroup-2.txt', 'nr_throttled 0')
('r3-run-3', 'after-cgroup-2.txt', 'throttled_usec 0')
('r3-run-3', 'after-cgroup-2.txt', 'oom 0')
('r3-run-3', 'after-cgroup-2.txt', 'oom_kill 0')
('r3-run-3', 'before-cgroup-4.txt', 'nr_throttled 0')
('r3-run-3', 'before-cgroup-4.txt', 'throttled_usec 0')
('r3-run-3', 'before-cgroup-4.txt', 'oom 0')
('r3-run-3', 'before-cgroup-4.txt', 'oom_kill 0')
('r3-run-3', 'after-cgroup-1.txt', 'nr_throttled 0')
('r3-run-3', 'after-cgroup-1.txt', 'throttled_usec 0')
('r3-run-3', 'after-cgroup-1.txt', 'oom 0')
('r3-run-3', 'after-cgroup-1.txt', 'oom_kill 0')
('r3-run-3', 'after-cgroup-5.txt', 'nr_throttled 0')
('r3-run-3', 'after-cgroup-5.txt', 'throttled_usec 0')
('r3-run-3', 'after-cgroup-5.txt', 'oom 0')
('r3-run-3', 'after-cgroup-5.txt', 'oom_kill 0')
('r3-run-3', 'before-cgroup-5.txt', 'nr_throttled 0')
('r3-run-3', 'before-cgroup-5.txt', 'throttled_usec 0')
('r3-run-3', 'before-cgroup-5.txt', 'oom 0')
('r3-run-3', 'before-cgroup-5.txt', 'oom_kill 0')
('r3-run-3', 'after-cgroup-3.txt', 'nr_throttled 0')
('r3-run-3', 'after-cgroup-3.txt', 'throttled_usec 0')
('r3-run-3', 'after-cgroup-3.txt', 'oom 0')
('r3-run-3', 'after-cgroup-3.txt', 'oom_kill 0')
```

Throughput did not scale in the requested ramp: every replicated multi-app run was below every single-app run. Executed range calculation:

```text
{1: [429.93907653071693, 430.3156403469725, 429.94684061574856], 2: [426.9330053557005, 429.1972420560015, 428.9298256551053], 3: [425.05837109113247, 428.9040413870723, 428.40369484545056]}
{1: (429.93907653071693, 430.3156403469725), 2: (426.9330053557005, 429.1972420560015), 3: (425.05837109113247, 428.9040413870723)}
```

The measured limiting factor for this closed-loop workload is offered-load pacing: bounded VUs sleep between requests; this is not an unpaced saturation test. nginx and extra replicas added latency rather than raising offered request rate. CPU/memory snapshots, empty queues and captured zero throttling/OOM counters do not establish saturation of app CPU, Redis, RabbitMQ, Postgres, or the laptop. We cannot honestly name one of those as a demonstrated capacity bottleneck from these runs. Server maximum capacity and the capacity bottleneck remain unmeasured. The laptop co-hosts k6, nginx, app replicas and infrastructure. No throughput scaling claim is made.

Redis runtime configuration differs from the assumption that it has no persistence at all:

```text
appendonly
no
save
3600 1 300 100 60 10000
dbfilename
dump.rdb
dir
/data
[{"Type":"volume","Name":"2c50c3d6439f56cc652d6f34838dc12c9794bd8cb4c23880572e5f98dd0261ae","Source":"/var/lib/docker/volumes/2c50c3d6439f56cc652d6f34838dc12c9794bd8cb4c23880572e5f98dd0261ae/_data","Destination":"/data","Driver":"local","Mode":"z","RW":true,"Propagation":""}]
```

Compose does not explicitly configure persistence, but this image enables RDB snapshots and mounts an anonymous data volume. A normal restart may preserve data; it is not a durable inventory design.

## Phase B: fault injection before confirms

Each row is an executed run. Error rate is k6 HTTP failure fraction; expected sold-out/idempotent statuses do not count as failures. Latencies are milliseconds, from full-precision k6 exports. Threshold failures remain in the table and raw files.

| Run | req/s | p50 | p95 | p99 | errors | HTTP 202 | CONFIRMED | stock | oversold | lost | duplicates | stuck | k6 exit |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| kill-run-1 | 476.2091285856997 | 3.524826 | 7.256963 | 12.6046405 | 0.0004364829946225295 | 57250 | 57250 | 42750 | 0 | 0 | 0 | 0 | 0 |
| kill-run-2 | 476.54047772893625 | 3.632466 | 6.939588249999999 | 9.814697580000008 | 0.00010480349344978167 | 57243 | 57243 | 42757 | 0 | 0 | 0 | 0 | 0 |
| kill-run-3 | 477.6585536292755 | 3.672739 | 6.724177799999999 | 9.805730940000005 | 0.00043594259507907996 | 57321 | 57321 | 42679 | 0 | 0 | 0 | 0 | 0 |
| rabbit-run-1 | 472.83416849317416 | 3.7358729999999998 | 6.839857099999998 | 10.29100013000001 | 0.08939265835270908 | 51696 | 51696 | 48304 | 0 | 0 | 0 | 0 | 99 |
| rabbit-run-2 | 473.086130663348 | 3.6203890000000003 | 7.1388950499999995 | 11.035664539999997 | 0.09335960868494211 | 51527 | 51527 | 48473 | 0 | 0 | 0 | 0 | 99 |
| rabbit-run-3 | 472.68307513661875 | 3.577152 | 6.933515 | 10.830669600000014 | 0.11742444244952562 | 50139 | 50139 | 49861 | 0 | 0 | 0 | 0 | 99 |
| redis-observation | 452.1143395571845 | 3.718795 | 7.716246799999997 | 14.586090159999985 | 0.0007369196757553427 | 54239 | 54239 | 45761 | 0 | 0 | 0 | 0 | 0 |

Maximum observed per-container snapshots (Docker display precision; not guaranteed peaks), generated by executed `python3 bench/summarize.py bench/results/faults-before`:

```text
{"run": "kill-run-1", "container": "flash-sale-engine-app-2", "max_observed_cpu": "59.75%", "max_observed_memory": "325.9MiB / 640MiB"}
{"run": "kill-run-1", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "20.57%", "max_observed_memory": "11.3MiB / 64MiB"}
{"run": "kill-run-1", "container": "flash-sale-engine-app-1", "max_observed_cpu": "269.93%", "max_observed_memory": "419MiB / 640MiB"}
{"run": "kill-run-1", "container": "flash_rabbitmq", "max_observed_cpu": "212.61%", "max_observed_memory": "259.1MiB / 384MiB"}
{"run": "kill-run-1", "container": "flash_redis", "max_observed_cpu": "11.75%", "max_observed_memory": "22.49MiB / 384MiB"}
{"run": "kill-run-1", "container": "flash_postgres", "max_observed_cpu": "49.84%", "max_observed_memory": "91.47MiB / 384MiB"}
{"run": "kill-run-2", "container": "flash-sale-engine-app-2", "max_observed_cpu": "59.91%", "max_observed_memory": "327.2MiB / 640MiB"}
{"run": "kill-run-2", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "20.36%", "max_observed_memory": "11.67MiB / 64MiB"}
{"run": "kill-run-2", "container": "flash-sale-engine-app-1", "max_observed_cpu": "309.39%", "max_observed_memory": "328.4MiB / 640MiB"}
{"run": "kill-run-2", "container": "flash_rabbitmq", "max_observed_cpu": "250.22%", "max_observed_memory": "263.9MiB / 384MiB"}
{"run": "kill-run-2", "container": "flash_redis", "max_observed_cpu": "12.16%", "max_observed_memory": "21.98MiB / 384MiB"}
{"run": "kill-run-2", "container": "flash_postgres", "max_observed_cpu": "43.00%", "max_observed_memory": "93.3MiB / 384MiB"}
{"run": "kill-run-3", "container": "flash-sale-engine-app-2", "max_observed_cpu": "53.75%", "max_observed_memory": "328.2MiB / 640MiB"}
{"run": "kill-run-3", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "20.15%", "max_observed_memory": "11.7MiB / 64MiB"}
{"run": "kill-run-3", "container": "flash-sale-engine-app-1", "max_observed_cpu": "213.12%", "max_observed_memory": "319.3MiB / 640MiB"}
{"run": "kill-run-3", "container": "flash_rabbitmq", "max_observed_cpu": "222.31%", "max_observed_memory": "207.8MiB / 384MiB"}
{"run": "kill-run-3", "container": "flash_redis", "max_observed_cpu": "5.88%", "max_observed_memory": "22.26MiB / 384MiB"}
{"run": "kill-run-3", "container": "flash_postgres", "max_observed_cpu": "37.26%", "max_observed_memory": "94.05MiB / 384MiB"}
{"run": "rabbit-run-1", "container": "flash-sale-engine-app-2", "max_observed_cpu": "47.23%", "max_observed_memory": "332.6MiB / 640MiB"}
{"run": "rabbit-run-1", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "16.85%", "max_observed_memory": "11.63MiB / 64MiB"}
{"run": "rabbit-run-1", "container": "flash-sale-engine-app-1", "max_observed_cpu": "66.79%", "max_observed_memory": "330.5MiB / 640MiB"}
{"run": "rabbit-run-1", "container": "flash_rabbitmq", "max_observed_cpu": "217.68%", "max_observed_memory": "222.6MiB / 384MiB"}
{"run": "rabbit-run-1", "container": "flash_redis", "max_observed_cpu": "6.18%", "max_observed_memory": "22MiB / 384MiB"}
{"run": "rabbit-run-1", "container": "flash_postgres", "max_observed_cpu": "43.39%", "max_observed_memory": "95.15MiB / 384MiB"}
{"run": "rabbit-run-2", "container": "flash-sale-engine-app-2", "max_observed_cpu": "81.26%", "max_observed_memory": "333.3MiB / 640MiB"}
{"run": "rabbit-run-2", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "25.22%", "max_observed_memory": "11.39MiB / 64MiB"}
{"run": "rabbit-run-2", "container": "flash-sale-engine-app-1", "max_observed_cpu": "119.87%", "max_observed_memory": "326.5MiB / 640MiB"}
{"run": "rabbit-run-2", "container": "flash_rabbitmq", "max_observed_cpu": "237.14%", "max_observed_memory": "264.2MiB / 384MiB"}
{"run": "rabbit-run-2", "container": "flash_redis", "max_observed_cpu": "6.51%", "max_observed_memory": "22.14MiB / 384MiB"}
{"run": "rabbit-run-2", "container": "flash_postgres", "max_observed_cpu": "57.91%", "max_observed_memory": "95.27MiB / 384MiB"}
{"run": "rabbit-run-3", "container": "flash-sale-engine-app-2", "max_observed_cpu": "68.14%", "max_observed_memory": "333.4MiB / 640MiB"}
{"run": "rabbit-run-3", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "21.24%", "max_observed_memory": "11.13MiB / 64MiB"}
{"run": "rabbit-run-3", "container": "flash-sale-engine-app-1", "max_observed_cpu": "60.50%", "max_observed_memory": "330.7MiB / 640MiB"}
{"run": "rabbit-run-3", "container": "flash_rabbitmq", "max_observed_cpu": "259.17%", "max_observed_memory": "237MiB / 384MiB"}
{"run": "rabbit-run-3", "container": "flash_redis", "max_observed_cpu": "10.96%", "max_observed_memory": "22.38MiB / 384MiB"}
{"run": "rabbit-run-3", "container": "flash_postgres", "max_observed_cpu": "54.21%", "max_observed_memory": "95.03MiB / 384MiB"}
{"run": "redis-observation", "container": "flash-sale-engine-app-2", "max_observed_cpu": "230.40%", "max_observed_memory": "368.7MiB / 640MiB"}
{"run": "redis-observation", "container": "flash-sale-engine-nginx-1", "max_observed_cpu": "28.47%", "max_observed_memory": "11.5MiB / 64MiB"}
{"run": "redis-observation", "container": "flash-sale-engine-app-1", "max_observed_cpu": "205.14%", "max_observed_memory": "335.9MiB / 640MiB"}
{"run": "redis-observation", "container": "flash_rabbitmq", "max_observed_cpu": "276.22%", "max_observed_memory": "241MiB / 384MiB"}
{"run": "redis-observation", "container": "flash_redis", "max_observed_cpu": "23.22%", "max_observed_memory": "17.18MiB / 384MiB"}
{"run": "redis-observation", "container": "flash_postgres", "max_observed_cpu": "58.97%", "max_observed_memory": "94.77MiB / 384MiB"}
```

### kill-run-1 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [kill-run-1](bench/results/faults-before/kill-run-1/).

```text
$ git rev-parse HEAD
started=2026-10-09T08:12:38.318536+00:00
4f9a9b6c983854a70a695287a04c3a3510dcef52

exit_code=0
ended=2026-10-09T08:12:38.320914+00:00

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=12.6ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.04%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.04%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 57250 475.992957/s
    unexpected_responses...........: 0.04% 25 out of 57275

    HTTP
    http_req_duration..............: avg=4.15ms   min=45.74µs  med=3.52ms   max=790.42ms p(90)=5.86ms   p(95)=7.25ms   p(99)=12.6ms  
      { expected_response:true }...: avg=4.14ms   min=45.74µs  med=3.52ms   max=790.42ms p(90)=5.86ms   p(95)=7.23ms   p(99)=12.39ms 
    http_req_failed................: 0.04% 25 out of 57276
    http_reqs......................: 57276 476.209129/s

    EXECUTION
    iteration_duration.............: avg=104.78ms min=101.33ms med=104.16ms max=890.68ms p(90)=106.54ms p(95)=107.95ms p(99)=113.36ms
    iterations.....................: 57275 476.200814/s
    vus............................: 50    min=50          max=50
    vus_max........................: 50    min=50          max=50

    NETWORK
    data_received..................: 18 MB 148 kB/s
    data_sent......................: 12 MB 100 kB/s




exit_code=0

```

Reconciliation:

```json
{
  "http_202": 57250,
  "confirmed": 57250,
  "redis_stock": 42750,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 0
}

```

```text
$ docker start 7584501b57d50df6ff2d0e7628287602db45d357d3e06c51051c8cca57cd7df2
started=2026-10-09T08:13:56.363056+00:00
7584501b57d50df6ff2d0e7628287602db45d357d3e06c51051c8cca57cd7df2

exit_code=0
ended=2026-10-09T08:13:56.732754+00:00

```

```text
$ docker kill -s KILL 7584501b57d50df6ff2d0e7628287602db45d357d3e06c51051c8cca57cd7df2
started=2026-10-09T08:13:35.568957+00:00
7584501b57d50df6ff2d0e7628287602db45d357d3e06c51051c8cca57cd7df2

exit_code=0
ended=2026-10-09T08:13:36.360495+00:00

```

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```

### kill-run-2 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [kill-run-2](bench/results/faults-before/kill-run-2/).

```text
$ git rev-parse HEAD
started=2026-10-09T08:14:59.565769+00:00
4f9a9b6c983854a70a695287a04c3a3510dcef52

exit_code=0
ended=2026-10-09T08:14:59.582491+00:00

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=9.81ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.01%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.01%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 57243 476.482211/s
    unexpected_responses...........: 0.01% 6 out of 57249

    HTTP
    http_req_duration..............: avg=4.26ms   min=822.49µs med=3.63ms   max=686.18ms p(90)=5.93ms   p(95)=6.93ms  p(99)=9.81ms  
      { expected_response:true }...: avg=4.26ms   min=822.49µs med=3.63ms   max=686.18ms p(90)=5.93ms   p(95)=6.93ms  p(99)=9.81ms  
    http_req_failed................: 0.01% 6 out of 57250
    http_reqs......................: 57250 476.540478/s

    EXECUTION
    iteration_duration.............: avg=104.85ms min=101.33ms med=104.23ms max=786.87ms p(90)=106.58ms p(95)=107.6ms p(99)=110.47ms
    iterations.....................: 57249 476.532154/s
    vus............................: 50    min=50         max=50
    vus_max........................: 50    min=50         max=50

    NETWORK
    data_received..................: 18 MB 148 kB/s
    data_sent......................: 12 MB 100 kB/s




exit_code=0

```

Reconciliation:

```json
{
  "http_202": 57243,
  "confirmed": 57243,
  "redis_stock": 42757,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 0
}

```

```text
$ docker start 7584501b57d50df6ff2d0e7628287602db45d357d3e06c51051c8cca57cd7df2
started=2026-10-09T08:16:06.091294+00:00
7584501b57d50df6ff2d0e7628287602db45d357d3e06c51051c8cca57cd7df2

exit_code=0
ended=2026-10-09T08:16:06.405701+00:00

```

```text
$ docker kill -s KILL 7584501b57d50df6ff2d0e7628287602db45d357d3e06c51051c8cca57cd7df2
started=2026-10-09T08:15:45.432426+00:00
7584501b57d50df6ff2d0e7628287602db45d357d3e06c51051c8cca57cd7df2

exit_code=0
ended=2026-10-09T08:15:46.092864+00:00

```

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```

### kill-run-3 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [kill-run-3](bench/results/faults-before/kill-run-3/).

```text
$ git rev-parse HEAD
started=2026-10-09T08:17:08.590877+00:00
4f9a9b6c983854a70a695287a04c3a3510dcef52

exit_code=0
ended=2026-10-09T08:17:08.600617+00:00

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=9.8ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.04%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.04%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 57321 477.441993/s
    unexpected_responses...........: 0.04% 25 out of 57346

    HTTP
    http_req_duration..............: avg=4.02ms   min=136.28µs med=3.67ms   max=648.01ms p(90)=5.78ms   p(95)=6.72ms   p(99)=9.8ms   
      { expected_response:true }...: avg=4.01ms   min=136.28µs med=3.67ms   max=648.01ms p(90)=5.78ms   p(95)=6.71ms   p(99)=9.68ms  
    http_req_failed................: 0.04% 25 out of 57347
    http_reqs......................: 57347 477.658554/s

    EXECUTION
    iteration_duration.............: avg=104.62ms min=101.26ms med=104.28ms max=748.85ms p(90)=106.44ms p(95)=107.38ms p(99)=110.41ms
    iterations.....................: 57346 477.650224/s
    vus............................: 50    min=50          max=50
    vus_max........................: 50    min=50          max=50

    NETWORK
    data_received..................: 18 MB 148 kB/s
    data_sent......................: 12 MB 101 kB/s




exit_code=0

```

Reconciliation:

```json
{
  "http_202": 57321,
  "confirmed": 57321,
  "redis_stock": 42679,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 0
}

```

```text
$ docker start 7584501b57d50df6ff2d0e7628287602db45d357d3e06c51051c8cca57cd7df2
started=2026-10-09T08:18:15.081348+00:00
7584501b57d50df6ff2d0e7628287602db45d357d3e06c51051c8cca57cd7df2

exit_code=0
ended=2026-10-09T08:18:15.396637+00:00

```

```text
$ docker kill -s KILL 7584501b57d50df6ff2d0e7628287602db45d357d3e06c51051c8cca57cd7df2
started=2026-10-09T08:17:54.320276+00:00
7584501b57d50df6ff2d0e7628287602db45d357d3e06c51051c8cca57cd7df2

exit_code=0
ended=2026-10-09T08:17:55.080601+00:00

```

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```

### rabbit-run-1 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [rabbit-run-1](bench/results/faults-before/rabbit-run-1/).

```text
$ git rev-parse HEAD
started=2026-10-09T08:19:17.613928+00:00
4f9a9b6c983854a70a695287a04c3a3510dcef52

exit_code=0
ended=2026-10-09T08:19:17.620798+00:00

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=10.29ms

    http_req_failed
    ✗ 'rate<0.01' rate=8.93%

    unexpected_responses
    ✗ 'rate<0.01' rate=8.93%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 51696 430.557937/s
    unexpected_responses...........: 8.93% 5075 out of 56771

    HTTP
    http_req_duration..............: avg=5.09ms   min=429.25µs med=3.73ms   max=1.02s    p(90)=5.87ms  p(95)=6.83ms   p(99)=10.29ms
      { expected_response:true }...: avg=4.03ms   min=429.25µs med=3.66ms   max=131.12ms p(90)=5.68ms  p(95)=6.54ms   p(99)=9.32ms 
    http_req_failed................: 8.93% 5075 out of 56772
    http_reqs......................: 56772 472.834168/s

    EXECUTION
    iteration_duration.............: avg=105.69ms min=101.44ms med=104.35ms max=1.12s    p(90)=106.5ms p(95)=107.44ms p(99)=110.9ms
    iterations.....................: 56771 472.82584/s
    vus............................: 50    min=50            max=50
    vus_max........................: 50    min=50            max=50

    NETWORK
    data_received..................: 18 MB 146 kB/s
    data_sent......................: 12 MB 101 kB/s



time="2026-10-09T08:21:18Z" level=error msg="thresholds on metrics 'http_req_failed, unexpected_responses' have been crossed"

exit_code=99

```

Reconciliation:

```json
{
  "http_202": 51696,
  "confirmed": 51696,
  "redis_stock": 48304,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 99
}

```

```text
$ docker restart flash_rabbitmq
started=2026-10-09T08:20:03.347793+00:00
flash_rabbitmq

exit_code=0
ended=2026-10-09T08:20:05.980090+00:00

```

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```

### rabbit-run-2 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [rabbit-run-2](bench/results/faults-before/rabbit-run-2/).

```text
$ git rev-parse HEAD
started=2026-10-09T08:21:27.740771+00:00
4f9a9b6c983854a70a695287a04c3a3510dcef52

exit_code=0
ended=2026-10-09T08:21:27.756408+00:00

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=11.03ms

    http_req_failed
    ✗ 'rate<0.01' rate=9.33%

    unexpected_responses
    ✗ 'rate<0.01' rate=9.33%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 51527 428.910671/s
    unexpected_responses...........: 9.33% 5306 out of 56833

    HTTP
    http_req_duration..............: avg=4.94ms  min=1.22ms  med=3.62ms  max=1.01s   p(90)=5.94ms   p(95)=7.13ms   p(99)=11.03ms 
      { expected_response:true }...: avg=3.97ms  min=1.22ms  med=3.55ms  max=62.65ms p(90)=5.86ms   p(95)=7ms      p(99)=10.35ms 
    http_req_failed................: 9.33% 5306 out of 56834
    http_reqs......................: 56834 473.086131/s

    EXECUTION
    iteration_duration.............: avg=105.6ms min=101.5ms med=104.3ms max=1.11s   p(90)=106.65ms p(95)=107.83ms p(99)=111.75ms
    iterations.....................: 56833 473.077807/s
    vus............................: 50    min=50            max=50
    vus_max........................: 50    min=50            max=50

    NETWORK
    data_received..................: 18 MB 146 kB/s
    data_sent......................: 12 MB 101 kB/s



time="2026-10-09T08:23:29Z" level=error msg="thresholds on metrics 'http_req_failed, unexpected_responses' have been crossed"

exit_code=99

```

Reconciliation:

```json
{
  "http_202": 51527,
  "confirmed": 51527,
  "redis_stock": 48473,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 99
}

```

```text
$ docker restart flash_rabbitmq
started=2026-10-09T08:22:13.616559+00:00
flash_rabbitmq

exit_code=0
ended=2026-10-09T08:22:16.119119+00:00

```

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```

### rabbit-run-3 raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [rabbit-run-3](bench/results/faults-before/rabbit-run-3/).

```text
$ git rev-parse HEAD
started=2026-10-09T08:23:37.794428+00:00
4f9a9b6c983854a70a695287a04c3a3510dcef52

exit_code=0
ended=2026-10-09T08:23:37.810342+00:00

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=10.83ms

    http_req_failed
    ✗ 'rate<0.01' rate=11.74%

    unexpected_responses
    ✗ 'rate<0.01' rate=11.74%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 50139  417.170208/s
    unexpected_responses...........: 11.74% 6671 out of 56810

    HTTP
    http_req_duration..............: avg=4.96ms   min=1.13ms   med=3.57ms   max=1.02s    p(90)=5.81ms   p(95)=6.93ms   p(99)=10.83ms 
      { expected_response:true }...: avg=3.89ms   min=1.13ms   med=3.5ms    max=118.53ms p(90)=5.52ms   p(95)=6.5ms    p(99)=9.37ms  
    http_req_failed................: 11.74% 6671 out of 56811
    http_reqs......................: 56811  472.683075/s

    EXECUTION
    iteration_duration.............: avg=105.65ms min=101.53ms med=104.27ms max=1.12s    p(90)=106.55ms p(95)=107.72ms p(99)=111.62ms
    iterations.....................: 56810  472.674755/s
    vus............................: 50     min=50            max=50
    vus_max........................: 50     min=50            max=50

    NETWORK
    data_received..................: 18 MB  146 kB/s
    data_sent......................: 12 MB  101 kB/s



time="2026-10-09T08:25:40Z" level=error msg="thresholds on metrics 'http_req_failed, unexpected_responses' have been crossed"

exit_code=99

```

Reconciliation:

```json
{
  "http_202": 50139,
  "confirmed": 50139,
  "redis_stock": 49861,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 99
}

```

```text
$ docker restart flash_rabbitmq
started=2026-10-09T08:24:24.148649+00:00
flash_rabbitmq

exit_code=0
ended=2026-10-09T08:24:26.908162+00:00

```

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```

### redis-observation raw output

Commands, revision, configuration, accepted-key ledger, Postgres rows, Redis reservations and resource snapshots: [redis-observation](bench/results/faults-before/redis-observation/).

```text
$ git rev-parse HEAD
started=2026-10-09T08:25:48.262997+00:00
4f9a9b6c983854a70a695287a04c3a3510dcef52

exit_code=0
ended=2026-10-09T08:25:48.276588+00:00

  █ THRESHOLDS 

    http_req_duration
    ✓ 'p(99)<1000' p(99)=14.58ms

    http_req_failed
    ✓ 'rate<0.01' rate=0.07%

    unexpected_responses
    ✓ 'rate<0.01' rate=0.07%


  █ TOTAL RESULTS 

    CUSTOM
    accepted_orders................: 54239 451.772838/s
    unexpected_responses...........: 0.07% 40 out of 54279

    HTTP
    http_req_duration..............: avg=9.85ms   min=1.16ms   med=3.71ms   max=6.14s p(90)=6.19ms   p(95)=7.71ms   p(99)=14.58ms 
      { expected_response:true }...: avg=6.98ms   min=1.16ms   med=3.71ms   max=3.21s p(90)=6.17ms   p(95)=7.67ms   p(99)=14.05ms 
    http_req_failed................: 0.07% 40 out of 54280
    http_reqs......................: 54280 452.11434/s

    EXECUTION
    iteration_duration.............: avg=110.53ms min=101.41ms med=104.41ms max=6.24s p(90)=106.92ms p(95)=108.53ms p(99)=115.67ms
    iterations.....................: 54279 452.10601/s
    vus............................: 50    min=50          max=50
    vus_max........................: 50    min=50          max=50

    NETWORK
    data_received..................: 17 MB 140 kB/s
    data_sent......................: 12 MB 98 kB/s




exit_code=0

```

Reconciliation:

```json
{
  "http_202": 54239,
  "confirmed": 54239,
  "redis_stock": 45761,
  "oversold_count": 0,
  "duplicate_orders": 0,
  "lost_orders": 0,
  "stuck_reservations": 0,
  "unacknowledged_confirmed": 0,
  "stock_accounting_excess": 0,
  "drained": true,
  "k6_exit": 0
}

```

```text
$ docker restart flash_redis
started=2026-10-09T08:26:34.111273+00:00
flash_redis

exit_code=0
ended=2026-10-09T08:26:36.911301+00:00

```

Queue drain final output (full polling output retained):

```text
order.queue	0	0
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name	messages_ready	messages_unacknowledged
order.dlq	0	0
order.queue	0	0
```


OOMKilled flags from executed Docker inspect:

```text
('kill-run-1', '/flash-sale-engine-app-1', False)
('kill-run-1', '/flash-sale-engine-app-2', False)
('kill-run-1', '/flash_postgres', False)
('kill-run-1', '/flash_redis', False)
('kill-run-1', '/flash_rabbitmq', False)
('kill-run-2', '/flash-sale-engine-app-1', False)
('kill-run-2', '/flash-sale-engine-app-2', False)
('kill-run-2', '/flash_postgres', False)
('kill-run-2', '/flash_redis', False)
('kill-run-2', '/flash_rabbitmq', False)
('kill-run-3', '/flash-sale-engine-app-1', False)
('kill-run-3', '/flash-sale-engine-app-2', False)
('kill-run-3', '/flash_postgres', False)
('kill-run-3', '/flash_redis', False)
('kill-run-3', '/flash_rabbitmq', False)
('rabbit-run-1', '/flash-sale-engine-app-1', False)
('rabbit-run-1', '/flash-sale-engine-app-2', False)
('rabbit-run-1', '/flash_postgres', False)
('rabbit-run-1', '/flash_redis', False)
('rabbit-run-1', '/flash_rabbitmq', False)
('rabbit-run-2', '/flash-sale-engine-app-1', False)
('rabbit-run-2', '/flash-sale-engine-app-2', False)
('rabbit-run-2', '/flash_postgres', False)
('rabbit-run-2', '/flash_redis', False)
('rabbit-run-2', '/flash_rabbitmq', False)
('rabbit-run-3', '/flash-sale-engine-app-1', False)
('rabbit-run-3', '/flash-sale-engine-app-2', False)
('rabbit-run-3', '/flash_postgres', False)
('rabbit-run-3', '/flash_redis', False)
('rabbit-run-3', '/flash_rabbitmq', False)
('redis-observation', '/flash-sale-engine-app-1', False)
('redis-observation', '/flash-sale-engine-app-2', False)
('redis-observation', '/flash_postgres', False)
('redis-observation', '/flash_redis', False)
('redis-observation', '/flash_rabbitmq', False)
```

CPU throttling and memory OOM counters (raw before/after cgroup snapshots):

```text
('kill-run-1', 'before-cgroup-0.txt', 'nr_throttled 0')
('kill-run-1', 'before-cgroup-0.txt', 'throttled_usec 0')
('kill-run-1', 'before-cgroup-0.txt', 'oom 0')
('kill-run-1', 'before-cgroup-0.txt', 'oom_kill 0')
('kill-run-1', 'after-cgroup-0.txt', 'nr_throttled 0')
('kill-run-1', 'after-cgroup-0.txt', 'throttled_usec 0')
('kill-run-1', 'after-cgroup-0.txt', 'oom 0')
('kill-run-1', 'after-cgroup-0.txt', 'oom_kill 0')
('kill-run-1', 'before-cgroup-2.txt', 'nr_throttled 0')
('kill-run-1', 'before-cgroup-2.txt', 'throttled_usec 0')
('kill-run-1', 'before-cgroup-2.txt', 'oom 0')
('kill-run-1', 'before-cgroup-2.txt', 'oom_kill 0')
('kill-run-1', 'before-cgroup-1.txt', 'nr_throttled 0')
('kill-run-1', 'before-cgroup-1.txt', 'throttled_usec 0')
('kill-run-1', 'before-cgroup-1.txt', 'oom 0')
('kill-run-1', 'before-cgroup-1.txt', 'oom_kill 0')
('kill-run-1', 'after-cgroup-4.txt', 'nr_throttled 0')
('kill-run-1', 'after-cgroup-4.txt', 'throttled_usec 0')
('kill-run-1', 'after-cgroup-4.txt', 'oom 0')
('kill-run-1', 'after-cgroup-4.txt', 'oom_kill 0')
('kill-run-1', 'before-cgroup-3.txt', 'nr_throttled 0')
('kill-run-1', 'before-cgroup-3.txt', 'throttled_usec 0')
('kill-run-1', 'before-cgroup-3.txt', 'oom 0')
('kill-run-1', 'before-cgroup-3.txt', 'oom_kill 0')
('kill-run-1', 'after-cgroup-2.txt', 'nr_throttled 0')
('kill-run-1', 'after-cgroup-2.txt', 'throttled_usec 0')
('kill-run-1', 'after-cgroup-2.txt', 'oom 0')
('kill-run-1', 'after-cgroup-2.txt', 'oom_kill 0')
('kill-run-1', 'before-cgroup-4.txt', 'nr_throttled 0')
('kill-run-1', 'before-cgroup-4.txt', 'throttled_usec 0')
('kill-run-1', 'before-cgroup-4.txt', 'oom 0')
('kill-run-1', 'before-cgroup-4.txt', 'oom_kill 0')
('kill-run-1', 'after-cgroup-1.txt', 'nr_throttled 0')
('kill-run-1', 'after-cgroup-1.txt', 'throttled_usec 0')
('kill-run-1', 'after-cgroup-1.txt', 'oom 0')
('kill-run-1', 'after-cgroup-1.txt', 'oom_kill 0')
('kill-run-1', 'after-cgroup-3.txt', 'nr_throttled 0')
('kill-run-1', 'after-cgroup-3.txt', 'throttled_usec 0')
('kill-run-1', 'after-cgroup-3.txt', 'oom 0')
('kill-run-1', 'after-cgroup-3.txt', 'oom_kill 0')
('kill-run-2', 'before-cgroup-0.txt', 'nr_throttled 0')
('kill-run-2', 'before-cgroup-0.txt', 'throttled_usec 0')
('kill-run-2', 'before-cgroup-0.txt', 'oom 0')
('kill-run-2', 'before-cgroup-0.txt', 'oom_kill 0')
('kill-run-2', 'after-cgroup-0.txt', 'nr_throttled 0')
('kill-run-2', 'after-cgroup-0.txt', 'throttled_usec 0')
('kill-run-2', 'after-cgroup-0.txt', 'oom 0')
('kill-run-2', 'after-cgroup-0.txt', 'oom_kill 0')
('kill-run-2', 'before-cgroup-2.txt', 'nr_throttled 0')
('kill-run-2', 'before-cgroup-2.txt', 'throttled_usec 0')
('kill-run-2', 'before-cgroup-2.txt', 'oom 0')
('kill-run-2', 'before-cgroup-2.txt', 'oom_kill 0')
('kill-run-2', 'before-cgroup-1.txt', 'nr_throttled 0')
('kill-run-2', 'before-cgroup-1.txt', 'throttled_usec 0')
('kill-run-2', 'before-cgroup-1.txt', 'oom 0')
('kill-run-2', 'before-cgroup-1.txt', 'oom_kill 0')
('kill-run-2', 'after-cgroup-4.txt', 'nr_throttled 0')
('kill-run-2', 'after-cgroup-4.txt', 'throttled_usec 0')
('kill-run-2', 'after-cgroup-4.txt', 'oom 0')
('kill-run-2', 'after-cgroup-4.txt', 'oom_kill 0')
('kill-run-2', 'before-cgroup-3.txt', 'nr_throttled 0')
('kill-run-2', 'before-cgroup-3.txt', 'throttled_usec 0')
('kill-run-2', 'before-cgroup-3.txt', 'oom 0')
('kill-run-2', 'before-cgroup-3.txt', 'oom_kill 0')
('kill-run-2', 'after-cgroup-2.txt', 'nr_throttled 0')
('kill-run-2', 'after-cgroup-2.txt', 'throttled_usec 0')
('kill-run-2', 'after-cgroup-2.txt', 'oom 0')
('kill-run-2', 'after-cgroup-2.txt', 'oom_kill 0')
('kill-run-2', 'before-cgroup-4.txt', 'nr_throttled 0')
('kill-run-2', 'before-cgroup-4.txt', 'throttled_usec 0')
('kill-run-2', 'before-cgroup-4.txt', 'oom 0')
('kill-run-2', 'before-cgroup-4.txt', 'oom_kill 0')
('kill-run-2', 'after-cgroup-1.txt', 'nr_throttled 0')
('kill-run-2', 'after-cgroup-1.txt', 'throttled_usec 0')
('kill-run-2', 'after-cgroup-1.txt', 'oom 0')
('kill-run-2', 'after-cgroup-1.txt', 'oom_kill 0')
('kill-run-2', 'after-cgroup-3.txt', 'nr_throttled 0')
('kill-run-2', 'after-cgroup-3.txt', 'throttled_usec 0')
('kill-run-2', 'after-cgroup-3.txt', 'oom 0')
('kill-run-2', 'after-cgroup-3.txt', 'oom_kill 0')
('kill-run-3', 'before-cgroup-0.txt', 'nr_throttled 0')
('kill-run-3', 'before-cgroup-0.txt', 'throttled_usec 0')
('kill-run-3', 'before-cgroup-0.txt', 'oom 0')
('kill-run-3', 'before-cgroup-0.txt', 'oom_kill 0')
('kill-run-3', 'after-cgroup-0.txt', 'nr_throttled 0')
('kill-run-3', 'after-cgroup-0.txt', 'throttled_usec 0')
('kill-run-3', 'after-cgroup-0.txt', 'oom 0')
('kill-run-3', 'after-cgroup-0.txt', 'oom_kill 0')
('kill-run-3', 'before-cgroup-2.txt', 'nr_throttled 0')
('kill-run-3', 'before-cgroup-2.txt', 'throttled_usec 0')
('kill-run-3', 'before-cgroup-2.txt', 'oom 0')
('kill-run-3', 'before-cgroup-2.txt', 'oom_kill 0')
('kill-run-3', 'before-cgroup-1.txt', 'nr_throttled 0')
('kill-run-3', 'before-cgroup-1.txt', 'throttled_usec 0')
('kill-run-3', 'before-cgroup-1.txt', 'oom 0')
('kill-run-3', 'before-cgroup-1.txt', 'oom_kill 0')
('kill-run-3', 'after-cgroup-4.txt', 'nr_throttled 0')
('kill-run-3', 'after-cgroup-4.txt', 'throttled_usec 0')
('kill-run-3', 'after-cgroup-4.txt', 'oom 0')
('kill-run-3', 'after-cgroup-4.txt', 'oom_kill 0')
('kill-run-3', 'before-cgroup-3.txt', 'nr_throttled 0')
('kill-run-3', 'before-cgroup-3.txt', 'throttled_usec 0')
('kill-run-3', 'before-cgroup-3.txt', 'oom 0')
('kill-run-3', 'before-cgroup-3.txt', 'oom_kill 0')
('kill-run-3', 'after-cgroup-2.txt', 'nr_throttled 0')
('kill-run-3', 'after-cgroup-2.txt', 'throttled_usec 0')
('kill-run-3', 'after-cgroup-2.txt', 'oom 0')
('kill-run-3', 'after-cgroup-2.txt', 'oom_kill 0')
('kill-run-3', 'before-cgroup-4.txt', 'nr_throttled 0')
('kill-run-3', 'before-cgroup-4.txt', 'throttled_usec 0')
('kill-run-3', 'before-cgroup-4.txt', 'oom 0')
('kill-run-3', 'before-cgroup-4.txt', 'oom_kill 0')
('kill-run-3', 'after-cgroup-1.txt', 'nr_throttled 0')
('kill-run-3', 'after-cgroup-1.txt', 'throttled_usec 0')
('kill-run-3', 'after-cgroup-1.txt', 'oom 0')
('kill-run-3', 'after-cgroup-1.txt', 'oom_kill 0')
('kill-run-3', 'after-cgroup-3.txt', 'nr_throttled 0')
('kill-run-3', 'after-cgroup-3.txt', 'throttled_usec 0')
('kill-run-3', 'after-cgroup-3.txt', 'oom 0')
('kill-run-3', 'after-cgroup-3.txt', 'oom_kill 0')
('rabbit-run-1', 'before-cgroup-0.txt', 'nr_throttled 0')
('rabbit-run-1', 'before-cgroup-0.txt', 'throttled_usec 0')
('rabbit-run-1', 'before-cgroup-0.txt', 'oom 0')
('rabbit-run-1', 'before-cgroup-0.txt', 'oom_kill 0')
('rabbit-run-1', 'after-cgroup-0.txt', 'nr_throttled 0')
('rabbit-run-1', 'after-cgroup-0.txt', 'throttled_usec 0')
('rabbit-run-1', 'after-cgroup-0.txt', 'oom 0')
('rabbit-run-1', 'after-cgroup-0.txt', 'oom_kill 0')
('rabbit-run-1', 'before-cgroup-2.txt', 'nr_throttled 0')
('rabbit-run-1', 'before-cgroup-2.txt', 'throttled_usec 0')
('rabbit-run-1', 'before-cgroup-2.txt', 'oom 0')
('rabbit-run-1', 'before-cgroup-2.txt', 'oom_kill 0')
('rabbit-run-1', 'before-cgroup-1.txt', 'nr_throttled 0')
('rabbit-run-1', 'before-cgroup-1.txt', 'throttled_usec 0')
('rabbit-run-1', 'before-cgroup-1.txt', 'oom 0')
('rabbit-run-1', 'before-cgroup-1.txt', 'oom_kill 0')
('rabbit-run-1', 'after-cgroup-4.txt', 'nr_throttled 0')
('rabbit-run-1', 'after-cgroup-4.txt', 'throttled_usec 0')
('rabbit-run-1', 'after-cgroup-4.txt', 'oom 0')
('rabbit-run-1', 'after-cgroup-4.txt', 'oom_kill 0')
('rabbit-run-1', 'before-cgroup-3.txt', 'nr_throttled 0')
('rabbit-run-1', 'before-cgroup-3.txt', 'throttled_usec 0')
('rabbit-run-1', 'before-cgroup-3.txt', 'oom 0')
('rabbit-run-1', 'before-cgroup-3.txt', 'oom_kill 0')
('rabbit-run-1', 'after-cgroup-2.txt', 'nr_throttled 0')
('rabbit-run-1', 'after-cgroup-2.txt', 'throttled_usec 0')
('rabbit-run-1', 'after-cgroup-2.txt', 'oom 0')
('rabbit-run-1', 'after-cgroup-2.txt', 'oom_kill 0')
('rabbit-run-1', 'before-cgroup-4.txt', 'nr_throttled 0')
('rabbit-run-1', 'before-cgroup-4.txt', 'throttled_usec 0')
('rabbit-run-1', 'before-cgroup-4.txt', 'oom 0')
('rabbit-run-1', 'before-cgroup-4.txt', 'oom_kill 0')
('rabbit-run-1', 'after-cgroup-1.txt', 'nr_throttled 0')
('rabbit-run-1', 'after-cgroup-1.txt', 'throttled_usec 0')
('rabbit-run-1', 'after-cgroup-1.txt', 'oom 0')
('rabbit-run-1', 'after-cgroup-1.txt', 'oom_kill 0')
('rabbit-run-1', 'after-cgroup-3.txt', 'nr_throttled 0')
('rabbit-run-1', 'after-cgroup-3.txt', 'throttled_usec 0')
('rabbit-run-1', 'after-cgroup-3.txt', 'oom 0')
('rabbit-run-1', 'after-cgroup-3.txt', 'oom_kill 0')
('rabbit-run-2', 'before-cgroup-0.txt', 'nr_throttled 0')
('rabbit-run-2', 'before-cgroup-0.txt', 'throttled_usec 0')
('rabbit-run-2', 'before-cgroup-0.txt', 'oom 0')
('rabbit-run-2', 'before-cgroup-0.txt', 'oom_kill 0')
('rabbit-run-2', 'after-cgroup-0.txt', 'nr_throttled 0')
('rabbit-run-2', 'after-cgroup-0.txt', 'throttled_usec 0')
('rabbit-run-2', 'after-cgroup-0.txt', 'oom 0')
('rabbit-run-2', 'after-cgroup-0.txt', 'oom_kill 0')
('rabbit-run-2', 'before-cgroup-2.txt', 'nr_throttled 0')
('rabbit-run-2', 'before-cgroup-2.txt', 'throttled_usec 0')
('rabbit-run-2', 'before-cgroup-2.txt', 'oom 0')
('rabbit-run-2', 'before-cgroup-2.txt', 'oom_kill 0')
('rabbit-run-2', 'before-cgroup-1.txt', 'nr_throttled 0')
('rabbit-run-2', 'before-cgroup-1.txt', 'throttled_usec 0')
('rabbit-run-2', 'before-cgroup-1.txt', 'oom 0')
('rabbit-run-2', 'before-cgroup-1.txt', 'oom_kill 0')
('rabbit-run-2', 'after-cgroup-4.txt', 'nr_throttled 0')
('rabbit-run-2', 'after-cgroup-4.txt', 'throttled_usec 0')
('rabbit-run-2', 'after-cgroup-4.txt', 'oom 0')
('rabbit-run-2', 'after-cgroup-4.txt', 'oom_kill 0')
('rabbit-run-2', 'before-cgroup-3.txt', 'nr_throttled 0')
('rabbit-run-2', 'before-cgroup-3.txt', 'throttled_usec 0')
('rabbit-run-2', 'before-cgroup-3.txt', 'oom 0')
('rabbit-run-2', 'before-cgroup-3.txt', 'oom_kill 0')
('rabbit-run-2', 'after-cgroup-2.txt', 'nr_throttled 0')
('rabbit-run-2', 'after-cgroup-2.txt', 'throttled_usec 0')
('rabbit-run-2', 'after-cgroup-2.txt', 'oom 0')
('rabbit-run-2', 'after-cgroup-2.txt', 'oom_kill 0')
('rabbit-run-2', 'before-cgroup-4.txt', 'nr_throttled 0')
('rabbit-run-2', 'before-cgroup-4.txt', 'throttled_usec 0')
('rabbit-run-2', 'before-cgroup-4.txt', 'oom 0')
('rabbit-run-2', 'before-cgroup-4.txt', 'oom_kill 0')
('rabbit-run-2', 'after-cgroup-1.txt', 'nr_throttled 0')
('rabbit-run-2', 'after-cgroup-1.txt', 'throttled_usec 0')
('rabbit-run-2', 'after-cgroup-1.txt', 'oom 0')
('rabbit-run-2', 'after-cgroup-1.txt', 'oom_kill 0')
('rabbit-run-2', 'after-cgroup-3.txt', 'nr_throttled 0')
('rabbit-run-2', 'after-cgroup-3.txt', 'throttled_usec 0')
('rabbit-run-2', 'after-cgroup-3.txt', 'oom 0')
('rabbit-run-2', 'after-cgroup-3.txt', 'oom_kill 0')
('rabbit-run-3', 'before-cgroup-0.txt', 'nr_throttled 0')
('rabbit-run-3', 'before-cgroup-0.txt', 'throttled_usec 0')
('rabbit-run-3', 'before-cgroup-0.txt', 'oom 0')
('rabbit-run-3', 'before-cgroup-0.txt', 'oom_kill 0')
('rabbit-run-3', 'after-cgroup-0.txt', 'nr_throttled 0')
('rabbit-run-3', 'after-cgroup-0.txt', 'throttled_usec 0')
('rabbit-run-3', 'after-cgroup-0.txt', 'oom 0')
('rabbit-run-3', 'after-cgroup-0.txt', 'oom_kill 0')
('rabbit-run-3', 'before-cgroup-2.txt', 'nr_throttled 0')
('rabbit-run-3', 'before-cgroup-2.txt', 'throttled_usec 0')
('rabbit-run-3', 'before-cgroup-2.txt', 'oom 0')
('rabbit-run-3', 'before-cgroup-2.txt', 'oom_kill 0')
('rabbit-run-3', 'before-cgroup-1.txt', 'nr_throttled 0')
('rabbit-run-3', 'before-cgroup-1.txt', 'throttled_usec 0')
('rabbit-run-3', 'before-cgroup-1.txt', 'oom 0')
('rabbit-run-3', 'before-cgroup-1.txt', 'oom_kill 0')
('rabbit-run-3', 'after-cgroup-4.txt', 'nr_throttled 0')
('rabbit-run-3', 'after-cgroup-4.txt', 'throttled_usec 0')
('rabbit-run-3', 'after-cgroup-4.txt', 'oom 0')
('rabbit-run-3', 'after-cgroup-4.txt', 'oom_kill 0')
('rabbit-run-3', 'before-cgroup-3.txt', 'nr_throttled 0')
('rabbit-run-3', 'before-cgroup-3.txt', 'throttled_usec 0')
('rabbit-run-3', 'before-cgroup-3.txt', 'oom 0')
('rabbit-run-3', 'before-cgroup-3.txt', 'oom_kill 0')
('rabbit-run-3', 'after-cgroup-2.txt', 'nr_throttled 0')
('rabbit-run-3', 'after-cgroup-2.txt', 'throttled_usec 0')
('rabbit-run-3', 'after-cgroup-2.txt', 'oom 0')
('rabbit-run-3', 'after-cgroup-2.txt', 'oom_kill 0')
('rabbit-run-3', 'before-cgroup-4.txt', 'nr_throttled 0')
('rabbit-run-3', 'before-cgroup-4.txt', 'throttled_usec 0')
('rabbit-run-3', 'before-cgroup-4.txt', 'oom 0')
('rabbit-run-3', 'before-cgroup-4.txt', 'oom_kill 0')
('rabbit-run-3', 'after-cgroup-1.txt', 'nr_throttled 0')
('rabbit-run-3', 'after-cgroup-1.txt', 'throttled_usec 0')
('rabbit-run-3', 'after-cgroup-1.txt', 'oom 0')
('rabbit-run-3', 'after-cgroup-1.txt', 'oom_kill 0')
('rabbit-run-3', 'after-cgroup-3.txt', 'nr_throttled 0')
('rabbit-run-3', 'after-cgroup-3.txt', 'throttled_usec 0')
('rabbit-run-3', 'after-cgroup-3.txt', 'oom 0')
('rabbit-run-3', 'after-cgroup-3.txt', 'oom_kill 0')
('redis-observation', 'before-cgroup-0.txt', 'nr_throttled 0')
('redis-observation', 'before-cgroup-0.txt', 'throttled_usec 0')
('redis-observation', 'before-cgroup-0.txt', 'oom 0')
('redis-observation', 'before-cgroup-0.txt', 'oom_kill 0')
('redis-observation', 'after-cgroup-0.txt', 'nr_throttled 0')
('redis-observation', 'after-cgroup-0.txt', 'throttled_usec 0')
('redis-observation', 'after-cgroup-0.txt', 'oom 0')
('redis-observation', 'after-cgroup-0.txt', 'oom_kill 0')
('redis-observation', 'before-cgroup-2.txt', 'nr_throttled 0')
('redis-observation', 'before-cgroup-2.txt', 'throttled_usec 0')
('redis-observation', 'before-cgroup-2.txt', 'oom 0')
('redis-observation', 'before-cgroup-2.txt', 'oom_kill 0')
('redis-observation', 'before-cgroup-1.txt', 'nr_throttled 0')
('redis-observation', 'before-cgroup-1.txt', 'throttled_usec 0')
('redis-observation', 'before-cgroup-1.txt', 'oom 0')
('redis-observation', 'before-cgroup-1.txt', 'oom_kill 0')
('redis-observation', 'after-cgroup-4.txt', 'nr_throttled 0')
('redis-observation', 'after-cgroup-4.txt', 'throttled_usec 0')
('redis-observation', 'after-cgroup-4.txt', 'oom 0')
('redis-observation', 'after-cgroup-4.txt', 'oom_kill 0')
('redis-observation', 'before-cgroup-3.txt', 'nr_throttled 0')
('redis-observation', 'before-cgroup-3.txt', 'throttled_usec 0')
('redis-observation', 'before-cgroup-3.txt', 'oom 0')
('redis-observation', 'before-cgroup-3.txt', 'oom_kill 0')
('redis-observation', 'after-cgroup-2.txt', 'nr_throttled 0')
('redis-observation', 'after-cgroup-2.txt', 'throttled_usec 0')
('redis-observation', 'after-cgroup-2.txt', 'oom 0')
('redis-observation', 'after-cgroup-2.txt', 'oom_kill 0')
('redis-observation', 'before-cgroup-4.txt', 'nr_throttled 0')
('redis-observation', 'before-cgroup-4.txt', 'throttled_usec 0')
('redis-observation', 'before-cgroup-4.txt', 'oom 0')
('redis-observation', 'before-cgroup-4.txt', 'oom_kill 0')
('redis-observation', 'after-cgroup-1.txt', 'nr_throttled 0')
('redis-observation', 'after-cgroup-1.txt', 'throttled_usec 0')
('redis-observation', 'after-cgroup-1.txt', 'oom 0')
('redis-observation', 'after-cgroup-1.txt', 'oom_kill 0')
('redis-observation', 'after-cgroup-3.txt', 'nr_throttled 0')
('redis-observation', 'after-cgroup-3.txt', 'throttled_usec 0')
('redis-observation', 'after-cgroup-3.txt', 'oom 0')
('redis-observation', 'after-cgroup-3.txt', 'oom_kill 0')
```

Before confirms, every executed app-kill and broker-restart run had zero lost orders, stuck reservations, duplicates and oversold orders after drain. This is a null result: these runs did not reproduce accepted-order loss. Broker restart breached HTTP error thresholds in every run; failures remain in the results. SIGKILL produced some HTTP failures despite successful reconciliation.

The Redis row is observation only, not a Redis-loss/HA assertion. Normal restart retained inventory and reservation data. Executed Redis logs show graceful shutdown saved a final RDB, then reloaded it:

```text
1:M 09 Oct 2026 07:47:25.137 * Loading RDB produced by version 7.4.11
1:M 09 Oct 2026 07:47:25.266 * Done loading RDB, keys loaded: 52181, keys expired: 0.
1:M 09 Oct 2026 07:47:25.266 * DB loaded from disk: 0.130 seconds
1:signal-handler (1791534394) Received SIGTERM scheduling shutdown...
1:M 09 Oct 2026 08:26:34.441 * User requested shutdown...
1:M 09 Oct 2026 08:26:34.442 * Saving the final RDB snapshot before exiting.
1:M 09 Oct 2026 08:26:37.068 * Loading RDB produced by version 7.4.11
1:M 09 Oct 2026 08:26:37.125 * Done loading RDB, keys loaded: 21496, keys expired: 0.
1:M 09 Oct 2026 08:26:37.125 * DB loaded from disk: 0.060 seconds
```

Known limitation: Redis inventory and idempotency state are not coordinated with Postgres or broker transactions. No explicit named persistence volume/AOF/recovery policy is configured; normal restart with the same anonymous volume and graceful RDB save is not evidence of safety under hard loss, missing volume, or stale snapshots. Redis loss is not a pass/fail test in this campaign. Full logs: [redis-full.log](bench/results/faults-before/redis-full.log).

## Publisher-confirm implementation validation

`JAVA_TOOL_OPTIONS=-Xmx256m ./mvnw test` was executed after the change. Original assertions are retained; added cases simulate a broker nack, remove an actual broker binding to force a mandatory return, and suppress a confirmation to force timeout. Each checks HTTP 503, restored stock, released key and no persisted order. DLQ recovery uses Spring AMQP confirmed republish.

```text
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 32.90 s -- in com.flashsale.engine.OrderFlowIntegrationTests
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

Full executed output: [tests.log](bench/results/confirms/tests.log). No outbox decision is inferred from unit tests; it depends on the following fault reruns.
