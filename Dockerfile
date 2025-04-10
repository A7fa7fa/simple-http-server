#
# Build stage
#
FROM alpine:latest AS build

RUN  apk update \
  && apk upgrade \
  && apk add --update openjdk23-jdk --repository=http://dl-cdn.alpinelinux.org/alpine/edge/testing/ \
  && apk add --update maven


ENV JAVA_HOME="/usr/lib/jvm/java-23-openjdk"
ENV PATH="${JAVA_HOME}/bin:${PATH}"
ENV JAVA_VERSION=23

RUN which java
RUN java -version
RUN mvn -version

COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

# #
# # Package stage
# #
FROM alpine:latest

RUN  apk update \
  && apk upgrade \
  && apk add ca-certificates \
  && update-ca-certificates \
  && apk add --update openjdk23-jre --repository=http://dl-cdn.alpinelinux.org/alpine/edge/testing/ \
  # add mime type information /etc/mime.types
  && apk add mailcap \
  && rm -rf /var/cache/apk/*

RUN java -version

COPY --from=build /home/app/target/simple-http-server-1.1-SNAPSHOT.jar /usr/local/lib/demo.jar
RUN mkdir -p /home/app/public
COPY src/main/resources/http.json /home/app
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/demo.jar", "/home/app/http.json"]
