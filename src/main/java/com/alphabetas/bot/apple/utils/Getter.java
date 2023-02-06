package com.alphabetas.bot.apple.utils;


import com.alphabetas.bot.apple.commands.PlayCommand;
import com.alphabetas.bot.apple.model.AppleGame;
import com.alphabetas.bot.apple.model.ApplePlayer;
import com.alphabetas.bot.apple.model.Notification;
import com.alphabetas.bot.apple.repo.ApplePlayerRepo;
import com.alphabetas.bot.apple.repo.NotificationRepo;
import com.alphabetas.bot.apple.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Component
public class Getter {
    private static ApplePlayerRepo applePlayerRepo;
    @Autowired
    private NotificationRepo notificationRepo;
    @Autowired
    private MessageService service;

    private static String getMessageString(AppleGame game) {
        Long chatId = game.getChat();
        Long messageId = game.getMessageId();
        String chat = chatId.toString().startsWith("-100") ? chatId.toString().substring(4) : chatId.toString();
        return String.format("https://t.me/c/%d/%d", Long.parseLong(chat), messageId);
    }

    public static ApplePlayer getPlayer(Long u, Long chat) {
        ApplePlayer player;
        try {
            player = applePlayerRepo.findByPlayerAndChat(u, chat).get();
        } catch (NoSuchElementException e) {
            player = new ApplePlayer(u, chat);
            applePlayerRepo.save(player);
        }
        return player;
    }

    public static String makeLink(Update update) {
        return String.format("<a href='tg://user?id=%d'>%s</a>", update.getMessage().getFrom().getId(),
                update.getMessage().getFrom().getFirstName());
    }

    public static String makeLink(Long id, String firstName) {
        return String.format("<a href='tg://user?id=%d'>%s</a>", id, firstName);
    }

    public static void setApplePlayerRepo(ApplePlayerRepo applePlayerRepo) {
        Getter.applePlayerRepo = applePlayerRepo;
    }

    //    @Scheduled(fixedDelay = 10000)
    public void notification() {

        var notificationList = notificationRepo.findAll();
        notificationList.removeIf(x -> x.getWaitingTime() > 30);
        notificationRepo.deleteAll();
        notificationRepo.saveAll(notificationList);
        var ref = new Object() {
            List<Notification> newNotifications = new ArrayList<>();
        };
        notificationList = notificationList.stream().peek(x -> x.setWaitingTime(x.getWaitingTime() + 1)).collect(Collectors.toList());
        notificationRepo.saveAll(notificationList);
        notificationList.stream().filter(x -> x.getWaitingTime() == 6 || x.getWaitingTime() >= 30)
                .forEach(x -> {
                    String msgText = PlayCommand.prepareGameString(x.getGame());
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setParseMode("html");

                    InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> allButtons = new ArrayList<>();
                    List<InlineKeyboardButton> row = new ArrayList<>();
                    InlineKeyboardButton button = new InlineKeyboardButton("В гру");
                    button.setUrl(getMessageString(x.getGame()));

                    row.add(button);
                    allButtons.add(row);
                    keyboard.setKeyboard(allButtons);

                    sendMessage.setReplyMarkup(keyboard);
                    sendMessage.setChatId(x.getGame().getCurrentPlayer().getPlayer());
                    sendMessage.setText(msgText);
                    service.sendMessage(sendMessage);

                    ref.newNotifications.add(x);

                });

        ref.newNotifications = ref.newNotifications.stream().filter(x -> x.getWaitingTime() >= 30).collect(Collectors.toList());
        notificationRepo.deleteAll(ref.newNotifications);
    }
}
