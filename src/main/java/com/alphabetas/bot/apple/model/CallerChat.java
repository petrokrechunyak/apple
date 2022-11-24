package com.alphabetas.bot.apple.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "caller_chats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CallerChat {

    @Id
    private Long Id;

    @OneToMany(mappedBy = "callerChat")
    private Set<CallerUser> callerUsers;

    public CallerChat(Long id) {
        Id = id;
        callerUsers = new HashSet<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CallerChat that = (CallerChat) o;
        return Id.equals(that.Id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Id);
    }
}
