package ru.liger.telegram.bot.entity;

import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "telegram_users")
@Data
@NoArgsConstructor
public class TelegramUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "chat_id", nullable = false, updatable = false)
    Long chatId;

    @Column(name = "username", updatable = false)
    String username;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    LocalDateTime createdAt;

    public TelegramUser(Long chatId, String username){
        this.chatId = chatId;
        this.username = username;
    }
}
