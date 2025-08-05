#FROM maven:3.8.3-openjdk-17 AS build
#WORKDIR /app
#COPY . .
#RUN mvn clean install -DskipTests
#
#FROM openjdk:17-jdk-slim
#COPY --from=build /app/target/*.jar app.jar
#EXPOSE 8080
#ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=$PORT" ]

# Build stage
FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Expose port (Render injects PORT env variable)
EXPOSE 8080

# Use exec form for ENTRYPOINT and support Render's PORT
ENTRYPOINT exec java -jar app.jar --server.port=$PORT
