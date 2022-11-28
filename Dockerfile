FROM --platform=linux/amd64 maven:3.8.6-amazoncorretto-17

WORKDIR /app
COPY target/urlshortener-1.0.jar ./target/urlshortener.jar
EXPOSE 8080 8081
CMD ["java", "-jar", "-Xms512M", "-Xmx1G", "target/urlshortener.jar", "server", "config.yml"]
