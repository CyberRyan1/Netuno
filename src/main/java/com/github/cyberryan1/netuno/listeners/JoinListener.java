package com.github.cyberryan1.netuno.listeners;

import com.github.cyberryan1.cybercore.CyberCore;
import com.github.cyberryan1.cybercore.utils.VaultUtils;
import com.github.cyberryan1.netuno.classes.IPPunishment;
import com.github.cyberryan1.netuno.classes.Punishment;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.database.Database;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JoinListener implements Listener {

    private final Database DATA = Utils.getDatabase();

    // Messages
    private final String IPBAN_ATTEMPT = Utils.getCombinedString( YMLUtils.getConfig().getColoredStrList( "ipban.attempt" ) );
    private final String IPBAN_EXPIRE = Utils.getCombinedString( YMLUtils.getConfig().getColoredStrList( "ipban.expire" ) );
    private final String IPBAN_EXPIRE_STAFF = Utils.getCombinedString( YMLUtils.getConfig().getColoredStrList( "ipban.expire-staff" ) );

    private final String BAN_ATTEMPT = Utils.getCombinedString( YMLUtils.getConfig().getColoredStrList( "ban.attempt" ) );
    private final String BAN_EXPIRE = Utils.getCombinedString( YMLUtils.getConfig().getColoredStrList( "ban.expire" ) );
    private final String BAN_EXPIRE_STAFF = Utils.getCombinedString( YMLUtils.getConfig().getColoredStrList( "ban.expire-staff" ) );

    private final boolean IPINFO_NOTIFS_ENABLED = YMLUtils.getConfig().getBool( "ipinfo.notifs" );
    private final String IPINFO_NOTIF_MSG = Utils.getCombinedString( YMLUtils.getConfig().getColoredStrList( "ipinfo.notif-msg" ) );
    private final String IPINFO_EXEMPT_PERM = YMLUtils.getConfig().getStr( "ipinfo.exempt-perm" );
    private final String IPINFO_NOTIF_HOVER = Utils.getCombinedString( YMLUtils.getConfig().getColoredStrList( "ipinfo.notif-hover" ) );
    private final String IPINFO_PERM = YMLUtils.getConfig().getStr( "ipinfo.perm" );

    @EventHandler
    public void onPlayerJoin( AsyncPlayerPreLoginEvent event ) {
        //
        // High priority things below here
        //

        // Log the player's IP address into the database
        String ipAddress = event.getAddress().getHostAddress();
        if ( DATA.playerHasIP( event.getUniqueId().toString(), ipAddress ) == false ) {
            DATA.addIP( event.getUniqueId().toString(), ipAddress );
        }

        //
        // Medium priority things below here
        //

        // ipban handling
        ArrayList<IPPunishment> allIPPunishments = DATA.getIPPunishment( event.getUniqueId().toString() );
        boolean hadActiveIPBan = false;
        for ( IPPunishment pun : allIPPunishments ) {
            if ( pun.getActive() && pun.getType().equalsIgnoreCase( "ipban" ) ) {
                hadActiveIPBan = true;
                break;
            }
        }

        ArrayList<OfflinePlayer> accountsIpbanned = DATA.getPunishedAltsByType( event.getUniqueId().toString(), "ipban" );
        if ( accountsIpbanned.size() >= 1 ) {

            List<IPPunishment> ipbanPunishments = new ArrayList<>();
            for ( OfflinePlayer account : accountsIpbanned ) {
                ipbanPunishments.addAll( DATA.getIPPunishment( account.getUniqueId().toString(), "ipban", true ) );
            }

            Collections.sort( ipbanPunishments );

            event.setLoginResult( AsyncPlayerPreLoginEvent.Result.KICK_OTHER );
            event.setKickMessage( Utils.replaceAllVariables( IPBAN_ATTEMPT, ipbanPunishments.get( 0 ) ) );
            return;
        }

        else if ( hadActiveIPBan ) {
            Bukkit.getScheduler().runTaskLaterAsynchronously( CyberCore.getPlugin(), () -> {
                final Player player = Bukkit.getPlayer( event.getUniqueId() );
                if ( player == null ) { return; }

                if ( IPBAN_EXPIRE != null ) {
                    Utils.sendAnyMsg( player, IPBAN_EXPIRE );
                }

                if ( IPBAN_EXPIRE_STAFF != null ) {
                    String msg = IPBAN_EXPIRE_STAFF.replace( "[TARGET]", player.getName() );
                    for ( Player p : Bukkit.getOnlinePlayers() ) {
                        if ( VaultUtils.hasPerms( p, YMLUtils.getConfig().getStr( "general.staff-perm" ) ) ) {
                            Utils.sendAnyMsg( p, IPBAN_EXPIRE_STAFF );
                        }
                    }
                }
            }, 30L );
        }

        // ban handling
        ArrayList<Punishment> allPunishments = DATA.getPunishment( event.getUniqueId().toString() );
        boolean hadActiveBan = false;
        for ( Punishment pun : allPunishments ) {
            if ( pun.getActive() == true && pun.getType().equalsIgnoreCase( "ban" ) ) {
                hadActiveBan = true;
                break;
            }
        }

        List<Punishment> banPunishments = DATA.getPunishment( event.getUniqueId().toString(), "ban", true );
        if ( banPunishments.size() >= 1 ) {
            event.setLoginResult( AsyncPlayerPreLoginEvent.Result.KICK_OTHER );

            Collections.sort( banPunishments );

            event.setKickMessage( Utils.replaceAllVariables( BAN_ATTEMPT, banPunishments.get( 0 ) ) );
            return;
        }

        else if ( hadActiveBan ) {
            Bukkit.getScheduler().runTaskLaterAsynchronously( CyberCore.getPlugin(), () -> {
                final Player player = Bukkit.getPlayer( event.getUniqueId() );
                if ( player == null ) { return; }

                if ( BAN_EXPIRE != null ) {
                    Utils.sendAnyMsg( player, BAN_EXPIRE );
                }

                if ( BAN_EXPIRE_STAFF != null ) {
                    String msg = BAN_EXPIRE_STAFF.replace( "[TARGET]", player.getName() );
                    for ( Player p : Bukkit.getOnlinePlayers() ) {
                        if ( VaultUtils.hasPerms( p, YMLUtils.getConfig().getStr( "general.staff-perm" ) ) ) {
                            Utils.sendAnyMsg( p, msg );
                        }
                    }
                }
            }, 30L );
        }

        //
        // Low priority things below here
        //

        // Checking if the player has any punished alts and alerting staff if they do
        if ( DATA.getPunishedAltList( event.getUniqueId().toString() ).size() >= 1 ) {
            Bukkit.getScheduler().runTaskLaterAsynchronously( CyberCore.getPlugin(), () -> {
                final Player player = Bukkit.getPlayer( event.getUniqueId() );
                if ( player == null ) { return; }

                if ( IPINFO_NOTIFS_ENABLED && VaultUtils.hasPerms( player, IPINFO_EXEMPT_PERM ) == false ) {
                    if ( IPINFO_NOTIF_MSG != null ) {
                        String coloredMsg = IPINFO_NOTIF_MSG.replace( "[TARGET]", player.getName() );

                        //? For some reason can send two blank lines, this is a "fix"
                        if ( coloredMsg.substring( coloredMsg.length() - 2 ).equals( "\n\n" ) ) {
                            coloredMsg = coloredMsg.substring( 0, coloredMsg.length() - 2 ) + "\n";
                        }

                        TextComponent message = new TextComponent( coloredMsg );
                        message.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/ipinfo " + player.getName() ) );

                        if ( IPINFO_NOTIF_HOVER.equals( "" ) == false ) {
                            String hoverColoredMsg = IPINFO_NOTIF_HOVER.replace( "[TARGET]", player.getName() );
                            ComponentBuilder hoverText = new ComponentBuilder( hoverColoredMsg );
                            message.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, hoverText.create() ) );
                        }

                        Bukkit.getScheduler().runTaskLater( CyberCore.getPlugin(), () -> {
                            for ( Player p : Bukkit.getOnlinePlayers() ) {
                                if ( VaultUtils.hasPerms( p, IPINFO_PERM ) ) {
                                    p.spigot().sendMessage( message );
                                }
                            }
                        }, 5L );
                    } else {
                        Utils.logWarn( "\"ipinfo.notifs\" in the config is enabled, yet you have no message set in \"ipinfo.notif-msg\"!" );
                    }
                }
            }, 20L );
        }

        // * IMPORTANT * Should be the last thing checked in this event
        // Checking if the player has been punished while they were offline
        // Gives them a notification about it if they were
        Bukkit.getScheduler().runTaskLaterAsynchronously( CyberCore.getPlugin(), () -> {
            final Player player = Bukkit.getPlayer( event.getUniqueId() );
            if ( player == null ) { return; }

            if ( DATA.searchNotifByUUID( event.getUniqueId().toString() ).size() > 0 ) {
                for ( int id : DATA.searchNotifByUUID( event.getUniqueId().toString() ) ) {
                    Punishment pun = DATA.getPunishment( id );
                    Utils.sendPunishmentMsg( player, pun );
                    DATA.removeNotif( id );
                }
            }
        }, 90L );
    }
}
