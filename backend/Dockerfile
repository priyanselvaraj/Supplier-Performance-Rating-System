# ==========================================
# STAGE 1: Build JAR using Maven
# ==========================================
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy backend source from build context
COPY . .

# Build production package without tests
RUN mvn clean package -DskipTests

# ==========================================
# STAGE 2: Lightweight Production Runtime
# ==========================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create non-root system user for security
RUN addgroup -S sprgroup && adduser -S spruser -G sprgroup

# Copy generated JAR from builder stage
COPY --from=builder /app/target/sprsystem-*.jar app.jar

# Set container ownership
RUN chown -R spruser:sprgroup /app

USER spruser

# Expose port (8080 default, overridden by $PORT on Render)
EXPOSE 8080

# Environment variable defaults & fast startup JVM flags
ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-Xms128m -Xmx384m -XX:+UseSerialGC -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.address=0.0.0.0 -Dserver.port=${PORT:-8080} -jar app.jar"]
