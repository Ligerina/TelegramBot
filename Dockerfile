FROM openjdk:21
ENV TELEGRAM_BOT_NAME=TestBotForPetProhectBot
ENV TELEGRAM_BOT_TOKEN=change-it
COPY build/libs/telegram.bot-0.0.1-SNAPSHOT.jar /telegram-bot.jar
CMD ["java", "-jar", "/telegram-bot.jar"]