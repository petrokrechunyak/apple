package com.alphabetas.bot.apple.commands;

import com.alphabetas.bot.apple.model.ApplePlayer;
import com.alphabetas.bot.apple.repo.ApplePlayerRepo;
import com.alphabetas.bot.apple.service.MessageService;
import com.alphabetas.bot.apple.utils.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Slf4j
@Component
public class TopCommand implements Command {

    MessageService service;
    private ApplePlayerRepo applePlayerRepo;

    public TopCommand(MessageService messageService, ApplePlayerRepo applePlayerRepo) {
        this.service = messageService;
        this.applePlayerRepo = applePlayerRepo;
    }

    @Override
    public void execute(Update update) {
        Long chatId = update.getMessage().getChatId();
        List<ApplePlayer> top = applePlayerRepo.findTop10ByChatOrderByScoreDesc(chatId);
        StringBuilder builder = new StringBuilder("<b>Топ гравців чату по балам</b>\n");
        for (int i = 0; i < top.size(); i++) {
            ApplePlayer p = top.get(i);
            builder.append("<b>").append(i + 1).append(".</b> ")
                    .append(Getter.makeLink(p.getPlayer(), p.getFirstname()))
                    .append(" — ").append(p.getScore()).append("\uD83C\uDFC6\n");
        }
        top = applePlayerRepo.findTop10ByChatOrderByGamesDesc(chatId);
        builder.append("\n<b>Топ гравців чату по іграм</b>\n");
        for (int i = 0; i < top.size(); i++) {
            ApplePlayer p = top.get(i);
            builder.append("<b>").append(i + 1).append(".</b> ")
                    .append(Getter.makeLink(p.getPlayer(), p.getFirstname()))
                    .append(" — ").append(p.getGames()).append("\uD83D\uDD79\n");
        }
        top = applePlayerRepo.findTop10ByChatOrderByEatenDesc(chatId);
        builder.append("\n<b>Топ голодних гравців чату</b>\n");
        for (int i = 0; i < top.size(); i++) {
            ApplePlayer p = top.get(i);
            builder.append("<b>").append(i + 1).append(".</b> ")
                    .append(Getter.makeLink(p.getPlayer(), p.getFirstname()))
                    .append(" — ").append(p.getEaten()).append("\uD83C\uDF4E\n");
        }

        SendMessage message = new SendMessage(chatId.toString(), "Топ 10 гравців");
        message.enableHtml(true);
        message.disableNotification();
        Message m = service.sendMessage(message);
        service.editMessage(chatId, m.getMessageId().longValue(), builder.toString());
    }
}
