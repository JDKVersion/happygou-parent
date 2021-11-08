FROM java:8
ADD /happygou-canal-service-1.0-SNAPSHOT.jar /happygou-canal-service/
EXPOSE 18084
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/happygou-canal-service/happygou-canal-service-1.0-SNAPSHOT.jar"]
