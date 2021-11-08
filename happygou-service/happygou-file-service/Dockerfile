FROM java:8
ADD /happygou-file-service-1.0-SNAPSHOT.jar /happygou-file-service/
EXPOSE 8888
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/happygou-file-service/happygou-file-service-1.0-SNAPSHOT.jar"]
