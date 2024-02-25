FROM openjdk:17-jdk-slim
COPY /target/hw17-docker-1.0.0.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
