# 1️⃣ Chọn base image JDK 17 nhẹ
FROM eclipse-temurin:17-jdk-alpine

# 2️⃣ Tạo thư mục tạm
VOLUME /tmp

# 3️⃣ Copy file jar từ target
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# 4️⃣ Expose port (phải trùng với server.port trong application.yml)
EXPOSE 10000

# 5️⃣ Chạy ứng dụng
ENTRYPOINT ["java","-jar","/app.jar"]
