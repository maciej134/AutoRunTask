FROM openjdk:11-jdk-slim
COPY target/RecrutingTask.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]