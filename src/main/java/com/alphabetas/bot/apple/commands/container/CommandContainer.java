package com.alphabetas.bot.apple.commands.container;

import com.alphabetas.bot.apple.commands.Command;
import com.alphabetas.bot.apple.commands.NoCommand;
import com.alphabetas.bot.apple.commands.PlayCommand;
import com.alphabetas.bot.apple.commands.TopCommand;
import com.alphabetas.bot.apple.repo.*;
import com.alphabetas.bot.apple.service.MessageService;
import com.alphabetas.bot.apple.utils.Getter;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class CommandContainer {

    // Repos
    private CallerChatRepo callerChatRepo;
    private CallerNameRepo callerNameRepo;
    private CallerUserRepo callerUserRepo;
    private NoCommand noCommand;

    private Map<String, Command> commands = new HashMap<>();

    public CommandContainer(CallerChatRepo callerChatRepo,
                            CallerNameRepo callerNameRepo, CallerUserRepo callerUserRepo,
                            ApplePlayerRepo applePlayerRepo, AppleGameRepo appleGameRepo,
                            MessageService messageService) {
        this(callerChatRepo, callerNameRepo, callerUserRepo, applePlayerRepo);
        PlayCommand command = new PlayCommand(callerChatRepo, callerNameRepo, callerUserRepo, appleGameRepo,
                messageService);

        commands.put("/play", command);
        commands.put("/top", new TopCommand(callerChatRepo, callerNameRepo, callerUserRepo, applePlayerRepo, messageService));
        commands.put("плей", command);
    }


    public Command getCommand(String command) {
        return commands.getOrDefault(command, new NoCommand());
    }

    public CommandContainer(CallerChatRepo callerChatRepo, CallerNameRepo callerNameRepo, CallerUserRepo callerUserRepo,
                            ApplePlayerRepo applePlayerRepo) {
        this.callerChatRepo = callerChatRepo;
        this.callerNameRepo = callerNameRepo;
        this.callerUserRepo = callerUserRepo;

        Getter.setCallerChatRepo(callerChatRepo);
        Getter.setCallerUserRepo(callerUserRepo);
        Getter.setApplePlayerRepo(applePlayerRepo);
    }
}
