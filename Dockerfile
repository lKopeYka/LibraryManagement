# === ЭТАП 1: Сборка фронтенда (React + Vite) ===
FROM node:20-alpine AS frontend-build
WORKDIR /frontend

# Явно копируем файлы конфигурации фронтенда
COPY frontend/package.json ./
COPY frontend/package-lock.json ./

# Устанавливаем зависимости фронтенда (install стабильнее внутри Docker)
RUN npm install

# Копируем весь исходный код фронта и собираем production-билд (генерирует папку /frontend/dist)
COPY frontend/ ./
RUN npm run build


# === ЭТАП 2: Сборка бэкенда (Spring Boot) ===
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Кэшируем зависимости Maven
COPY pom.xml .
RUN mvn dependency:go-offline

# Копируем исходный код Java-сервера
COPY src ./src

# !!! МАГИЯ ИНТЕГРАЦИИ !!!
# Забираем собранную статику React (из /frontend/dist на ЭТАПЕ 1)
# и переносим её в папку ресурсов Spring Boot перед компиляцией
COPY --from=frontend-build /frontend/dist ./src/main/resources/static

# Собираем готовый full-stack .jar пакет (пропуская тесты, чтобы деплой шел быстрее)
RUN mvn clean package -DskipTests


# === ЭТАП 3: Финальный запуск приложения ===
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Копируем собранный jar-файл из этапа сборки
COPY --from=build /app/target/LibraryManagement-0.0.1-SNAPSHOT.jar app.jar

# Открываем порт 8080 для Render
EXPOSE 8080

# Инструкция для запуска нашего монолита
ENTRYPOINT ["java", "-jar", "app.jar"]