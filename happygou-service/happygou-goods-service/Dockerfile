FROM java:8
ADD /happygou-goods-service-1.0-SNAPSHOT.jar /happygou-goods-service/
EXPOSE 18081
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/happygou-goods-service/happygou-goods-service-1.0-SNAPSHOT.jar"]
