FROM openjdk:8-jdk-alpine
COPY /build/libs/hcxPayor-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 9095