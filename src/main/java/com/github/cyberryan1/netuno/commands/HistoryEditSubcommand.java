package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.spigot.command.CyberSubCommand;
import com.github.cyberryan1.cybercore.spigot.command.sent.SentCommand;
import com.github.cyberryan1.cybercore.spigot.command.sent.SentSubCommand;
import com.github.cyberryan1.cybercore.spigot.command.settings.ArgType;
import com.github.cyberryan1.cybercore.spigot.utils.CyberVaultUtils;
import com.github.cyberryan1.netuno.apimplement.ApiNetuno;
import com.github.cyberryan1.netuno.guis.history.HistoryEditGUI;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HistoryEditSubcommand extends CyberSubCommand {

    public HistoryEditSubcommand() {
        super(
                "edit",
                null,
                Settings.PERM_DENIED_MSG.coloredString(),
                "&8/&shistory &pedit (pun ID)"
        );

        setDemandPlayer( true );
        setDemandPermission( true );
        setMinArgLength( 1 );
        setArgType( 0, ArgType.INTEGER );
        setRunAsync( true );
    }

    @Override
    public List<String> tabComplete( SentCommand command, SentSubCommand subCommand ) {
        return List.of();
    }

    @Override
    public boolean execute( SentCommand command, SentSubCommand subCommand ) {
        final Player player = subCommand.getPlayer();
        final int punId = subCommand.getIntegerAtArg( 0 );

        NPunishment pun = ApiNetuno.getData().getNetunoPuns().getPunishment( punId );
        if ( pun != null ) {
            HistoryEditGUI gui = new HistoryEditGUI( pun.getPlayer(), player, punId );

            gui.open();
        }

        else {
            CommandErrors.sendPunishmentIDNotFound( player, punId );
        }

        return true;
    }

    @Override
    public boolean permissionsAllowed( CommandSender sender ) {
        return CyberVaultUtils.hasPerms( sender, Settings.HISTORY_TIME_PERMISSION.string() )
                || CyberVaultUtils.hasPerms( sender, Settings.HISTORY_REASON_PERMISSION.string() );
    }
}