package com.alphabetas.bot.apple.service;

import com.alphabetas.bot.apple.AppleBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class MessageServiceImpl implements MessageService {
    private AppleBot bot;

    public MessageServiceImpl(AppleBot bot) {
        this.bot = bot;
    }

    //@Autowired
    //public void setBot(AppleBot bot) {
    //    this.bot = bot;
    //}

    @Override
    public Message sendMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage(chatId.toString(), text);
        sendMessage.setParseMode("html");
        try {
            return bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Message didn't sent: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public Message sendMessage(BotApiMethod sendMessage) {
        try {
            return (Message) bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Message didn't sent: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteMessage(Long chatId, Integer messageId) {
        DeleteMessage deleteMessage = new DeleteMessage(chatId.toString(), messageId);
        try {
            return bot.execute(deleteMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Message editMessage(Long chatId, Long updateMessageId, String text, boolean removeMarkup) {
        EditMessageText editMessage = new EditMessageText(text);
        editMessage.setMessageId(updateMessageId.intValue());
        editMessage.setChatId(chatId);
        editMessage.setParseMode("html");
        if(removeMarkup)
            editMessage.setReplyMarkup(null);

        try {
            return (Message) bot.execute(editMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Message editMessage(Long chatId, Long updateMessageId, String text) {
        return editMessage(chatId, updateMessageId, text, false);
    }
}
