# Dockerfile del servizio (LAB 3): stesso progetto Maven della versione
# console (vedi Dockerfile.console), ma l'immagine finale avvia HealthServer
# invece di Main, quindi resta in ascolto invece di stampare e uscire.
# È questo il Dockerfile che Render costruisce, perché legge "Dockerfile"
# alla radice del repository.

# Stage 1: build e test dell'applicazione
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN mvn --batch-mode clean package

# Stage 2: immagine runtime minimale
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /build/target/devops-maven-lab-1.0.0-SNAPSHOT.jar app.jar

USER 10001

ENTRYPOINT ["java", "-cp", "app.jar", "it.its.devops.web.HealthServer"]
