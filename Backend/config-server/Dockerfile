FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY mvnw* .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline -B || true
COPY src src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]
