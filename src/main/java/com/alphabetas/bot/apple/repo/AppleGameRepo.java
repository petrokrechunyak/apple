package com.alphabetas.bot.apple.repo;

import com.alphabetas.bot.apple.model.AppleGame;
import com.alphabetas.bot.apple.model.ApplePlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AppleGameRepo extends JpaRepository<AppleGame, Long> {

    @Transactional
    Integer deleteAllByPlayer1AndChat(ApplePlayer p1, Long chat);

    AppleGame findByChatAndMessageId(Long chat, Long messageId);

    AppleGame findByPlayer1AndChat(ApplePlayer p1, Long chat);

}
