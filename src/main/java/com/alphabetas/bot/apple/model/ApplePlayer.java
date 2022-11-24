package com.alphabetas.bot.apple.model;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "apple_players")
@NoArgsConstructor
@Getter
@ToString
public class ApplePlayer implements Comparable<ApplePlayer>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private CallerUser player;

    @OneToOne
    private CallerChat chat;

    private Integer games;

    private Integer score;

    public ApplePlayer(CallerUser player, CallerChat chat) {
        this.player = player;
        this.chat = chat;
        games = 0;
        score = 0;
    }

    public ApplePlayer(CallerUser player, Integer games, Integer score) {
        this.player = player;
        this.games = games;
        this.score = score;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPlayer(CallerUser player) {
        this.player = player;
    }

    public void setChat(CallerChat chat) {
        this.chat = chat;
    }

    public void setGames(Integer games) {
        this.games = games;
    }

    public void setScore(Integer score) {
        if(score < 0)
            this.score = 0;
        else
            this.score = score;
    }

    @Override
    public int compareTo(ApplePlayer o) {
        return this.score - o.score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplePlayer that = (ApplePlayer) o;
        return player.equals(that.player) && chat.equals(that.chat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, chat);
    }
}
