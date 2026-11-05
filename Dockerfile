# Образ сервера каталога для хостинга (Render, Koyeb — любой, кто собирает из Dockerfile).
# Два этапа: в первом собираем сервер полным JDK, во втором запускаем его
# на облегчённом JRE — так готовый образ меньше и в нём нет Gradle и исходников.

# ---------- Этап 1: сборка ----------
FROM eclipse-temurin:17-jdk AS build
WORKDIR /src
COPY . .
# chmod: на случай, если gradlew пришёл из Windows без права на запуск.
# installDist собирает server/build/install/server: bin/server (скрипт запуска) и lib/*.jar.
RUN chmod +x gradlew && ./gradlew :server:installDist --no-daemon

# ---------- Этап 2: запуск ----------
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /src/server/build/install/server ./server
COPY content ./content

# Где лежат наборы. PORT задаёт сам хостинг; application.conf читает обе переменные.
ENV CONTENT_DIR=/app/content
# На бесплатном тарифе мало памяти: просим JVM занимать не больше 75% от выделенного.
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"

# Порт по умолчанию для запуска на своём компьютере; на хостинге его заменит PORT.
EXPOSE 8080
CMD ["./server/bin/server"]
