package com.alphabetas.bot.apple.service;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface MessageService {

    Message sendMessage(Long chatId, String text);

    Message sendMessage(BotApiMethod sendMessage);

    boolean deleteMessage(Long chatId, Integer messageId);

    Message editMessage(Long chatId, Long updateMessageId, String text);

    Message editMessage(Long chatId, Long updateMessageId, String text, boolean removeMarkup);


}
