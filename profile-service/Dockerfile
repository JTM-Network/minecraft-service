FROM openjdk:16.0-buster
VOLUME /tmp
EXPOSE 8222
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
CMD java -Xms256m -Xmx512m -jar app.jar