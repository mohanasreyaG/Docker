FROM eclipse-temurin:11-jdk-jammy

# Install curl and netcat for healthcheck and wait-for-it script
RUN apt-get update && apt-get install -y \
    curl \
    netcat \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy wait-for-it script
COPY wait-for-it.sh /app/wait-for-it.sh
RUN chmod +x /app/wait-for-it.sh

# Copy the jar file
COPY target/Demo-1.0-SNAPSHOT.jar app.jar

# Create startup script
RUN echo '#!/bin/bash\n\
./wait-for-it.sh cassandra:9042 -t 10\n\
./wait-for-it.sh kafka:29092 -t 20\n\
./wait-for-it.sh ignite:47500 -t 10\n\
exec java ${JAVA_OPTS} -jar app.jar' > /app/startup.sh \
    && chmod +x /app/startup.sh

# Expose necessary ports
EXPOSE 8080

# Set environment variables for JVM options
ENV JAVA_OPTS="-Xms512m -Xmx2g -XX:+UseG1GC -Djava.net.preferIPv4Stack=true -XX:MaxDirectMemorySize=512m"


# Use the custom startup script
ENTRYPOINT ["/app/startup.sh"]
