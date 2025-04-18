FROM openjdk:23

COPY build/libs/PashmSocialCreditBot-2.0.8.jar /app.jar

CMD ["/usr/bin/java", "-jar", "/app.jar", "/data/secret.properties", "/data/ratings.db"]