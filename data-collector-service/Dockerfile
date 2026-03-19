# Etapa 1: Build con Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
# Copiar los archivos necesarios para resolver la estructura del monorepo
COPY pom.xml .
COPY auth-service/pom.xml auth-service/
COPY data-collector-service/pom.xml data-collector-service/
COPY alert-service/pom.xml alert-service/
# Descargar dependencias (cache)
RUN mvn dependency:go-offline -B -pl data-collector-service -am
# Copiar el código fuente y compilar
COPY data-collector-service/src data-collector-service/src
RUN mvn clean package -pl data-collector-service -am

# Etapa 2: Imagen de ejecución ligera
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/data-collector-service/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
