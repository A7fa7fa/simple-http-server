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
FROM alpine:latest

RUN  apk update \
  && apk upgrade \
  && apk add ca-certificates \
  && update-ca-certificates \
  && apk add --update coreutils && rm -rf /var/cache/apk/*   \
  && apk add --update openjdk17 tzdata curl unzip bash \
  && apk add --no-cache nss \
  # add mime type information /etc/mime.types
  && apk add mailcap \
  && rm -rf /var/cache/apk/*

RUN java -version

COPY --from=build /home/app/target/simple-http-server-1.0-SNAPSHOT.jar /usr/local/lib/demo.jar
RUN mkdir -p /home/app/public
COPY src/main/resources/http.json /home/app
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/demo.jar", "/home/app/http.json"]
