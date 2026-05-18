# Paso 1: Construir la aplicación usando Maven y Java 17
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
ENV MAVEN_OPTS="-Xmx300m"
RUN mvn clean package -DskipTests

# Paso 2: Ejecutar la aplicación en un entorno moderno y ligero de Java 17
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
