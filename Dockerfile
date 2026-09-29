FROM eclipse-temurin:21-jdk AS build
WORKDIR /sentry
COPY . .
RUN ./kotlin task :bot:executableJarJvm

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /sentry/build/tasks/_bot_executableJarJvm/bot-jvm-executable.jar /app/bot.jar
CMD ["java", "-jar", "/app/bot.jar"]