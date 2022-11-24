package com.alphabetas.bot.apple.repo;

import com.alphabetas.bot.apple.model.CallerChat;
import com.alphabetas.bot.apple.model.CallerUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CallerUserRepo extends JpaRepository<CallerUser, Long> {
    CallerUser getByUserIdAndCallerChat(Long userId, CallerChat callerChat);

    @Transactional
    void removeCallerUserByCallerChat(CallerChat chat);
}
