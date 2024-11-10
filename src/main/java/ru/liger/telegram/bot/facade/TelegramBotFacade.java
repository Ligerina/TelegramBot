package ru.liger.telegram.bot.facade;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.liger.telegram.bot.config.BotConfig;
import ru.liger.telegram.bot.service.ChatService;

@Slf4j
@Service
@AllArgsConstructor
public class TelegramBotFacade extends TelegramLongPollingBot {

    private final BotConfig botConfig;

    private final ChatService chatService;

    @Override
    public void onUpdateReceived(Update update) {
        log.info("update received - {}", update);
        var chatId = update.getMessage().getChatId();
        var userText = update.getMessage().getText();
        var username = update.getMessage().getChat().getUserName();
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        log.info("Bot received message. ChatId: {}\\n msgtext: {}\n username {}\n", chatId, userText, username);
        var responseMessage = chatService.handleUserRequest(update);
        sendMessage(responseMessage);
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    private void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
