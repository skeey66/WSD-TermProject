# syntax=docker/dockerfile:1
# Spring Boot 4 requires Gradle 8.14+ (see Spring Boot 4 docs).
FROM eclipse-temurin:21-jdk AS build

ARG GRADLE_VERSION=8.14.2

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl unzip ca-certificates \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /workspace

# Install Gradle
RUN curl -fsSL "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" -o gradle.zip \
    && unzip -q gradle.zip -d /opt \
    && rm gradle.zip
ENV PATH="/opt/gradle-${GRADLE_VERSION}/bin:${PATH}"

# Copy sources
COPY . .

# Build (tests included by default; if you want faster builds on server: add --no-daemon -x test)
RUN gradle --no-daemon clean bootJar

FROM eclipse-temurin:21-jre

# Runtime healthcheck uses wget; ensure it exists.
RUN apt-get update \
    && apt-get install -y --no-install-recommends wget ca-certificates \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar /app/app.jar

EXPOSE 8080
ENV JAVA_OPTS=""

HEALTHCHECK --interval=10s --timeout=3s --retries=12 CMD \
  sh -c 'wget -qO- http://127.0.0.1:8080/health | grep -q "\"status\":\"UP\"" || exit 1'

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
