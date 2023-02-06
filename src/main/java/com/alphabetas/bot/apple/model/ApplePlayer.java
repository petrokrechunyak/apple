package com.alphabetas.bot.apple.model;

import java.util.Objects;
import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "apple_players")
@NoArgsConstructor
@Getter
@ToString
public class ApplePlayer implements Comparable<ApplePlayer>{
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long player;

    private String firstname;

    private Long chat;

    private Integer games;

    private Integer score;
    
    private Long eaten;

    public ApplePlayer(Long player, Long chat) {
        this.player = player;
        this.chat = chat;
        games = 0;
        score = 0;
        eaten = 0L;
    }

    public ApplePlayer(Long player, Integer games, Integer score) {
        this.player = player;
        this.games = games;
        this.score = score;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPlayer(Long player) {
        this.player = player;
    }

    public void setChat(Long chat) {
        this.chat = chat;
    }

    public void setGames(Integer games) {
        this.games = games;
    }

    public Long getEaten() {
        return eaten;
    }

    public void setEaten(Long eaten) {
        this.eaten = eaten;
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
