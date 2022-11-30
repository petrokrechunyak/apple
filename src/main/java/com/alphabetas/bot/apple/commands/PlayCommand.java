package com.alphabetas.bot.apple.commands;

import com.alphabetas.bot.apple.model.AppleGame;
import com.alphabetas.bot.apple.model.CallerChat;
import com.alphabetas.bot.apple.model.CallerName;
import com.alphabetas.bot.apple.model.CallerUser;
import com.alphabetas.bot.apple.model.Notification;
import com.alphabetas.bot.apple.model.enums.GameDesign;
import com.alphabetas.bot.apple.repo.CallerNameRepo;
import com.alphabetas.bot.apple.repo.CallerUserRepo;
import com.alphabetas.bot.apple.repo.NotificationRepo;
import com.alphabetas.bot.apple.service.MessageService;
import com.alphabetas.bot.apple.utils.Getter;
import com.alphabetas.bot.apple.model.ApplePlayer;
import com.alphabetas.bot.apple.repo.ApplePlayerRepo;
import com.alphabetas.bot.apple.repo.CallerChatRepo;
import com.alphabetas.bot.apple.repo.AppleGameRepo;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.management.Descriptor;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Transactional
@Component
@Slf4j
public class PlayCommand implements Command{

    @Autowired
    private MessageService service;
    private CallerChatRepo callerChatRepo;
    private CallerNameRepo callerNameRepo;
    private CallerUserRepo callerUserRepo;
    private AppleGameRepo appleGameRepo;
    @Autowired
    private ApplePlayerRepo applePlayerRepo;
    @Autowired
    private NotificationRepo notificationRepo;

    public PlayCommand(CallerChatRepo callerChatRepo, CallerNameRepo callerNameRepo, CallerUserRepo callerUserRepo, AppleGameRepo appleGameRepo) {
        this.callerChatRepo = callerChatRepo;
        this.callerNameRepo = callerNameRepo;
        this.callerUserRepo = callerUserRepo;
        this.appleGameRepo = appleGameRepo;
    }

    @Transactional
    @Override
    public void execute(Update update) {
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        log.info("Entered into execute with text {}", text);

        AppleGame game = prepareGame(update);
        if(game == null) {
            return;
        }


        SendMessage sendMessage = new SendMessage(chatId.toString(), prepareGameString(game));
        sendMessage.setReplyMarkup(prepareKeyboard(game));
        sendMessage.setParseMode("html");


        AppleGame oldGame = appleGameRepo.findByPlayer1AndChat(game.getPlayer1(), Getter.getChat(chatId));
        if(oldGame != null) {
            try {
                service.editMessage(chatId, oldGame.getMessageId(), "Гра завершена :(   ");
            } catch (Exception e) {}
            notificationRepo.deleteAllByGame(oldGame);
            appleGameRepo.deleteAllByPlayer1AndChat(game.getPlayer1(), game.getChat());
        }
        Message message = service.sendMessage(sendMessage);
        game.setMessageId(message.getMessageId().longValue());

        appleGameRepo.save(game);
        notificationRepo.save(new Notification(game));


    }

    private AppleGame prepareGame(Update update) {
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        CallerChat chat = Getter.getChat(chatId);
        CallerUser user1 = Getter.getUser(update.getMessage().getFrom(), chatId);
        CallerUser user2 = preparePlayer2(update);
        if(user2 == null)
            return null;

        ApplePlayer player1 = Getter.getPlayer(user1, chat);
        ApplePlayer player2 = Getter.getPlayer(user2, chat);

        ApplePlayer currentPLayer;
        if(new Random().nextInt(2) == 1) {
            currentPLayer = player2;
        } else {
            currentPLayer = player1;
        }

        Integer apples = null;
        String[] args = text.split(" ");
        for(int i = 1; i < Math.min(args.length, 3); i++) {
            try {
                apples = Integer.parseInt(args[i]);
                if(apples < 15)
                    apples = 15;
                else if(apples > 50)
                    apples = 50;
                break;
            } catch (NumberFormatException e) {

            }
        }
        if(apples == null) {
            apples = new Random().nextInt(15, 40);
        }

        GameDesign gameDesign;
        if(text.contains("#ХЕРСОН")) {
            gameDesign = GameDesign.MELON;
        }
        // DEFAULT
        else {
            gameDesign = GameDesign.DEFAULT;
        }


        return new AppleGame(chat, player1, player2, currentPLayer, apples, gameDesign);

    }

    private CallerUser preparePlayer2(Update update) {
        CallerUser user;
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        String[] args = text.split(" ");
        Message msg  = update.getMessage().getReplyToMessage();
        try {
            if(msg != null) {
                User player = msg.getFrom();
                user = Getter.getUser(player, chatId);
            } else {
                int argNum = args.length;
                CallerName name = callerNameRepo.getByChatIdAndNameIgnoreCase(chatId, args[argNum-1]);
                user = name.getCallerUser();
            }
        } catch (NullPointerException e) {
            service.sendMessage(chatId, "Щоб грати з кимось напишіть /play у відповідь на повідомлення тієї людини, або напишіть /play {ім'я людини з Кликуна}");
            return null;
        }
        return user;
    }

    public static String prepareGameString(AppleGame game) {
        String prepareStr;
        switch (game.getGameDesign()) {
            case MELON -> prepareStr = prepareGameString(game, "\uD83C\uDF49", "\uD83C\uDF30", "Кавуни");
            default -> prepareStr = prepareGameString(game, "\uD83C\uDF4E", "\uD83C\uDF4F", "Яблука");
        }
        return prepareStr;
    }

    public static String prepareGameString(AppleGame game, String main, String last, String text) {
        StringBuilder builder = new StringBuilder("Гра почалась! Гравці:\n")
                .append(Getter.makeLink(game.getPlayer1().getPlayer().getUserId(),
                        game.getPlayer1().getPlayer().getFirstname()))
                .append(game.getPlayer1() == game.getCurrentPlayer() ?" ⬅️" : "")
                .append("\n")
                .append(Getter.makeLink(game.getPlayer2().getPlayer().getUserId(),
                        game.getPlayer2().getPlayer().getFirstname()))
                .append(game.getPlayer2() == game.getCurrentPlayer() ?" ⬅️" : "")
                .append("\n").append(text).append(":")
                .append(game.getApples())
                .append("\n").append(last)
                .append(main.repeat(game.getApples()-1));
        return builder.toString();
    }

    public static InlineKeyboardMarkup prepareKeyboard(AppleGame game) {
        int apples = game.getApples();
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> allButtons = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        String btn;
        switch (game.getGameDesign()) {
            case MELON -> btn = "\uD83C\uDF49";
            default -> btn = "\uD83C\uDF4E";
        }

        row.add(button(btn,1));
        if(apples >= 2)
            row.add(button(btn,2));
        if (apples >= 3)
            row.add(button(btn, 3));

        allButtons.add(row);
        keyboard.setKeyboard(allButtons);

        return keyboard;
    }

    private static InlineKeyboardButton button(String design, int apples) {

        InlineKeyboardButton apple = new InlineKeyboardButton(design.repeat(apples));
        apple.setCallbackData("\uD83C\uDF4E".repeat(apples));
        return apple;
    }

    public void setCallerChatRepo(CallerChatRepo callerChatRepo) {
        this.callerChatRepo = callerChatRepo;
    }

    public void setCallerNameRepo(CallerNameRepo callerNameRepo) {
        this.callerNameRepo = callerNameRepo;
    }

    public void setCallerUserRepo(CallerUserRepo callerUserRepo) {
        this.callerUserRepo = callerUserRepo;
    }

    public void setGameAppleRepo(AppleGameRepo appleGameRepo) {
        this.appleGameRepo = appleGameRepo;
    }
}
