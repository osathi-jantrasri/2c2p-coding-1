# Build stage
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn -q dependency:resolve

# Copy source code
COPY src/ src/

# Copy data file
COPY data.json .

# Build the application
RUN mvn -q clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the built fat JAR from builder stage
COPY --from=builder /app/target/demo.jar app.jar

# Copy data file
COPY --from=builder /app/data.json .

# Set the entry point
ENTRYPOINT ["java", "-jar", "app.jar"]
