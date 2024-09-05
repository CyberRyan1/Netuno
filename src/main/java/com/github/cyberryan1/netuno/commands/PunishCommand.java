package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.spigot.command.CyberCommand;
import com.github.cyberryan1.cybercore.spigot.command.sent.SentCommand;
import com.github.cyberryan1.netuno.models.commands.CommandHelpInfo;
import com.github.cyberryan1.netuno.utils.settings.Settings;

import java.util.List;

public class PunishCommand extends CyberCommand {

    public PunishCommand( int helpOrder ) {
        super(
                "punish",
                Settings.PUNISH_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                "&8/&spunish &p(player) [punishment code]"
        );

        register( true );
        new CommandHelpInfo( this, helpOrder );
    }
    @Override
    public List<String> tabComplete( SentCommand sentCommand ) {
        // TODO
        return List.of();
    }

    @Override
    public boolean execute( SentCommand sentCommand ) {
        // TODO
        return true;
    }
}
