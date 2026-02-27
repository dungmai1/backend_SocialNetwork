# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml và download dependencies trước để tận dụng Docker cache
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code và build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Tạo user không có quyền root để chạy ứng dụng
RUN addgroup -S spring && adduser -S spring -G spring

# Copy JAR file từ stage build
COPY --from=build /app/target/*.jar app.jar

# Tạo thư mục logs
RUN mkdir -p /app/logs && chown -R spring:spring /app

# Chuyển sang user spring
USER spring:spring

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
