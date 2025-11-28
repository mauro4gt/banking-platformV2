FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /workspace

# Copiamos los poms primero para aprovechar la cache de dependencias
COPY pom.xml ./pom.xml
COPY common/pom.xml ./common/pom.xml
COPY accounts-service/pom.xml ./accounts-service/pom.xml

# Copiamos el código fuente necesario para compilar este módulo y sus dependencias
COPY common/src ./common/src
COPY accounts-service/src ./accounts-service/src

RUN mvn -pl accounts-service -am -DskipTests package

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /workspace/accounts-service/target/accounts-service-*.jar /app/app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
