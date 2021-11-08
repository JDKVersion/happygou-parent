FROM java:8
ADD /happygou-eureka-1.0-SNAPSHOT.jar /happygou-eureka/
EXPOSE 8761
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/happygou-eureka/happygou-eureka-1.0-SNAPSHOT.jar"]
