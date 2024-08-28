FROM openjdk:21-jdk

WORKDIR /backend

COPY ./travel-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/backend/app.jar"]