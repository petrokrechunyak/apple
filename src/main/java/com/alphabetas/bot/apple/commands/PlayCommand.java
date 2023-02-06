package com.alphabetas.bot.apple.commands;

import com.alphabetas.bot.apple.model.AppleGame;
import com.alphabetas.bot.apple.model.ApplePlayer;
import com.alphabetas.bot.apple.model.enums.GameDesign;
import com.alphabetas.bot.apple.repo.AppleGameRepo;
import com.alphabetas.bot.apple.repo.ApplePlayerRepo;
import com.alphabetas.bot.apple.repo.NotificationRepo;
import com.alphabetas.bot.apple.service.MessageService;
import com.alphabetas.bot.apple.utils.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Transactional
@Component
@Slf4j
public class PlayCommand implements Command {

    private MessageService service;
    private AppleGameRepo appleGameRepo;
    @Autowired
    private ApplePlayerRepo applePlayerRepo;
    @Autowired
    private NotificationRepo notificationRepo;

    public PlayCommand(AppleGameRepo appleGameRepo, MessageService messageService, ApplePlayerRepo applePlayerRepo) {
        this.applePlayerRepo = applePlayerRepo;
        this.appleGameRepo = appleGameRepo;
        this.service = messageService;
    }

    public static String prepareGameString(AppleGame game) {
        String prepareStr;
        switch (game.getGameDesign()) {
            case MELON:
                prepareStr = prepareGameString(game, "\uD83C\uDF49", "\uD83C\uDF30", "Кавуни");
                break;
            default:
                prepareStr = prepareGameString(game, "\uD83C\uDF4E", "\uD83C\uDF4F", "Яблука");
                break;
        }
        return prepareStr;
    }

    public static String prepareGameString(AppleGame game, String main, String last, String text) {
        StringBuilder builder = new StringBuilder("Гра почалась! Гравці:\n")
                .append(Getter.makeLink(game.getPlayer1().getPlayer(),
                        game.getPlayer1().getFirstname()))
                .append(game.getPlayer1() == game.getCurrentPlayer() ? " ⬅️" : "")
                .append("\n")
                .append(Getter.makeLink(game.getPlayer2().getPlayer(),
                        game.getPlayer2().getFirstname()))
                .append(game.getPlayer2() == game.getCurrentPlayer() ? " ⬅️" : "")
                .append("\n").append(text).append(": ")
                .append(game.getApples())
                .append("\n").append(last + " ")
                .append((main + " ").repeat(game.getApples() - 1));
        return builder.toString();
    }

    public static InlineKeyboardMarkup prepareKeyboard(AppleGame game) {
        int apples = game.getApples();
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> allButtons = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        String btn;
        switch (game.getGameDesign()) {
            case MELON:
                btn = "\uD83C\uDF49";
                break;
            default:
                btn = "\uD83C\uDF4E";
                break;
        }

        row.add(button(btn, 1));
        if (apples >= 2)
            row.add(button(btn, 2));
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
    @Override
    @Transactional
    public void execute(Update update) {
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        log.info("Entered into execute with text {}", text);

        AppleGame game = prepareGame(update);
        if (game == null) {
            return;
        }


        SendMessage sendMessage = new SendMessage(chatId.toString(), prepareGameString(game));
        sendMessage.setReplyMarkup(prepareKeyboard(game));
        sendMessage.setParseMode("html");


        AppleGame oldGame = appleGameRepo.findByPlayer1AndChat(game.getPlayer1(),
                chatId);
        if (oldGame != null) {
            try {
                service.editMessage(chatId, oldGame.getMessageId(), "Гра завершена :(   ");
            } catch (Exception e) {
            }
            appleGameRepo.deleteAllByPlayer1AndChat(game.getPlayer1(), game.getChat());
        }
        Message message = service.sendMessage(sendMessage);
        game.setMessageId(message.getMessageId().longValue());

        appleGameRepo.save(game);


    }

    private AppleGame prepareGame(Update update) {
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        User user1 = update.getMessage().getFrom();

        ApplePlayer player1 = Getter.getPlayer(user1.getId(), chatId);
        player1.setFirstname(user1.getFirstName());

        ApplePlayer player2 = preparePlayer2(update);

        if (update.getMessage().getReplyToMessage() == null) {
            return null;
        }

        player2.setFirstname(update.getMessage().getReplyToMessage().getFrom().getFirstName());

        applePlayerRepo.save(player1);
        applePlayerRepo.save(player2);

        ApplePlayer currentPLayer;
        if (new Random().nextInt(2) == 1) {
            currentPLayer = player2;
        } else {
            currentPLayer = player1;
        }

        Integer apples = null;
        String[] args = text.split(" ");
        for (int i = 1; i < Math.min(args.length, 3); i++) {
            try {
                apples = Integer.parseInt(args[i]);
                if (apples < 15)
                    apples = 15;
                else if (apples > 50)
                    apples = 50;
                break;
            } catch (NumberFormatException e) {

            }
        }
        if (apples == null) {
            apples = new Random().nextInt(25) + 15;
        }

        GameDesign gameDesign;
        if (text.contains("#ХЕРСОН")) {
            gameDesign = GameDesign.MELON;
        }
        // DEFAULT
        else {
            gameDesign = GameDesign.DEFAULT;
        }


        return new AppleGame(chatId, player1, player2, currentPLayer, apples, gameDesign);

    }

    private ApplePlayer preparePlayer2(Update update) {

        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        String[] args = text.split(" ");
        Message msg = update.getMessage().getReplyToMessage();
        if (msg != null) {
            User player = msg.getFrom();
            return Getter.getPlayer(player.getId(), chatId);
        } else {
            service.sendMessage(chatId, "Щоб грати з кимось напишіть /play у відповідь на повідомлення тієї людини, або напишіть /play {ім'я людини з Кликуна}");
            return null;
        }
    }
}
