FROM openjdk:21

# Копируем билд в image
COPY build/libs/telegram.bot-0.0.1.jar /telegram-bot.jar

# CMD для запуска приложения
CMD ["java", "-jar", "/telegram-bot.jar"]
