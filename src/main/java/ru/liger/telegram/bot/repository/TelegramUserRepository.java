package ru.liger.telegram.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.liger.telegram.bot.entity.TelegramUser;

import java.util.Optional;
import java.util.UUID;

public interface TelegramUserRepository extends JpaRepository<TelegramUser, UUID> {

    Optional<TelegramUser> findByChatId(Long chatId);

}
