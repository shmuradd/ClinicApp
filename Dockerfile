# Use a base image with Java 17
FROM openjdk:17-jdk-alpine

# Add a volume to store logs
VOLUME /tmp

# Copy the JAR file from the build directory to the Docker image
COPY build/libs/Clinics-0.0.1-SNAPSHOT.jar app.jar

# Expose the port that the Spring Boot app listens on
EXPOSE 8090

# Command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app.jar"]
