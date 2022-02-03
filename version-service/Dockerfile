FROM openjdk:16.0-buster
VOLUME /tmp
EXPOSE 9675
ADD /build/libs/*.jar app.jar
CMD java -Xms256m -Xmx512m -jar app.jar