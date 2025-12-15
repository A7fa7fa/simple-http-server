#
# Build stage
#
FROM alpine:latest AS build

RUN  apk update \
  && apk upgrade \
  && apk add --update openjdk21-jdk \
  && apk add --update maven


ENV JAVA_HOME="/usr/lib/jvm/java-21-openjdk"
ENV PATH="${JAVA_HOME}/bin:${PATH}"
ENV JAVA_VERSION=21

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
  && apk add --update openjdk21-jre \
  # add mime type information /etc/mime.types
  && apk add mailcap \
  && rm -rf /var/cache/apk/*

RUN java -version

COPY --from=build /home/app/target/simple-http-server-1.1-SNAPSHOT-shaded.jar /usr/local/lib/http.jar
RUN mkdir -p /home/app/public
COPY src/main/resources/http.json /home/app
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/http.jar", "/home/app/http.json"]
