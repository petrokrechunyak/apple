package com.alphabetas.bot.apple.commands;

import com.alphabetas.bot.apple.service.MessageService;
import com.alphabetas.bot.apple.model.ApplePlayer;
import com.alphabetas.bot.apple.model.CallerChat;
import com.alphabetas.bot.apple.repo.ApplePlayerRepo;
import com.alphabetas.bot.apple.repo.CallerChatRepo;
import com.alphabetas.bot.apple.repo.CallerNameRepo;
import com.alphabetas.bot.apple.repo.CallerUserRepo;
import com.alphabetas.bot.apple.utils.Getter;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
public class TopCommand implements Command{

    MessageService service;
    private CallerChatRepo callerChatRepo;
    private CallerNameRepo callerNameRepo;
    private CallerUserRepo callerUserRepo;
    private ApplePlayerRepo applePlayerRepo;

    public TopCommand(CallerChatRepo callerChatRepo, CallerNameRepo callerNameRepo, CallerUserRepo callerUserRepo,
                      ApplePlayerRepo applePlayerRepo, MessageService messageService) {
        this.callerChatRepo = callerChatRepo;
        this.callerNameRepo = callerNameRepo;
        this.callerUserRepo = callerUserRepo;
        this.applePlayerRepo = applePlayerRepo;
        this.service = messageService;
    }

    @Override
    public void execute(Update update) {
        Long chatId = update.getMessage().getChatId();
        CallerChat chat = Getter.getChat(chatId);
        List<ApplePlayer> top = applePlayerRepo.findTop10ByChatOrderByScoreDesc(chat);
        StringBuilder builder = new StringBuilder("<b>Топ гравців чату по балам</b>\n");
        for(int i = 0; i < top.size(); i++) {
            ApplePlayer p = top.get(i);
            builder.append("<b>").append(i+1).append(".</b> ")
                    .append(Getter.makeLink(p.getPlayer().getUserId(), p.getPlayer().getFirstname()))
                    .append(" — ").append(p.getScore()).append("\uD83C\uDFC6\n");
        }
        top = applePlayerRepo.findTop10ByChatOrderByGamesDesc(chat);
        builder.append("\n<b>Топ гравців чату по іграм</b>\n");
        for(int i = 0; i < top.size(); i++) {
            ApplePlayer p = top.get(i);
            builder.append("<b>").append(i+1).append(".</b> ")
                    .append(Getter.makeLink(p.getPlayer().getUserId(), p.getPlayer().getFirstname()))
                    .append(" — ").append(p.getGames()).append("\uD83D\uDD79\n");
        }
        top = applePlayerRepo.findTop10ByChatOrderByEatenDesc(chat);
        builder.append("\n<b>Топ голодних гравців чату</b>\n");
        for(int i = 0; i < top.size(); i++) {
            ApplePlayer p = top.get(i);
            builder.append("<b>").append(i+1).append(".</b> ")
                    .append(Getter.makeLink(p.getPlayer().getUserId(), p.getPlayer().getFirstname()))
                    .append(" — ").append(p.getEaten()).append("\uD83C\uDF4E\n");
        }

        SendMessage message = new SendMessage(chatId.toString(), "Топ 10 гравців");
        message.enableHtml(true);
        message.disableNotification();
        Message m = service.sendMessage(message);
        service.editMessage(chatId, m.getMessageId().longValue(), builder.toString());
    }
}
