package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.helpers.command.ArgType;
import com.github.cyberryan1.cybercore.helpers.command.CyberSubcommand;
import com.github.cyberryan1.cybercore.helpers.command.SubcommandStatus;
import com.github.cyberryan1.cybercore.utils.CoreUtils;
import com.github.cyberryan1.netuno.classes.IPPunishment;
import com.github.cyberryan1.netuno.classes.Punishment;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
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

        ArrayList<Punishment> allPuns = Utils.getDatabase().getAllPunishments( target.getUniqueId().toString() );
        for ( Punishment pun : allPuns ) {
            Utils.getDatabase().deletePunishment( pun.getID() );
        }

        ArrayList<IPPunishment> ipPuns = Utils.getDatabase().getIPPunishment( target.getUniqueId().toString() );
        for ( IPPunishment pun : ipPuns ) {
            Utils.getDatabase().deletePunishment( pun.getID() );
        }

        ArrayList<Integer> notifs = Utils.getDatabase().searchNotifByUUID( target.getUniqueId().toString() );
        for ( int id : notifs ) {
            Utils.getDatabase().removeNotif( id );
        }

        Utils.getDatabase().removeAllGUIPun( target );

        int deletedSize = allPuns.size() + ipPuns.size() + notifs.size();
        String plural = ( deletedSize == 1 ) ? ( "punishment" ) : ( "punishments" );
        CoreUtils.sendMsg( player, "&sSuccessfully deleted &p" + deletedSize + " &s" + plural + " from &p" + target.getName() + "&s's history" );

        return SubcommandStatus.NORMAL;
    }
}