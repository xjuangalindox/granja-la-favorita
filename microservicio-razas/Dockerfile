# Etapa 1: Compilación
FROM maven:3.9.6-eclipse-temurin-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Imagen final
FROM openjdk:17-jdk-slim

# Instalar curl para que el healthcheck funcione
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

COPY --from=build target/microservicio-razas-0.0.1-SNAPSHOT.jar microservicio_razas.jar
ENTRYPOINT ["java","-jar","microservicio_razas.jar"]