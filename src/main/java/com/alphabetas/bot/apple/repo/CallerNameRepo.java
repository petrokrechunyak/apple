package com.alphabetas.bot.apple.repo;

import com.alphabetas.bot.apple.model.CallerName;
import com.alphabetas.bot.apple.model.CallerUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

public interface CallerNameRepo extends JpaRepository<CallerName, Long> {
    CallerName getByChatIdAndNameIgnoreCase(Long chatId, String name);

    Set<CallerName> getAllByChatId(Long chatId);

    boolean deleteAllByCallerUser(CallerUser callerUser);

    @Transactional
    void removeAllByCallerUserAndChatId(CallerUser callerUser, Long chatId);
}
