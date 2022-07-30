package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.helpers.command.ArgType;
import com.github.cyberryan1.cybercore.helpers.command.CyberCommand;
import com.github.cyberryan1.netuno.classes.Punishment;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.Time;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Unmute extends CyberCommand {

    public Unmute() {
        super(
                "unmute",
                Settings.UNMUTE_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.string(),
                "&8/&sunmute &p(player)"
        );
        register( true );

        demandPermission( true );
        setMinArgs( 1 );
        setArgType( 0, ArgType.OFFLINE_PLAYER );
        setAsync( true );
    }

    @Override
    public List<String> tabComplete( CommandSender sender, String[] args ) {
        return List.of();
    }

    @Override
    // /unmute (player)
    public boolean execute( CommandSender sender, String args[] ) {
        final OfflinePlayer target = Bukkit.getOfflinePlayer( args[0] );

        ArrayList<Punishment> punishments = Utils.getDatabase().getPunishment( target.getUniqueId().toString(), "mute", true );
        if ( punishments.size() >= 1 ) {
            for ( Punishment pun : punishments ) {
                Utils.getDatabase().setPunishmentActive( pun.getID(), false );
            }

            Punishment unmutePun = new Punishment();
            unmutePun.setPlayerUUID( target.getUniqueId().toString() );
            unmutePun.setReason( "" );
            unmutePun.setLength( -1L );
            unmutePun.setDate( Time.getCurrentTimestamp() );
            unmutePun.setType( "Unmute" );
            unmutePun.setActive( false );

            unmutePun.setStaffUUID( "CONSOLE" );
            if ( sender instanceof Player ) {
                Player staff = ( Player ) sender;
                unmutePun.setStaffUUID( staff.getUniqueId().toString() );
            }

            Utils.getDatabase().addPunishment( unmutePun );
            if ( target.isOnline() ) {
                Player targetOnline = target.getPlayer();
                Utils.sendPunishmentMsg( targetOnline, unmutePun );
            }

            Utils.doPublicPunBroadcast( unmutePun );
            Utils.doStaffPunBroadcast( unmutePun );
        }

        else {
            CommandErrors.sendNoPunishments( sender, target.getName(), "mute" );
        }

        return true;
    }
}
