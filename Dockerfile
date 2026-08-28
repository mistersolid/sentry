FROM eclipse-temurin:21-jdk AS build
WORKDIR /sentry
COPY . .
RUN ./kotlin task :sentry:executableJarJvm

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /sentry/build/tasks/_sentry_executableJarJvm/sentry-jvm-executable.jar /app/bot.jar
CMD ["java", "-jar", "/app/bot.jar"]