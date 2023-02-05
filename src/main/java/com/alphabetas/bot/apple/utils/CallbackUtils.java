package com.alphabetas.bot.apple.utils;

import com.alphabetas.bot.apple.model.Notification;
import com.alphabetas.bot.apple.repo.NotificationRepo;
import com.alphabetas.bot.apple.service.MessageService;
import com.alphabetas.bot.apple.commands.PlayCommand;
import com.alphabetas.bot.apple.model.AppleGame;
import com.alphabetas.bot.apple.model.ApplePlayer;
import com.alphabetas.bot.apple.model.CallerChat;
import com.alphabetas.bot.apple.repo.AppleGameRepo;
import com.alphabetas.bot.apple.repo.ApplePlayerRepo;
import com.alphabetas.bot.apple.repo.CallerChatRepo;
import com.alphabetas.bot.apple.repo.CallerNameRepo;
import com.alphabetas.bot.apple.repo.CallerUserRepo;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.User;

@Component
public class CallbackUtils {

    private MessageService service;
    private CallerChatRepo callerChatRepo;
    private CallerNameRepo callerNameRepo;
    private CallerUserRepo callerUserRepo;
    private AppleGameRepo appleGameRepo;
    @Autowired
    private ApplePlayerRepo applePlayerRepo;
    @Autowired
    private NotificationRepo notificationRepo;

    public CallbackUtils(CallerChatRepo callerChatRepo, CallerNameRepo callerNameRepo, CallerUserRepo callerUserRepo, AppleGameRepo appleGameRepo,
                         MessageService messageService) {
        this.callerChatRepo = callerChatRepo;
        this.callerNameRepo = callerNameRepo;
        this.callerUserRepo = callerUserRepo;
        this.appleGameRepo = appleGameRepo;
        this.service = messageService;
    }


    @Transactional
    public void game(String data, User from, Long chatId, Long messageId) {

        CallerChat chat = Getter.getChat(chatId);
        AppleGame game = appleGameRepo.findByChatAndMessageId(chat, messageId);

        if(!game.getCurrentPlayer().getPlayer().getUserId().equals(from.getId()))
            return;

        int apples = data.length() / 2;
        game.getCurrentPlayer().setEaten(game.getCurrentPlayer().getEaten() + apples);
        applePlayerRepo.save(game.getCurrentPlayer());

        // change current player
//        notificationRepo.deleteAllByGame(game);
        game.setCurrentPlayer(getNotCurrentPlayer(game));
//        notificationRepo.save(new Notification(game));

        game.setApples(game.getApples() - apples);
        if(game.getApples() <= 1) {

            ApplePlayer winner;
            ApplePlayer loser;
            appleGameRepo.delete(game);

            if(game.getApples() == 0) {
                winner = game.getCurrentPlayer();
                loser = getNotCurrentPlayer(game);
            } else {
                loser = game.getCurrentPlayer();
                winner = getNotCurrentPlayer(game);
            }

            winner.setGames(winner.getGames()+1);
            loser.setGames(loser.getGames()+1);

            int score = new Random().nextInt(3)+5;
            winner.setScore(winner.getScore()+score);
            loser.setScore(loser.getScore()-score);
            StringBuilder builder = new StringBuilder("Гра закінчена! Переможець:\n")
                    .append(Getter.makeLink(winner.getPlayer().getUserId(),
                            winner.getPlayer().getFirstname()))
                    .append(" +").append(score).append("\uD83C\uDFC6\n")
                    .append(Getter.makeLink(loser.getPlayer().getUserId(),
                            loser.getPlayer().getFirstname()))
                    .append(" -").append(score).append("\uD83C\uDFC6");
            service.editMessage(chatId, game.getMessageId(), builder.toString());

            applePlayerRepo.save(winner);
            applePlayerRepo.save(loser);


//            notificationRepo.deleteAllByGame(game);
            appleGameRepo.deleteAllByPlayer1AndChat(game.getPlayer1(), chat);

            return;
        }



        EditMessageText editMessageText = new EditMessageText(PlayCommand.prepareGameString(game));
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(game.getMessageId().intValue());
        editMessageText.setReplyMarkup(PlayCommand.prepareKeyboard(game));
        editMessageText.setParseMode("html");
        service.sendMessage(editMessageText);

        appleGameRepo.save(game);

    }

    private ApplePlayer getNotCurrentPlayer(AppleGame game) {
        return game.getCurrentPlayer() == game.getPlayer1()
                ? game.getPlayer2()
                : game.getPlayer1();
    }

}
