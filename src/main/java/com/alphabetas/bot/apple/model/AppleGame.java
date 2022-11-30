package com.alphabetas.bot.apple.model;

import com.alphabetas.bot.apple.model.enums.GameDesign;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "apple_games")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class AppleGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private CallerChat chat;

    @OneToOne
    private ApplePlayer player1;

    @OneToOne
    private ApplePlayer player2;

    @OneToOne
    private ApplePlayer currentPlayer;

    private Integer apples;

    @Enumerated(EnumType.STRING)
    private GameDesign gameDesign;

    private Long messageId;


    public AppleGame(CallerChat chat, ApplePlayer player1, ApplePlayer player2, ApplePlayer currentPlayer, Integer apples, GameDesign gameDesign) {
        this.chat = chat;
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = currentPlayer;
        this.apples = apples;
        this.gameDesign = gameDesign;
    }
}
