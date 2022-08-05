package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.helpers.command.ArgType;
import com.github.cyberryan1.cybercore.helpers.command.CyberSubcommand;
import com.github.cyberryan1.cybercore.helpers.command.SubcommandStatus;
import com.github.cyberryan1.cybercore.utils.CoreUtils;
import com.github.cyberryan1.netuno.api.ApiNetuno;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HistoryResetSubcommand extends CyberSubcommand {

    public HistoryResetSubcommand() {
        super(
                "reset",
                Settings.HISTORY_RESET_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.string(),
                "&8/&shistory &preset (player)"
        );

        setDemandPermission( true );
        setMinArgs( 2 );
        setArgType( 1, ArgType.OFFLINE_PLAYER );
    }

    @Override
    public List<String> tabComplete( CommandSender sender, String args[] ) {
        return List.of();
    }

    @Override
    public SubcommandStatus execute( CommandSender sender, String args[] ) {
        final Player player = ( Player ) sender;
        final OfflinePlayer target = Bukkit.getOfflinePlayer( args[1] );

        CoreUtils.sendMsg( player, "&sDeleting all punishments for &p" + target.getName() + "&s..." );

        List<NPunishment> punishments = ApiNetuno.getData().getNetunoPuns().getPunishments( target );
        ApiNetuno.getData().getNetunoPuns().removePunishments( target );

        String plural = ( punishments.size() == 1 ) ? ( "punishment" ) : ( "punishments" );
        CoreUtils.sendMsg( player, "&sSuccessfully deleted &p" + punishments.size() + " &s"
                + plural + " from &p" + target.getName() + "&s's history" );

        return SubcommandStatus.NORMAL;
    }
}