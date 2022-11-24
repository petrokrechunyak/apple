package com.alphabetas.bot.apple.repo;

import com.alphabetas.bot.apple.model.CallerChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CallerChatRepo extends JpaRepository<CallerChat, Long> {
}
