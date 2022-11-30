package com.alphabetas.bot.apple;

import com.alphabetas.bot.apple.commands.container.CommandContainer;
import com.alphabetas.bot.apple.service.MessageService;
import com.alphabetas.bot.apple.utils.CallbackUtils;
import com.alphabetas.bot.apple.service.MessageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
public class AppleBot extends TelegramLongPollingBot {

    @Autowired
    CommandContainer container;
    @Autowired
    CallbackUtils callbackUtils;
    MessageService service;

    public AppleBot() {
        this.service = new MessageServiceImpl(this);
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message givenMessage = update.getMessage();
        if(update.hasMessage() && givenMessage != null) {
            log.info("Update received with text: {}", update.getMessage().getText());
            String msgText = givenMessage.getText();
                readCommand(update, msgText.split(" ")[0]);
        }
        if(update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();

            callbackUtils.game(callbackQuery.getData(), callbackQuery.getFrom(),
                        callbackQuery.getMessage().getChatId(), callbackQuery.getMessage().getMessageId().longValue());
        }
    }

    public void readCommand(Update update, String text) {
        String[] parts = text.split("@");
        log.info("Entered into readCommand, {}", parts);
        if(parts.length == 1 || parts[1].equals("GameDontEatGreenAppleBot")) {
            container.getCommand(parts[0].toLowerCase()).execute(update);
        }
    }

    @Value("${bot.username}")
    private String username;
    @Value("${bot.token}")
    private String token;

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }
}
