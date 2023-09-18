#
# Build stage
#
FROM maven:3.8.5-openjdk-17 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

#
# Package stage
#
FROM openjdk:17-jdk-slim
COPY --from=build /home/app/target/simple-http-server-1.0-SNAPSHOT.jar /usr/local/lib/demo.jar
RUN mkdir -p /home/app/public
COPY src/main/resources/http.json /home/app
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/demo.jar", "/home/app/http.json"]
