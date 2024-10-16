package ru.liger.telegram.bot.facade;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.liger.telegram.bot.config.BotConfig;
import ru.liger.telegram.bot.entity.TelegramUser;
import ru.liger.telegram.bot.service.TelegramUserService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class TelegramBotFacade extends TelegramLongPollingBot {

    private final BotConfig botConfig;

    private final TelegramUserService telegramUserService;

    private final Map<Long, String> userState = new HashMap<>();

    @Override
    public void onUpdateReceived(Update update) {
        log.info("update received - {}", update);

        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        var chatId = update.getMessage().getChatId();
        var userText = update.getMessage().getText();
        var username = update.getMessage().getChat().getUserName();

        log.info("Bot received message. ChatId: {}\\n msgtext: {}\n username {}\n", chatId, userText, username);

        switch (userText) {
            case "/start":
                telegramUserService.addNewUser(TelegramUser.builder()
                        .chatId(chatId)
                        .username(username)
                        .build());
                startCommandReceived(username, chatId);
                break;
            case "/startNewDialog":
                createNewDialogWithUser(username, chatId);
                break;
            default:
                log.warn("Unknown command message {}. User = {}, chatId = {}", userText, username, chatId);
                sendMessage(chatId, "The command was not recognised");
        }

    }

    private void createNewDialogWithUser(String username, Long chatId) {

    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    private void startCommandReceived(String userName, Long chatId) {
        var responseToUser = "Привет, " + userName + "! \n"+
                "Я бот Антона (и не только). На данном этапе развития я не умнее людей, " +
                "которые пьют кофе в серфе, но, надеюсь, я стану умнее!";
        sendMessage(chatId, responseToUser);
    }

    private void sendMessage(Long chatId, String userMessage) {
        var message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(userMessage);
        sendMessage(message);
    }

    private void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }
}
