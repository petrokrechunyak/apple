package com.alphabetas.bot.apple.repo;

import com.alphabetas.bot.apple.model.AppleGame;
import com.alphabetas.bot.apple.model.ApplePlayer;
import com.alphabetas.bot.apple.model.CallerChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppleGameRepo extends JpaRepository<AppleGame, Long> {

    Integer deleteAllByPlayer1AndChat(ApplePlayer p1, CallerChat chat);

    AppleGame findByChatAndMessageId(CallerChat chat, Long messageId);

    AppleGame findByPlayer1AndChat(ApplePlayer p1, CallerChat chat);

}
