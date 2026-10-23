# --- build stage: compile and package the jar ---
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /src
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q package -DskipTests

# --- runtime stage: JRE only, non-root user ---
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd --system --uid 10001 app
COPY --from=build /src/target/hello-service.jar app.jar
USER 10001
EXPOSE 8080
# size the heap from the container memory limit, not from the node
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/app.jar"]
