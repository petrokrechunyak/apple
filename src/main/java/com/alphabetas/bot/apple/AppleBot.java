package com.alphabetas.bot.apple;

import com.alphabetas.bot.apple.commands.container.CommandContainer;
import com.alphabetas.bot.apple.repo.AppleGameRepo;
import com.alphabetas.bot.apple.repo.ApplePlayerRepo;
import com.alphabetas.bot.apple.service.MessageService;
import com.alphabetas.bot.apple.service.MessageServiceImpl;
import com.alphabetas.bot.apple.utils.CallbackUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
public class AppleBot extends TelegramLongPollingBot {
    private static final String MYCHAT = "731921794";
    CallbackUtils callbackUtils;
    MessageService service;
    private CommandContainer container;
    @Value("${bot.username}")
    private String username;
    @Value("${bot.token}")
    private String token;

    public AppleBot(ApplePlayerRepo applePlayerRepo, AppleGameRepo appleGameRepo) {
        MessageService messageService = new MessageServiceImpl(this);
        this.container = new CommandContainer(applePlayerRepo, appleGameRepo, messageService);
        this.callbackUtils = new CallbackUtils(appleGameRepo, messageService, applePlayerRepo);

    }

    @Override
    public void onUpdateReceived(Update update) {

        Message givenMessage = update.getMessage();
        if (update.hasMessage() && givenMessage != null) {
            String msgText = givenMessage.getText();
            readCommand(update, msgText.split(" ")[0]);
        }
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();

            callbackUtils.game(callbackQuery.getData(), callbackQuery.getFrom(),
                    callbackQuery.getMessage().getChatId(), callbackQuery.getMessage().getMessageId().longValue());
        }
    }

    public void readCommand(Update update, String text) {
        String[] parts = text.split("@");
        log.info("Entered into readCommand, {}", parts);
        if (parts.length == 1 || parts[1].equals("GameDontEatGreenAppleBot")) {
            container.getCommand(parts[0].toLowerCase()).execute(update);
        }
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }
}
