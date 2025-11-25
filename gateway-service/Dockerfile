# Etapa 1: Compilación
FROM maven:3.9.6-eclipse-temurin-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Imagen final
# FROM openjdk:17-jdk-slim
FROM eclipse-temurin:17-jdk-alpine

# Instalar curl para que el healthcheck funcione
# RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
RUN apk add --no-cache curl

COPY --from=build target/gateway-service-0.0.1-SNAPSHOT.jar gateway.jar
ENTRYPOINT ["java","-jar","gateway.jar"]