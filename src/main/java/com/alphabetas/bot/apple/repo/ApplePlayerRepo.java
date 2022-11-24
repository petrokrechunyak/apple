package com.alphabetas.bot.apple.repo;

import com.alphabetas.bot.apple.model.ApplePlayer;
import com.alphabetas.bot.apple.model.CallerChat;
import com.alphabetas.bot.apple.model.CallerUser;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplePlayerRepo extends JpaRepository<ApplePlayer, Long> {


    List<ApplePlayer> findTop10ByChatOrderByScoreDesc(CallerChat chat);

    List<ApplePlayer> findTop10ByChatOrderByGamesDesc(CallerChat chat);

    ApplePlayer findByPlayer(CallerUser player);

    Optional<ApplePlayer> findByPlayerAndChat(CallerUser user, CallerChat chat);
}
