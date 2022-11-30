package com.alphabetas.bot.apple.repo;

import com.alphabetas.bot.apple.model.AppleGame;
import com.alphabetas.bot.apple.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepo extends JpaRepository<Notification, Long> {
    void deleteAllByGame(AppleGame game);
}
