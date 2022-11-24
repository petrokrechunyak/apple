package com.alphabetas.bot.apple;

import com.alphabetas.bot.apple.commands.container.CommandContainer;
import com.alphabetas.bot.apple.service.MessageService;
import com.alphabetas.bot.apple.service.MessageServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


class AppleBotTest {

    static AppleBot bot;
    static CommandContainer container;
    static MessageService service;

    @BeforeAll
    static void prepare() {
        bot = mock(AppleBot.class);
        container = mock(CommandContainer.class);
        service = mock(MessageServiceImpl.class);
    }

    @Test
    void readCommand() {
//
//        bot.readCommand(new Update(), "/start");
//        when(container.getCommand("/start")).thenReturn(new StartCommand(service));
//
//        verify(bot.container.getCommand("/start"), times(1));

    }
}