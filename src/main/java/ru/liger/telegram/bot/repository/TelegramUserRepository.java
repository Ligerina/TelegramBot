package ru.liger.telegram.bot.repository;

import org.springframework.data.repository.CrudRepository;
import ru.liger.telegram.bot.entity.TelegramUser;

import java.util.Optional;
import java.util.UUID;

public interface TelegramUserRepository extends CrudRepository<TelegramUser, UUID> {

    Optional<TelegramUser> findByChatId(Long chatId);

}
