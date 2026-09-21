# ==========================================
# STAGE 1: Build JAR using Maven
# ==========================================
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build production package
RUN mvn clean package -DskipTests

# ==========================================
# STAGE 2: Lightweight Production Runtime
# ==========================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Install curl for container health check
RUN apk add --no-cache curl

# Create non-root system user for secure container execution
RUN addgroup -S sprgroup && adduser -S spruser -G sprgroup

# Copy generated JAR from builder stage
COPY --from=builder /app/target/sprsystem-*.jar app.jar

# Set container ownership to non-root user
RUN chown -R spruser:sprgroup /app

USER spruser

# Expose Spring Boot default application port
EXPOSE 8080

# Configure container healthcheck via Spring Boot Actuator using dynamic PORT
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:${PORT:-8080}/actuator/health || exit 1

# Environment variable defaults
ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar app.jar"]
