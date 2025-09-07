# ===============================
# 1. Build stage
# ===============================
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# Set working directory inside container
WORKDIR /app

# Copy pom.xml and download dependencies (cache layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Package application (skip tests để build nhanh hơn)
RUN mvn clean package -DskipTests

# ===============================
# 2. Run stage
# ===============================
FROM eclipse-temurin:17-jdk

# Set working directory
WORKDIR /app

# Copy JAR từ build stage
COPY --from=builder /app/target/*.jar app.jar

# App sẽ chạy ở port 8080 (Render yêu cầu)
EXPOSE 8080

# Run Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
