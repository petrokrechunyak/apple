package com.alphabetas.bot.apple.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "names", uniqueConstraints=
@UniqueConstraint(columnNames={"chat_id", "name"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CallerName {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "caller_user_id")
    private CallerUser callerUser;

    @Column(name = "chat_id")
    private Long chatId;

    private String name;

    public CallerName(CallerUser callerUser, Long chatId, String name) {
        this.callerUser = callerUser;
        this.chatId = chatId;
        this.name = name;
    }
}
