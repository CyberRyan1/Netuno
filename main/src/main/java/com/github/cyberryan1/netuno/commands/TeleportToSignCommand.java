package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.spigot.command.CyberCommand;
import com.github.cyberryan1.cybercore.spigot.command.sent.SentCommand;
import com.github.cyberryan1.cybercore.spigot.command.settings.ArgType;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class TeleportToSignCommand extends CyberCommand {

    public TeleportToSignCommand() {
        super(
                "tptosign",
                Settings.SIGN_NOTIFS_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                "&8/&stptosign (world) (x) (y) (z)"
        );
        register(true);

        demandPermission( true );
        demandPlayer( true );
        setMinArgLength(4);
        setArgType(0, ArgType.STRING);
        setArgType(1, ArgType.DOUBLE);
        setArgType(2, ArgType.DOUBLE);
        setArgType(3, ArgType.DOUBLE);
    }

    @Override
    public List<String> tabComplete(SentCommand sentCommand) {
        return List.of();
    }

    @Override
    public boolean execute(SentCommand sentCommand) {
        Location loc = new Location(
                Bukkit.getWorld(sentCommand.getArg(0)),
                sentCommand.getDoubleAtArg(1),
                sentCommand.getDoubleAtArg(2),
                sentCommand.getDoubleAtArg(3)
        );

        final Player player = sentCommand.getPlayer();

        player.teleport(loc);

        return true;
    }
}
