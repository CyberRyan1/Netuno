package com.github.cyberryan1.netuno.listeners;

import com.github.cyberryan1.netuno.utils.*;
import com.github.cyberryan1.netuno.utils.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;

public class JoinListener implements Listener {

    private final Database DATA = Utils.getDatabase();

    @EventHandler
    public void onPlayerJoin( PlayerJoinEvent event ) {
        //
        // High priority things below here
        //

        // Log the player's IP address into the database
        String ipAddress = event.getPlayer().getAddress().getAddress().getHostAddress();
        if ( DATA.playerHasIP( event.getPlayer().getUniqueId().toString(), ipAddress ) == false ) {
            DATA.addIP( event.getPlayer().getUniqueId().toString(), ipAddress );
        }

        //
        // Medium priority things below here
        //

        // ipban handling
        ArrayList<IPPunishment> allIPPunishments = DATA.getIPPunishment( event.getPlayer().getUniqueId().toString() );
        boolean hadActiveIPBan = false;
        for ( IPPunishment pun : allIPPunishments ) {
            if ( pun.getActive() && pun.getType().equalsIgnoreCase( "ipban" ) ) {
                hadActiveIPBan = true;
                break;
            }
        }

        ArrayList<IPPunishment> ipbanPunishments = DATA.getIPPunishment( event.getPlayer().getUniqueId().toString(), "ipban", true );
        if ( ipbanPunishments.size() >= 1 ) {
            event.setJoinMessage( null );

            long highestExpire = ipbanPunishments.get( 0 ).getExpirationDate();
            IPPunishment highest = ipbanPunishments.get( 0 );
            for ( int index = 1; index < ipbanPunishments.size(); index++ ) {
                if ( ipbanPunishments.get( index ).getExpirationDate() > highest.getExpirationDate() ) {
                    highest = ipbanPunishments.get( index );
                }
            }

            event.getPlayer().kickPlayer( ConfigUtils.replaceAllVariables( ConfigUtils.getColoredStrFromList( "ipban.attempt" ), highest ) );
            return;
        }

        else if ( hadActiveIPBan ) {
            Utils.logWarn( "hadActiveIPBan == true" ); // ! debug
            if ( ConfigUtils.checkListNotEmpty( "ipban.expire" ) ) {
                Utils.logWarn( "ConfigUtils.getColoredStrFromList( \"ipban.expire\" ) == " + ConfigUtils.getColoredStrFromList( "ipban.expire" ) ); // ! debug
                Utils.sendAnyMsg( event.getPlayer(), ConfigUtils.getColoredStrFromList( "ipban.expire" ) );
            }

            if ( ConfigUtils.checkListNotEmpty( "ipban.expire-staff" ) ) {
                Utils.logWarn( "ConfigUtils.getColoredStrFromList( \"ipban.expire-staff\" ) == " + ConfigUtils.getColoredStrFromList( "ipban.expire-staff" ) ); // ! debug
                String msg = ConfigUtils.getColoredStrFromList( "ipban.expire-staff" );
                for ( Player p : Bukkit.getOnlinePlayers() ) {
                    if ( VaultUtils.hasPerms( p, ConfigUtils.getStr( "general.staff-perm" ) ) ) {
                        Utils.logWarn( "sending ipban.expire-staff to " + p.getName() ); // ! debug
                        Utils.sendAnyMsg( p, msg.replace( "[TARGET]", event.getPlayer().getName() ) );
                    }
                }
            }
        }

        // ban handling
        ArrayList<Punishment> allPunishments = DATA.getPunishment( event.getPlayer().getUniqueId().toString() );
        boolean hadActiveBan = false;
        for ( Punishment pun : allPunishments ) {
            if ( pun.getActive() == true && pun.getType().equalsIgnoreCase( "ban" ) ) {
                hadActiveBan = true;
                break;
            }
        }

        ArrayList<Punishment> banPunishments = DATA.getPunishment( event.getPlayer().getUniqueId().toString(), "ban", true );
        if ( banPunishments.size() >= 1 ) {
            event.setJoinMessage( null );

            Punishment highest = banPunishments.get( 0 );
            for ( int index = 1; index < banPunishments.size(); index++ ) {
                if ( banPunishments.get( index ).getExpirationDate() > highest.getExpirationDate() ) {
                    highest = banPunishments.get( index );
                }
            }

            event.getPlayer().kickPlayer( ConfigUtils.replaceAllVariables( ConfigUtils.getColoredStrFromList( "ban.attempt" ), highest ) );
            return;
        }

        else if ( hadActiveBan ) {
            if ( ConfigUtils.checkListNotEmpty( "ban.expire" ) ) {
                Utils.sendAnyMsg( event.getPlayer(), ConfigUtils.getColoredStrFromList( "ban.expire" ) );
            }

            if ( ConfigUtils.checkListNotEmpty( "ban.expire-staff" ) ) {
                String msg = ConfigUtils.getColoredStrFromList( "ban.expire-staff" );
                for ( Player p : Bukkit.getOnlinePlayers() ) {
                    if ( VaultUtils.hasPerms( p, ConfigUtils.getStr( "general.staff-perm" ) ) ) {
                        Utils.sendAnyMsg( p, msg.replace( "[TARGET]", event.getPlayer().getName() ) );
                    }
                }
            }
        }

        //
        // Low priority things below here
        //

        // * IMPORTANT * Should be the last thing checked in this event
        // Checking if the player has been punished while they were offline
        // Gives them a notification about it if they were
        Bukkit.getScheduler().runTaskLater( Utils.getPlugin(), () -> {
            if ( DATA.searchNotifByUUID( event.getPlayer().getUniqueId().toString() ).size() > 0 ) {
                for ( int id : DATA.searchNotifByUUID( event.getPlayer().getUniqueId().toString() ) ) {
                    Punishment pun = DATA.getPunishment( id );
                    Utils.sendPunishmentMsg( event.getPlayer(), pun );
                    DATA.removeNotif( id );
                }
            }
        }, 60L );
    }
}
