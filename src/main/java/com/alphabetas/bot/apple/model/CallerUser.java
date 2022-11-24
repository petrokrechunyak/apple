package com.alphabetas.bot.apple.model;

import com.alphabetas.bot.apple.model.enums.ConfigEmojiEnum;
import com.alphabetas.bot.apple.model.enums.UserStates;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "caller_users",
        uniqueConstraints=@UniqueConstraint(columnNames={"user_id", "caller_chat_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CallerUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    private String firstname;

    @ManyToOne
    @JoinColumn(name = "caller_chat_id")
    private CallerChat callerChat;

    @OneToMany(mappedBy = "callerUser")
    private Set<CallerName> tableCallerNames;

    @Transient
    private transient HashSet<String> names;

    @Enumerated(EnumType.STRING)
    private UserStates userState;

    @Enumerated(EnumType.STRING)
    private ConfigEmojiEnum emojiEnum = ConfigEmojiEnum.ALL;

    public CallerUser(Long userId, String firstname) {
        this.userId = userId;
        this.firstname = firstname;
    }

    public CallerUser(Long id, Long userId, String firstname, CallerChat callerChat) {
        this.id = id;
        this.userId = userId;
        this.firstname = firstname;
        this.callerChat = callerChat;
        userState = UserStates.OFF;
    }

    public CallerUser(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CallerUser that = (CallerUser) o;
        return userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
