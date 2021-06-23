package com.github.cyberryan1.netuno.listeners;

import com.github.cyberryan1.netuno.classes.IPPunishment;
import com.github.cyberryan1.netuno.classes.Punishment;
import com.github.cyberryan1.netuno.utils.*;
import com.github.cyberryan1.netuno.utils.database.Database;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.md_5.bungee.api.chat.TextComponent;

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
            if ( ConfigUtils.checkListNotEmpty( "ipban.expire" ) ) {
                Utils.sendAnyMsg( event.getPlayer(), ConfigUtils.getColoredStrFromList( "ipban.expire" ) );
            }

            if ( ConfigUtils.checkListNotEmpty( "ipban.expire-staff" ) ) {
                String msg = ConfigUtils.getColoredStrFromList( "ipban.expire-staff" );
                for ( Player p : Bukkit.getOnlinePlayers() ) {
                    if ( VaultUtils.hasPerms( p, ConfigUtils.getStr( "general.staff-perm" ) ) ) {
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

        // Checking if the player has any punished alts and alerting staff if they do
        if ( DATA.getPunishedAltList( event.getPlayer().getUniqueId().toString() ).size() >= 1 ) {
            if ( ConfigUtils.getBool( "ipinfo.notifs" )
                    && VaultUtils.hasPerms( event.getPlayer(), ConfigUtils.getStr( "ipinfo.exempt-perm" ) ) == false ) {
                if ( ConfigUtils.checkListNotEmpty( "ipinfo.notif-msg" ) ) {
                    String coloredMsg = ConfigUtils.getColoredStrFromList( "ipinfo.notif-msg" );
                    coloredMsg = coloredMsg.replace( "[TARGET]", event.getPlayer().getName() );

                    //? For some reason can send two blank lines, this is a "fix"
                    if ( coloredMsg.substring( coloredMsg.length() - 2 ).equals( "\n\n" ) ) {
                        coloredMsg = coloredMsg.substring( 0, coloredMsg.length() - 2 ) + "\n";
                    }

                    TextComponent message = new TextComponent( coloredMsg );
                    message.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/ipinfo " + event.getPlayer().getName() ) );

                    if ( ConfigUtils.getStr( "ipinfo.notif-hover" ).equals( "" ) == false ) {
                        String hoverColoredMsg = ConfigUtils.getColoredStr( "ipinfo.notif-hover" ).replace( "[TARGET]", event.getPlayer().getName() );
                        ComponentBuilder hoverText = new ComponentBuilder( hoverColoredMsg );
                        message.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, hoverText.create() ) );
                    }

                    Bukkit.getScheduler().runTaskLater( Utils.getPlugin(), () -> {
                        for ( Player player : Bukkit.getOnlinePlayers() ) {
                            if ( VaultUtils.hasPerms( player, ConfigUtils.getStr( "ipinfo.perm" ) ) ) {
                                player.spigot().sendMessage( message );
                            }
                        }
                    }, 5L );
                }

                else {
                    Utils.logWarn( "\"ipinfo.notifs\" in the config is enabled, yet you have no message set in \"ipinfo.notif-msg\"!" );
                }
            }
        }

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
