FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/flash-sale-engine-0.0.1-SNAPSHOT.jar app.jar
ENV JAVA_TOOL_OPTIONS="-Xmx256m"
USER 10001:10001
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
