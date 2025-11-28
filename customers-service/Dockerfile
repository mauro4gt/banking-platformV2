FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /workspace

# Copiamos los poms primero para cachear dependencias
COPY pom.xml ./pom.xml
COPY common/pom.xml ./common/pom.xml
COPY accounts-service/pom.xml ./accounts-service/pom.xml
COPY customers-service/pom.xml ./customers-service/pom.xml

# Copiamos código fuente requerido para compilar este módulo y dependencias
COPY common/src ./common/src
COPY customers-service/src ./customers-service/src

RUN mvn -pl customers-service -am -DskipTests package

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /workspace/customers-service/target/customers-service-*.jar /app/app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
