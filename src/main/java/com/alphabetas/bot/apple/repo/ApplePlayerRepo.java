package com.alphabetas.bot.apple.repo;

import com.alphabetas.bot.apple.model.ApplePlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplePlayerRepo extends JpaRepository<ApplePlayer, Long> {


    List<ApplePlayer> findTop10ByChatOrderByScoreDesc(Long chat);

    List<ApplePlayer> findTop10ByChatOrderByGamesDesc(Long chat);

    List<ApplePlayer> findTop10ByChatOrderByEatenDesc(Long chat);

    ApplePlayer findByPlayer(Long player);

    Optional<ApplePlayer> findByPlayerAndChat(Long user, Long chat);
}
