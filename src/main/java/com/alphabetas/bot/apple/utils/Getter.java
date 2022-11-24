package com.alphabetas.bot.apple.utils;


import com.alphabetas.bot.apple.model.CallerChat;
import com.alphabetas.bot.apple.model.CallerName;
import com.alphabetas.bot.apple.model.CallerUser;
import com.alphabetas.bot.apple.model.enums.UserStates;
import com.alphabetas.bot.apple.repo.CallerUserRepo;
import com.alphabetas.bot.apple.model.ApplePlayer;
import com.alphabetas.bot.apple.repo.ApplePlayerRepo;
import com.alphabetas.bot.apple.repo.CallerChatRepo;
import java.util.HashSet;
import java.util.NoSuchElementException;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

public final class Getter {

    private static CallerUserRepo callerUserRepo;
    private static CallerChatRepo callerChatRepo;
    private static ApplePlayerRepo applePlayerRepo;

    public static CallerUser getUser(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        CallerUser user;
        CallerChat chat = getChat(update);
        if((user = callerUserRepo.getByUserIdAndCallerChat(userId, chat)) == null) {
            user = new CallerUser(userId, update.getMessage().getFrom().getFirstName());
            user.setCallerChat(chat);
            user.setUserState(UserStates.OFF);
            user = callerUserRepo.save(user);
            user.setTableCallerNames(new HashSet<>());
        }
        user.setNames(new HashSet<>());
        for(CallerName callerName : user.getTableCallerNames()) {
            user.getNames().add(callerName.getName());
        }
        return user;
    }

    public static CallerUser getUser(User u, Long chatId) {
        CallerUser user;
        CallerChat chat = getChat(chatId);
        if((user = callerUserRepo.getByUserIdAndCallerChat(u.getId(), chat)) == null) {
            user = new CallerUser(u.getId(), u.getFirstName());
            user.setCallerChat(chat);
            user.setUserState(UserStates.OFF);
            user = callerUserRepo.save(user);
            user.setTableCallerNames(new HashSet<>());
        }
        user.setNames(new HashSet<>());
        for(CallerName callerName : user.getTableCallerNames()) {
            user.getNames().add(callerName.getName());
        }
        return user;
    }

    @Transactional
    public static ApplePlayer getPlayer(CallerUser u, CallerChat chat) {
        ApplePlayer player;
        try {
            player = applePlayerRepo.findByPlayerAndChat(u, chat).get();
        } catch (NoSuchElementException e) {
            player = new ApplePlayer(u, chat);
            applePlayerRepo.save(player);
        }
        return player;
    }

    public static CallerChat getChat(Update update) {
        Long chatId = update.getMessage().getChatId();
        CallerChat callerChat;
        try {
            callerChat = callerChatRepo.findById(chatId).get();
        } catch (NoSuchElementException e) {
            callerChat = callerChatRepo.save(new CallerChat(chatId));
        }
        return callerChat;
    }

    public static CallerChat getChat(Long chatId) {
        CallerChat callerChat;
        try {
            callerChat = callerChatRepo.findById(chatId).get();
        } catch (NoSuchElementException e) {
            callerChat = callerChatRepo.save(new CallerChat(chatId));
        }
        return callerChat;
    }

    public static String makeLink(Update update) {
        return String.format("<a href='tg://user?id=%d'>%s</a>", update.getMessage().getFrom().getId(),
                update.getMessage().getFrom().getFirstName());
    }

    public static String makeLink(Long id, String firstName) {
        return String.format("<a href='tg://user?id=%d'>%s</a>", id, firstName);
    }

    public static CallerUserRepo getCallerUserRepo() {
        return callerUserRepo;
    }

    public static void setCallerUserRepo(CallerUserRepo callerUserRepo) {
        Getter.callerUserRepo = callerUserRepo;
    }

    public static CallerChatRepo getCallerChatRepo() {
        return callerChatRepo;
    }

    public static void setCallerChatRepo(CallerChatRepo callerChatRepo) {
        Getter.callerChatRepo = callerChatRepo;
    }

    public static void setApplePlayerRepo(ApplePlayerRepo applePlayerRepo) {
        Getter.applePlayerRepo = applePlayerRepo;
    }

    public static ApplePlayerRepo getApplePlayerRepo() {
        return applePlayerRepo;
    }
}
