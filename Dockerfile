FROM node:20-alpine AS frontend-build
WORKDIR /app

COPY frontend/package.json ./
COPY frontend/package-lock.json ./
RUN npm install

COPY frontend/ .
RUN npm run build

FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src

COPY --from=frontend-build /app/dist ./src/main/resources/static

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/LibraryManagement-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]