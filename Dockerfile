# Stage 1: Создаем stage  для билда
FROM openjdk:21 AS build

# Устанавливаем аргументы - они передадутся как аргументы команды docker build
ARG TELEGRAM_BOT_NAME
ARG TELEGRAM_BOT_TOKEN

# Устанавливаем переменные окружения
ENV TELEGRAM_BOT_NAME=$TELEGRAM_BOT_NAME
ENV TELEGRAM_BOT_TOKEN=$TELEGRAM_BOT_TOKEN

# Копируем билд в image
COPY build/libs/telegram.bot-0.0.1.jar /app/telegram-bot.jar

# Stage 2: Создаем stage для финального образа
FROM openjdk:21

# Копируем только готовый артефакт из первого этапа
COPY --from=build /app/telegram-bot.jar /telegram-bot.jar

# CMD для запуска приложения
CMD ["java", "-jar", "/telegram-bot.jar"]

