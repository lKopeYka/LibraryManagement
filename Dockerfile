# === ЭТАП 1: Сборка фронтенда (React + Vite) ===
FROM node:20-alpine AS frontend-build
WORKDIR /frontend
# Копируем конфиги и ставим зависимости
COPY frontend/package*.json ./
RUN npm ci
# Копируем весь исходный код фронта и собираем production-билд
COPY frontend/ ./
RUN npm run build

# === ЭТАП 2: Сборка бэкенда (Spring Boot) ===
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
# Копируем исходники Java
COPY src ./src
# !!! МАГИЯ: Забираем собранный React-статику из ЭТАПА 1 и кладем в ресурсы Spring Boot
COPY --from=frontend-build /frontend/dist ./src/main/resources/static
# Собираем всё вместе в один .jar
RUN mvn clean package -DskipTests

# === ЭТАП 3: Финальный запуск приложения ===
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/LibraryManagement-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]