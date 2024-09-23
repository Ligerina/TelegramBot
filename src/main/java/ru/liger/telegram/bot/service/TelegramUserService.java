package ru.liger.telegram.bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.liger.telegram.bot.entity.TelegramUser;
import ru.liger.telegram.bot.repository.TelegramUserRepository;

@Service
@RequiredArgsConstructor
public class TelegramUserService {

    private final TelegramUserRepository telegramUserRepository;

    public void addNewUser(TelegramUser newUser){
        if (isNewUser(newUser)) {
            telegramUserRepository.save(newUser);
        }
    }

    public boolean isNewUser(TelegramUser user){
        return user.getChatId() != null && telegramUserRepository.findByChatId(user.getChatId()).isEmpty();
    }
}
