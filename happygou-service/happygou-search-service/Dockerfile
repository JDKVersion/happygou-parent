FROM java:8
ADD /happygou-search-service-1.0-SNAPSHOT.jar /happygou-search-service/
EXPOSE 18082
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/happygou-search-service/happygou-search-service-1.0-SNAPSHOT.jar"]
