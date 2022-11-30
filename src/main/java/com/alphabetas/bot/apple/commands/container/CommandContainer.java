package com.alphabetas.bot.apple.commands.container;

import com.alphabetas.bot.apple.commands.Command;
import com.alphabetas.bot.apple.commands.NoCommand;
import com.alphabetas.bot.apple.commands.PlayCommand;
import com.alphabetas.bot.apple.commands.TopCommand;
import com.alphabetas.bot.apple.repo.CallerNameRepo;
import com.alphabetas.bot.apple.repo.CallerUserRepo;
import com.alphabetas.bot.apple.service.MessageService;
import com.alphabetas.bot.apple.utils.Getter;
import com.alphabetas.bot.apple.repo.ApplePlayerRepo;
import com.alphabetas.bot.apple.repo.CallerChatRepo;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommandContainer {

    // Repos
    private CallerChatRepo callerChatRepo;
    private CallerNameRepo callerNameRepo;
    private CallerUserRepo callerUserRepo;
    private NoCommand noCommand;

    private Map<String, Command> commands = new HashMap<>();

    @Autowired
    public CommandContainer(CallerChatRepo callerChatRepo,
                            CallerNameRepo callerNameRepo, CallerUserRepo callerUserRepo,
                            ApplePlayerRepo applePlayerRepo,
                            PlayCommand playCommand, TopCommand topCommand,
                            NoCommand noCommand) {
        this(callerChatRepo, callerNameRepo, callerUserRepo, applePlayerRepo);
        this.noCommand = noCommand;
        commands.put("/play", playCommand);
        commands.put("/top", topCommand);
        commands.put("плей", playCommand);
    }


    public Command getCommand(String command) {
        return commands.getOrDefault(command, noCommand);
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
