# Use official OpenJDK image
FROM eclipse-temurin:17-jdk

# Set working directory inside container
WORKDIR /app

# Copy built jar from target folder
COPY target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java","-jar","app.jar"]