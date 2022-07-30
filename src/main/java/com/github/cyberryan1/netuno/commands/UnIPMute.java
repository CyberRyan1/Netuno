package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.helpers.command.ArgType;
import com.github.cyberryan1.cybercore.helpers.command.CyberCommand;
import com.github.cyberryan1.netuno.classes.IPPunishment;
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

public class UnIPMute extends CyberCommand {

    public UnIPMute() {
        super(
                "unipmute",
                Settings.UNIPMUTE_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.string(),
                "&8/&sunipmute &p(player)"
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
    public boolean execute( CommandSender sender, String args[] ) {
        final OfflinePlayer target = Bukkit.getOfflinePlayer( args[0] );

        List<OfflinePlayer> punishedAccounts = Utils.getDatabase().getPunishedAltsByType( target.getUniqueId().toString(), "ipmute" );
        List<IPPunishment> punishments = new ArrayList<>();
        for ( OfflinePlayer account : punishedAccounts ) {
            punishments.addAll( Utils.getDatabase().getIPPunishment( account.getUniqueId().toString(), "ipmute", true ) );
        }

        if ( punishments.size() >= 1 ) {
            for ( IPPunishment pun : punishments ) {
                Utils.getDatabase().setPunishmentActive( pun.getID(), false );
            }

            Punishment pun = new Punishment();
            pun.setPlayerUUID( target.getUniqueId().toString() );
            pun.setReason( "" );
            pun.setLength( -1L );
            pun.setDate( Time.getCurrentTimestamp() );
            pun.setType( "UnIPMute" );
            pun.setActive( false );

            pun.setStaffUUID( "CONSOLE" );
            if ( sender instanceof Player ) {
                Player staff = ( Player ) sender;
                pun.setStaffUUID( staff.getUniqueId().toString() );
            }

            Utils.getDatabase().addPunishment( pun );
            if ( target.isOnline() ) {
                Player targetOnline = target.getPlayer();
                Utils.sendPunishmentMsg( targetOnline, pun );
            }

            Utils.doPublicPunBroadcast( pun );
            Utils.doStaffPunBroadcast( pun );

            // Sends a notification to all online alts for the same punishment
            for ( OfflinePlayer alt : Utils.getDatabase().getAllAlts( target.getUniqueId().toString() ) ) {
                if ( alt.isOnline() && alt.getName().equals( target.getName() ) == false ) {
                    pun.setPlayerUUID( alt.getUniqueId().toString() );
                    Utils.sendPunishmentMsg( alt.getPlayer(), pun );
                }
            }
        }

        else {
            CommandErrors.sendNoPunishments( sender, target.getName(), "ipmute" );
        }

        return true;
    }
}
