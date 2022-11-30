package com.alphabetas.bot.apple.commands;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class NoCommand implements Command{


    @Override
    public void execute(Update update) {

    }
}