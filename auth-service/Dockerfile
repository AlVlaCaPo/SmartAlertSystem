# Etapa 1: Build con Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
# Copiar el POM raíz y el POM del servicio
COPY pom.xml .
COPY auth-service/pom.xml auth-service/
# Descargar dependencias (cache)
RUN mvn dependency:go-offline -B -pl auth-service -am
# Copiar el código fuente y compilar
COPY auth-service/src auth-service/src
# Ejecutar pruebas y empaquetar
RUN mvn clean package -pl auth-service -am

# Etapa 2: Imagen de ejecución ligera
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/auth-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
