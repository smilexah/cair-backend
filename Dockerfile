# Stage 1: Build
FROM eclipse-temurin:25-jdk AS build

WORKDIR /app

# Копируем Gradle wrapper и конфиги
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Делаем gradlew исполняемым (и на всякий случай чиним переносы строк из Windows)
RUN chmod +x ./gradlew

# Если сильно переживаешь из-за Windows-строк — можно так:
# RUN apt-get update \
#   && apt-get install -y --no-install-recommends dos2unix \
#   && dos2unix ./gradlew \
#   && rm -rf /var/lib/apt/lists/*

ENV GRADLE_OPTS="-Xmx2g -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8"

# Копируем исходники
COPY src src

# Собираем jar
RUN ./gradlew bootJar --no-daemon

# Stage 2: Run
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# Копируем готовый jar из build-стейджа
COPY --from=build /app/build/libs/*.jar app.jar

# Опционально: не такие конские лимиты памяти, чтобы контейнер не умирал
ENTRYPOINT ["java", "-Dvertx.disableDnsResolver=true", "-Djava.net.preferIPv4Stack=true", "-Xms512m","-Xmx1g","-XX:+UseG1GC", "-jar", "app.jar"]