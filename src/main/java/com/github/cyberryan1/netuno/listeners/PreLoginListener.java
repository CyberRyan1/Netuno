package com.github.cyberryan1.netuno.listeners;

import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.cybercore.spigot.utils.CyberLogUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberVaultUtils;
import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.api.models.ApiPlayer;
import com.github.cyberryan1.netuno.api.models.ApiPunishment;
import com.github.cyberryan1.netuno.models.NetunoPlayer;
import com.github.cyberryan1.netuno.models.NetunoPunishment;
import com.github.cyberryan1.netuno.models.PlayerIpsRecord;
import com.github.cyberryan1.netuno.models.libraries.PunishmentLibrary;
import com.github.cyberryan1.netuno.utils.TextComponentUtils;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netuno.utils.settings.SettingsVariableFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class PreLoginListener implements Listener {

    // need the priority to be low so that the player will
    //      be loaded into NetunoService first
    @EventHandler( priority = EventPriority.LOW )
    public void onPreLogin( AsyncPlayerPreLoginEvent event ) {
        final String IP_ADDRESS = event.getAddress().getHostAddress();

        // Firstly, we need to see if the IP the player is joining with
        //      is logged within the IP database
        final PlayerIpsRecord record = Netuno.ALT_SERVICE.getPlayerFromIpRecords( event.getUniqueId() )
                .orElse( null );

        // If the player has never joined the server before OR if they
        //      are joining with an IP address that isn't saved, we
        //      update their record
        if ( record == null || record.containsIp( IP_ADDRESS ) == false ) {
            Netuno.ALT_SERVICE.addNewIpAddress( event.getUniqueId(), IP_ADDRESS );
        }

        // Secondly, we need to load the player from NetunoService
        Netuno.SERVICE.getPlayer( event.getUniqueId() ).thenAccept( apiPlayer -> {
            final NetunoPlayer player = ( NetunoPlayer ) apiPlayer;
            final List<NetunoPunishment> allPunishments = player.getPunishments().stream()
                    .map( pun -> ( NetunoPunishment ) pun )
                    .collect( Collectors.toList() );

            // TODO I'm not sure if I am a fan of how this is done, may want to redo it
            // If the player has any alts with IP punishments and those
            //      punishments are not added to this player already,
            //      we add those punishments to this player
            List<UUID> altUuids = player.getAlts();
            List<NetunoPunishment> altIpPunishments = new ArrayList<>();
            for ( UUID uuid : altUuids ) {
                try {
                    final NetunoPlayer altPlayer = ( NetunoPlayer ) Netuno.SERVICE.getPlayer( uuid ).get();
                    if ( altPlayer == null ) throw new RuntimeException();

                    for ( ApiPunishment altPun : altPlayer.getPunishments() ) {
                        if ( altPun.getType().isIpPunishment() == false ) continue;
                        // Don't add duplicates to the list
                        if ( altIpPunishments.contains( ( NetunoPunishment ) altPun ) ) continue;
                        altIpPunishments.add( ( NetunoPunishment ) altPun );
                    }
                } catch ( InterruptedException | ExecutionException e ) {
                    throw new RuntimeException( e );
                }
            }

            // TODO ensure this is working properly
            for ( NetunoPunishment altIpPun : altIpPunishments ) {
                if ( allPunishments.contains( altIpPun ) == false ) {
                    NetunoPunishment newPun = ( NetunoPunishment ) altIpPun.copy();
                    newPun.setPlayer( player.getUuid() );

                    // Setting the reference ID for the new punishment to
                    //      the correct one
                    if ( altIpPun.isOriginalPunishment() ) newPun.setReferenceId( altIpPun.getId() );
                    else newPun.setReferenceId( altIpPun.getReferenceId() );

                    // Officially create this punishment
                    Netuno.PUNISHMENT_SERVICE.createPunishment( newPun );
                }
            }

            // If the player has any active IP ban or regular ban punishments,
            //      we disallow them from joining
            boolean hadActiveBanPunishment = player.getPunishments().stream()
                    .anyMatch( pun ->
                            pun.getType() == ApiPunishment.PunType.BAN
                                    && ( ( NetunoPunishment ) pun ).isActive_silent()
                    );
            boolean hadActiveIpbanPunishment = player.getPunishments().stream()
                    .anyMatch( pun ->
                            pun.getType() == ApiPunishment.PunType.IPBAN
                                    && ( ( NetunoPunishment ) pun ).isActive_silent()
                    );
            final List<NetunoPunishment> activePunishments = player.getActivePunishments().stream()
                    .map( pun -> ( NetunoPunishment ) pun )
                    .collect( Collectors.toList() );
            if ( activePunishments.stream().anyMatch( pun -> pun.getType() == ApiPunishment.PunType.BAN
                    || pun.getType() == ApiPunishment.PunType.IPBAN ) ) {
                final NetunoPunishment highestPunishment = PunishmentLibrary.getPunishmentWithHighestDurationRemaining( activePunishments );
                denyJoin( event, highestPunishment );
                return;
            }
            else {
                // if this is true, then the player's ban has just expired
                if ( hadActiveBanPunishment ) {
                    // sending a message to the player
                    Bukkit.getScheduler().runTaskLater( CyberCore.getPlugin(), () -> {
                        if ( apiPlayer.getPlayer().isOnline() == false ) return;
                        new SettingsVariableFactory( Settings.BAN_EXPIRE ).sendMsg( apiPlayer.getPlayer().getPlayer() );
                    }, 20L * 3 );

                    // sending a message to online staff
                    new SettingsVariableFactory( Settings.BAN_EXPIRE_STAFF )
                            .target( apiPlayer.getPlayer() )
                            .sendMsg( p -> CyberVaultUtils.hasPerms( p, Settings.STAFF_PERMISSION.string() ) );
                }

                // if this is true, then the player's IP ban has just expired
                if ( hadActiveIpbanPunishment ) {
                    // sending a message to the player
                    Bukkit.getScheduler().runTaskLater( CyberCore.getPlugin(), () -> {
                        if ( apiPlayer.getPlayer().isOnline() == false ) return;
                        new SettingsVariableFactory( Settings.IPBAN_EXPIRE ).sendMsg( apiPlayer.getPlayer().getPlayer() );
                    }, 10L );

                    // sending a message to online staff
                    new SettingsVariableFactory( Settings.IPBAN_EXPIRE_STAFF )
                            .target( apiPlayer.getPlayer() )
                            .sendMsg( p -> CyberVaultUtils.hasPerms( p, Settings.STAFF_PERMISSION.string() ) );
                }
            }

            // Send any punishments with notifications needing to be sent
            final List<NetunoPunishment> punishmentsNeedingNotifSent = player.getPunishments().stream()
                    .filter( pun -> pun.isNotifSent() == false )
                    .map( pun -> ( NetunoPunishment ) pun )
                    .collect( Collectors.toList() );
            // Delay the message by at least three seconds
            Bukkit.getScheduler().runTaskLaterAsynchronously( CyberCore.getPlugin(), () -> {
                // If the player logs off, don't send anything
                if ( player.getPlayer().isOnline() == false ) return;

                for ( NetunoPunishment pun : punishmentsNeedingNotifSent ) {
                    // Send the notification
                    pun.sendNotification();
                    // Update the notification in the database
                    Netuno.PUNISHMENT_SERVICE.updatePunishment( pun );
                }
            }, 20L * 3 );

            // Punished alt notifications
            // If the player joining has any punished alt accounts, then we
            //      send a notification to online staff
            if ( Settings.IPINFO_NOTIFS.bool() ) {
                handlePunishedAltNotification( apiPlayer );
            }
        } ).exceptionally( Netuno.FUTURE_ERROR_HANDLING ).join();
    }

    /**
     * Denies the player from joining
     *
     * @param event      The event
     * @param punishment The punishment
     */
    private void denyJoin( AsyncPlayerPreLoginEvent event, NetunoPunishment punishment ) {
        Settings settingToFill = PunishmentLibrary.getSettingForMessageType( punishment.getType(), PunishmentLibrary.MessageSetting.ATTEMPT );
        Component component = punishment.fillSettingMessage( settingToFill );
        event.disallow( AsyncPlayerPreLoginEvent.Result.KICK_BANNED, component );
    }

    /**
     * Handles punished alt notifications for the provided player
     * @param apiPlayer The player
     */
    private void handlePunishedAltNotification( ApiPlayer apiPlayer ) {
        // If the settings are null or empty, don't send anything
        if ( Settings.IPINFO_NOTIFS_MESSAGE.stringlist() == null
                || Settings.IPINFO_NOTIFS_MESSAGE.stringlist().length == 0 ) {
            CyberLogUtils.logWarn( "\"ipinfo.notifs\" in the config is enabled, yet you have no message set in \"ipinfo.notif-msg\"!" );
            return;
        }

        // Getting the player's alt accounts
        // TODO it seems that notifications are being sent even if they don't have any alts, needs further testing
        Netuno.ALT_SERVICE.getAlts( apiPlayer ).thenAccept( apiAlts -> {
            boolean sendNotif; // Whether we should send the notif or not

            // Getting all the active punishments of the accounts
            List<ApiPunishment> activeAltPunishments = new ArrayList<>();
            for ( ApiPlayer account : apiAlts ) {
                // We don't want to add any punishments from this player
                if ( account.getUuid().equals( apiPlayer.getUuid() ) ) continue;
                activeAltPunishments.addAll( account.getActivePunishments() );
            }

            // If there are zero active alt punishments, stop
            if ( activeAltPunishments.isEmpty() ) return;

            // If this setting is true, then we will always send the notification
            if ( Settings.IPINFO_NOTIF_IF_IPPUNISHED.bool() ) { sendNotif = true; }

            // Otherwise, we need to check the type of punishments the alt accounts have
            // If either (a) any of them are muted but they are not IP muted or (b) any
            //      of them are banned but they are not IP banned, then we will send
            //      the notification
            else {
                boolean anyMutes = false, anyBans = false, anyIpmutes = false, anyIpbans = false;
                for ( ApiPunishment pun : activeAltPunishments ) {
                    switch ( pun.getType() ) {
                        case MUTE -> anyMutes = true;
                        case IPMUTE -> anyIpmutes = true;
                        case BAN -> anyBans = true;
                        case IPBAN -> anyIpbans = true;
                    }
                }

                sendNotif = ( anyMutes && anyIpmutes == false ) || ( anyBans && anyIpbans == false );
            }

            // If we aren't sending the notification, then don't do anything else
            if ( sendNotif == false ) return;

            final String NOTIF_MSG = String.join( "\n", Settings.IPINFO_NOTIFS_MESSAGE.stringlist() );
            final OfflinePlayer target = apiPlayer.getPlayer();
            TextComponent message = TextComponentUtils.toTextComponent( NOTIF_MSG.replace( "[TARGET]", target.getName() ) );
            message = message.clickEvent( ClickEvent.clickEvent( ClickEvent.Action.RUN_COMMAND, "/ipinfo " + target.getName() ) );
            TextComponent hoverMsg = TextComponentUtils.toTextComponent(
                    new SettingsVariableFactory( Settings.IPINFO_NOTIF_HOVER_MESSAGE )
                            .target( target.getName() )
                            .getColoredMsg()
            );
            message = message.hoverEvent( HoverEvent.showText( hoverMsg ) );

            // Figuring out if a sound will be played for the alt alert or not
            boolean sendSound = false;
            if ( Settings.IPINFO_NOTIFS_SOUND_ENABLED.bool() ) {
                // If any of the accounts has an active punishment with a type
                //      that matches one of the triggers specified in the config,
                //      then we want to send the sound
                final String[] TRIGGERS = Settings.IPINFO_NOTIFS_SOUND_TRIGGERS.string().split( "," );
                for ( String trigger : TRIGGERS ) {
                    ApiPunishment.PunType type = ApiPunishment.PunType.valueOf( trigger.toUpperCase() );
                    if ( type == null ) {
                        CyberLogUtils.logWarn( "Invalid trigger \"" + trigger + "\" given in config path " + Settings.IPINFO_NOTIFS_SOUND_TRIGGERS.getPath() );
                        continue;
                    }


                    if ( activeAltPunishments.stream().anyMatch( pun -> pun.getType() == type ) ) {
                        sendSound = true;
                    }
                }
            }

            // Sending the notification and, if needed, sounds to online staff
            final TextComponent finalMsg = message;
            final boolean finalSendSound = sendSound;
            Bukkit.getScheduler().runTaskLater( CyberCore.getPlugin(), () -> {
                final String IPINFO_PERM = Settings.IPINFO_PERMISSION.string();
                final List<Player> sendingSounds = new ArrayList<>();
                for ( Player p : Bukkit.getOnlinePlayers() ) {
                    // Ensuring the player has permission to receive the notification
                    if ( CyberVaultUtils.hasPerms( p, IPINFO_PERM ) ) {
                        p.sendMessage( finalMsg );
                        if ( finalSendSound ) {
                            p.playSound( p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1 );
                            sendingSounds.add( p );
                        }
                    }
                }

                // Playing the rest of the sound, if needed
                if ( finalSendSound == false ) { return; }
                Bukkit.getScheduler().runTaskLater( CyberCore.getPlugin(), () -> {
                    for ( Player p : sendingSounds ) {
                        if ( p.isOnline() == false ) { continue; }
                        p.playSound( p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, .1f );
                    }
                }, 4 );
                Bukkit.getScheduler().runTaskLater( CyberCore.getPlugin(), () -> {
                    for ( Player p : sendingSounds ) {
                        if ( p.isOnline() == false ) { continue; }
                        p.playSound( p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 2 );
                    }
                }, 8 );
            }, 5L );
        } ).exceptionally( Netuno.FUTURE_ERROR_HANDLING );
    }
}