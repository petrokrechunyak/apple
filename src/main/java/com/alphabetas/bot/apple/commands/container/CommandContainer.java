package com.alphabetas.bot.apple.commands.container;

import com.alphabetas.bot.apple.commands.Command;
import com.alphabetas.bot.apple.commands.NoCommand;
import com.alphabetas.bot.apple.commands.PlayCommand;
import com.alphabetas.bot.apple.commands.TopCommand;
import com.alphabetas.bot.apple.repo.AppleGameRepo;
import com.alphabetas.bot.apple.repo.ApplePlayerRepo;
import com.alphabetas.bot.apple.service.MessageService;
import com.alphabetas.bot.apple.utils.Getter;

import java.util.HashMap;
import java.util.Map;

public class CommandContainer {
    private NoCommand noCommand;

    private Map<String, Command> commands = new HashMap<>();

    public CommandContainer(ApplePlayerRepo applePlayerRepo, AppleGameRepo appleGameRepo,
                            MessageService messageService) {
        this(applePlayerRepo);
        PlayCommand command = new PlayCommand(appleGameRepo, messageService, applePlayerRepo);

        commands.put("/play", command);
        commands.put("/top", new TopCommand(messageService, applePlayerRepo));
        commands.put("плей", command);
    }


    public CommandContainer(ApplePlayerRepo applePlayerRepo) {
        Getter.setApplePlayerRepo(applePlayerRepo);
    }

    public Command getCommand(String command) {
        return commands.getOrDefault(command, new NoCommand());
    }
}
