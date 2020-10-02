FROM openjdk:8-jdk-alpine
MAINTAINER Atos
VOLUME /tmp
ADD ./target/derivation-id-0.0.1.DEVELOPMENT.jar derivation-id-0.0.1.DEVELOPMENT.jar
RUN sh -c 'touch /derivation-id-0.0.1.DEVELOPMENT.jar'
USER root
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /derivation-id-0.0.1.DEVELOPMENT.jar" ]
EXPOSE 8020
