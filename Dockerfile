# Stage 1: Build (No changes needed)
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime (Use Jammy/Debian to match Python Slim)
FROM eclipse-temurin:25-jre-jammy
WORKDIR /app

# 1. Use Debian/Ubuntu commands (Matches Python Slim)
RUN groupadd -g 1000 appuser && \
    useradd -u 1000 -g appuser -m appuser

# 2. Copy the jar (Ensure path matches Stage 1 WORKDIR)
COPY --from=build --chown=appuser:appuser /app/target/*.jar app.jar

# 3. Switch to the non-root user
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]