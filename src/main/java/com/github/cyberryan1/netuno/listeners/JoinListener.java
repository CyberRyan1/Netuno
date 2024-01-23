package com.github.cyberryan1.netuno.listeners;

import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.cybercore.spigot.utils.CyberLogUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberVaultUtils;
import com.github.cyberryan1.netuno.apimplement.ApiNetuno;
import com.github.cyberryan1.netuno.apimplement.models.players.NetunoPlayer;
import com.github.cyberryan1.netuno.apimplement.models.players.NetunoPlayerCache;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;
import com.github.cyberryan1.netunoapi.models.punishments.PunishmentType;
import com.github.cyberryan1.netunoapi.utils.PunishmentUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JoinListener implements Listener {

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
        ApiNetuno.getInstance().getAltCache().initEntry( event.getUniqueId(), event.getAddress().getHostAddress() );
        // Load the player into the cache
        final NetunoPlayer nPlayer = NetunoPlayerCache.getOrLoad( event.getUniqueId().toString() );

        //
        // Medium priority things below here
        //

        // IP Ban and Ban Handling
        final List<NPunishment> all = nPlayer.getPunishments().stream()
                .filter( pun -> pun.getPunishmentType() == PunishmentType.IPBAN || pun.getPunishmentType() == PunishmentType.BAN )
                .collect( Collectors.toList() );
        final List<NPunishment> active = PunishmentUtils.getActive( all );

        if ( active.size() > 0 ) {
            event.setLoginResult( AsyncPlayerPreLoginEvent.Result.KICK_OTHER );

            final NPunishment highestIpban = PunishmentUtils.getHighestActive( active, PunishmentType.IPBAN );
            final NPunishment highestBan = PunishmentUtils.getHighestActive( active, PunishmentType.BAN );

            if ( highestIpban != null ) { event.setKickMessage( Utils.replaceAllVariables( IPBAN_ATTEMPT, highestIpban ) ); }
            else { event.setKickMessage( Utils.replaceAllVariables( BAN_ATTEMPT, highestBan ) ); }
            return;
        }

        // Getting all punishments from the alt group
        final List<NPunishment> activeAltPunishments = new ArrayList<>();
        for ( UUID altUuid : nPlayer.getAltAccounts() ) {
            final NetunoPlayer altPlayer = NetunoPlayerCache.getOrLoad( altUuid.toString() );
            activeAltPunishments.addAll( altPlayer.getPunishments().stream()
                    .filter( pun -> pun.isActive() && pun.getReferencePunId() < 0 )
                    .collect( Collectors.toList() ) );
        }

        // Checking if any of the other alts in the alt group are IP banned and enforcing the IP ban on this alt if so
        final List<NPunishment> activeAltIpbans = activeAltPunishments.stream()
                .filter( pun -> pun.getPunishmentType() == PunishmentType.IPBAN )
                .collect( Collectors.toList() );
        if ( activeAltIpbans.size() > 0 ) {
            event.setLoginResult( AsyncPlayerPreLoginEvent.Result.KICK_OTHER );
            final NPunishment highestIpban = PunishmentUtils.getHighestActive( activeAltIpbans, PunishmentType.IPBAN );

            final NPunishment newPun = highestIpban.copy();
            newPun.setId( -1 );
            newPun.setReferencePunId( highestIpban.getId() );
            ApiNetuno.getData().getPun().addPunishment( newPun );

            event.setKickMessage( Utils.replaceAllVariables( IPBAN_ATTEMPT, newPun ) );
            return;
        }

        // Checking if any of the other alts in the alt group are IP muted and enforcing the IP mute on this alt if so
        final List<NPunishment> activeIpmutes = nPlayer.getPunishments().stream()
                .filter( pun -> pun.getPunishmentType() == PunishmentType.IPMUTE && pun.isActive() )
                .collect( Collectors.toList() );
        if ( activeIpmutes.size() == 0 ) {
            final List<NPunishment> activeAltIpmutes = activeAltPunishments.stream()
                    .filter( pun -> pun.getPunishmentType() == PunishmentType.IPMUTE )
                    .collect( Collectors.toList() );
            if ( activeAltIpmutes.size() > 0 ) {
                final NPunishment highestIpmute = PunishmentUtils.getHighestActive( activeAltIpmutes, PunishmentType.IPMUTE );
                final NPunishment newPun = highestIpmute.copy();
                newPun.setId( -1 );
                newPun.setReferencePunId( highestIpmute.getId() );
                ApiNetuno.getData().getPun().addPunishment( newPun );
            }
        }

        // Sending expiration notices for any previously active IP Bans and regular bans
        final List<NPunishment> dataActiveIpbans = nPlayer.getPunishments().stream()
                .filter( pun -> pun.dataIsActive() && pun.getPunishmentType() == PunishmentType.IPBAN )
                .collect( Collectors.toList() );
        final List<NPunishment> dataActiveBans = nPlayer.getPunishments().stream()
                .filter( pun -> pun.dataIsActive() && pun.getPunishmentType() == PunishmentType.BAN )
                .collect( Collectors.toList() );

        if ( dataActiveIpbans.size() > 0 ) {
            Bukkit.getScheduler().runTaskLaterAsynchronously( CyberCore.getPlugin(), () -> {
                if ( nPlayer.getPlayer().isOnline() == false ) { return; }

                if ( IPBAN_EXPIRE != null ) {
                    Utils.sendAnyMsg( nPlayer.getPlayer().getPlayer(), IPBAN_EXPIRE );
                }

                if ( IPBAN_EXPIRE_STAFF != null ) {
                    String msg = IPBAN_EXPIRE_STAFF.replace( "[TARGET]", nPlayer.getPlayer().getName() );
                    for ( Player p : Bukkit.getOnlinePlayers() ) {
                        if ( CyberVaultUtils.hasPerms( p, Settings.STAFF_PERMISSION.string() ) ) {
                            Utils.sendAnyMsg( p, msg );
                        }
                    }
                }

                for ( NPunishment pun : dataActiveIpbans ) {
                    pun.setActive( false );
                    ApiNetuno.getData().getPun().updatePunishment( pun );
                }
            }, 30L );
        }

        if ( dataActiveBans.size() > 0 ) {
            Bukkit.getScheduler().runTaskLaterAsynchronously( CyberCore.getPlugin(), () -> {
                if ( nPlayer.getPlayer().isOnline() == false ) { return; }

                if ( BAN_EXPIRE != null ) {
                    Utils.sendAnyMsg( nPlayer.getPlayer().getPlayer(), BAN_EXPIRE );
                }

                if ( BAN_EXPIRE_STAFF != null ) {
                    String msg = BAN_EXPIRE_STAFF.replace( "[TARGET]", nPlayer.getPlayer().getName() );
                    for ( Player p : Bukkit.getOnlinePlayers() ) {
                        if ( CyberVaultUtils.hasPerms( p, Settings.STAFF_PERMISSION.string() ) ) {
                            Utils.sendAnyMsg( p, msg );
                        }
                    }
                }

                for ( NPunishment pun : dataActiveBans ) {
                    pun.setActive( false );
                    ApiNetuno.getData().getPun().updatePunishment( pun );
                }
            }, 30L );
        }

        //
        // Low priority things below here
        //

        // Checking if the player has any punished alts and alerting staff if they do
        if ( activeAltPunishments.size() >= 1 && IPINFO_NOTIFS_ENABLED ) {
            Bukkit.getScheduler().runTaskLaterAsynchronously( CyberCore.getPlugin(), () -> {
                final Player player = Bukkit.getPlayer( event.getUniqueId() );
                if ( player == null ) { return; }

                if ( CyberVaultUtils.hasPerms( player, IPINFO_EXEMPT_PERM ) == false ) {
                    if ( IPINFO_NOTIF_MSG != null ) {
                        String coloredMsg = IPINFO_NOTIF_MSG.replace( "[TARGET]", player.getName() );

                        // ? For some reason can send two blank lines, this is a "fix"
                        if ( coloredMsg.substring( coloredMsg.length() - 2 ).equals( "\n\n" ) ) {
                            coloredMsg = coloredMsg.substring( 0, coloredMsg.length() - 2 ) + "\n";
                        }

                        TextComponent message = new TextComponent( coloredMsg );
                        message.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/ipinfo " + player.getName() ) );

                        if ( IPINFO_NOTIF_HOVER.isBlank() == false ) {
                            String hoverColoredMsg = IPINFO_NOTIF_HOVER.replace( "[TARGET]", player.getName() );
                            ComponentBuilder hoverText = new ComponentBuilder( hoverColoredMsg );
                            message.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, hoverText.create() ) );
                        }

                        Bukkit.getScheduler().runTaskLater( CyberCore.getPlugin(), () -> {
                            for ( Player p : Bukkit.getOnlinePlayers() ) {
                                if ( CyberVaultUtils.hasPerms( p, IPINFO_PERM ) ) {
                                    p.spigot().sendMessage( message );
                                }
                            }
                        }, 5L );
                    } else {
                        CyberLogUtils.logWarn( "\"ipinfo.notifs\" in the config is enabled, yet you have no message set in \"ipinfo.notif-msg\"!" );
                    }
                }
            }, 20L );
        }

        // * IMPORTANT * Should be the last thing checked in this event
        // Checking if the player has been warned while they were offline
        // Gives them a notification about it if they were
        final List<NPunishment> playerWarnNotifs = nPlayer.getPunishments().stream()
                .filter( pun -> pun.getPunishmentType() == PunishmentType.WARN && pun.needsNotifSent() )
                .collect( Collectors.toList() );
        if ( playerWarnNotifs.size() >= 1 ) {
            Bukkit.getScheduler().runTaskLaterAsynchronously( CyberCore.getPlugin(), () -> {
                if ( nPlayer.getPlayer().isOnline() == false ) { return; }
                final Player player = nPlayer.getPlayer().getPlayer();

                for ( NPunishment pun : playerWarnNotifs ) {
                    Utils.sendPunishmentMsg( player, pun );
                    pun.setNeedsNotifSent( false );
                    ApiNetuno.getData().getPun().updatePunishment( pun );
                }
            }, 90L );
        }
    }
}
