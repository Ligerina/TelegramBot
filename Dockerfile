FROM openjdk:21
COPY build/libs/telegram.bot-0.0.1.jar /telegram-bot.jar
EXPOSE 8080
CMD ["java", "-jar", "/telegram-bot.jar"]