FROM ubuntu:latest
LABEL authors="Ghina Nabila"

ENTRYPOINT ["top", "-b"]

# Use official OpenJDK 21 slim base image
FROM openjdk:21-jdk-slim

# Set working directory inside container
WORKDIR /app

# Copy the Gradle-built JAR
COPY build/libs/authentication-0.0.1-SNAPSHOT.jar app.jar

# Optional: define JVM options
ENV JAVA_OPTS=""

# Expose port 8080
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar