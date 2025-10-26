# syntax=docker/dockerfile:1

# 1) Build stage: compile and package with JDK 21
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /workspace/app

# Cache dependencies
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -B -q -DskipTests dependency:go-offline

# Build app JAR (fixed name for predictable copy)
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B -DskipTests -DfinalName=app clean package

# 2) Runtime stage: minimal, non-root, no shell
FROM gcr.io/distroless/java21-debian12:nonroot
WORKDIR /app

# Copy app as non-root
COPY --from=builder --chown=nonroot:nonroot /workspace/app/target/chat-agent-0.0.1-SNAPSHOT.jar /app/app.jar

# Configure port and runtime env
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=default
ENV JAVA_TOOL_OPTIONS=""

# Start the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
