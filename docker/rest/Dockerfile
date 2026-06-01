# Build context must be the repo root (oms/), so the jar path below resolves.
# Build: docker build -f docker/rest/Dockerfile -t oms-rest:latest .
FROM eclipse-temurin:17-jre

WORKDIR /app

# Maven produces rest/target/rest-1.0.0.jar (artifactId-version, no custom finalName)
COPY rest/target/rest-1.0.0.jar app.jar

EXPOSE 9988

# DB / app config lives in app.yml on the remote server. Mount it at runtime and
# point Spring Boot at it, e.g.:
#   docker run -p 9988:9988 \
#     -v /path/on/server/app.yml:/config/app.yml \
#     -e SPRING_CONFIG_ADDITIONAL_LOCATION=/config/app.yml \
#     oms-rest:latest
ENTRYPOINT ["java", "-jar", "app.jar"]
