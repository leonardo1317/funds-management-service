# ---------- STAGE 1: Build ----------
FROM eclipse-temurin:17-jdk-jammy AS build

WORKDIR /app

COPY gradlew .
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

RUN chmod +x gradlew

# Cache de dependencias
RUN ./gradlew dependencies --no-daemon

# Código fuente
COPY src ./src

# Build
RUN ./gradlew clean bootJar -x test --no-daemon

# ---------- STAGE 2: Runtime ----------
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]