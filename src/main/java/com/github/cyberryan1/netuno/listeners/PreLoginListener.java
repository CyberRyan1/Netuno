package com.github.cyberryan1.netuno.listeners;

import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.api.models.ApiPunishment;
import com.github.cyberryan1.netuno.models.NPlayer;
import com.github.cyberryan1.netuno.models.PlayerIpsRecord;
import com.github.cyberryan1.netuno.models.Punishment;
import com.github.cyberryan1.netuno.models.libraries.PunishmentLibrary;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
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
            final NPlayer player = ( NPlayer ) apiPlayer;
            final List<Punishment> allPunishments = player.getPunishments().stream()
                    .map( pun -> ( Punishment ) pun )
                    .collect( Collectors.toList() );

            // If the player has any alts with IP punishments and those
            //      punishments are not added to this player already,
            //      we add those punishments to this player
            List<UUID> altUuids = player.getAlts();
            List<Punishment> altIpPunishments = new ArrayList<>();
            for ( UUID uuid : altUuids ) {
                try {
                    final NPlayer altPlayer = ( NPlayer ) Netuno.SERVICE.getPlayer( uuid ).get();
                    if ( altPlayer == null ) throw new RuntimeException();

                    for ( ApiPunishment altPun : altPlayer.getPunishments() ) {
                        if ( altPun.getType().isIpPunishment() == false ) continue;
                        // Don't add duplicates to the list
                        if ( altIpPunishments.contains( ( Punishment ) altPun ) ) continue;
                        altIpPunishments.add( ( Punishment ) altPun );
                    }
                } catch ( InterruptedException | ExecutionException e ) {
                    throw new RuntimeException( e );
                }
            }

            for ( Punishment altIpPun : altIpPunishments ) {
                if ( allPunishments.contains( altIpPun ) == false ) {
                    Punishment newPun = ( Punishment ) altIpPun.copy();
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
            final List<Punishment> activePunishments = player.getActivePunishments().stream()
                    .map( pun -> ( Punishment ) pun )
                    .collect( Collectors.toList() );
            if ( activePunishments.stream().anyMatch( pun -> pun.getType() == ApiPunishment.PunType.BAN
                    || pun.getType() == ApiPunishment.PunType.IPBAN ) ) {
                final Punishment highestPunishment = PunishmentLibrary.getPunishmentWithHighestDurationRemaining( activePunishments );
                denyJoin( event, highestPunishment );
                return;
            }

            // Send any punishments with notifications needing to be sent
            final List<Punishment> punishmentsNeedingNotifSent = player.getPunishments().stream()
                    .filter( pun -> pun.isNotifSent() == false )
                    .map( pun -> ( Punishment ) pun )
                    .collect( Collectors.toList() );
            // Delay the message by at least three seconds
            Bukkit.getScheduler().runTaskLaterAsynchronously( CyberCore.getPlugin(), () -> {
                // If the player logs off, don't send anything
                if ( player.getPlayer().isOnline() == false ) return;

                for ( Punishment pun : punishmentsNeedingNotifSent ) {
                    // Send the notification
                    pun.sendNotification();
                    // Update the notification in the database
                    Netuno.PUNISHMENT_SERVICE.updatePunishment( pun );
                }
            }, 20L * 3 );

        } );
    }

    /**
     * Denies the player from joining
     *
     * @param event      The event
     * @param punishment The punishment
     */
    private void denyJoin( AsyncPlayerPreLoginEvent event, Punishment punishment ) {
        Settings settingToFill = PunishmentLibrary.getSettingForMessageType( punishment.getType(), PunishmentLibrary.MessageSetting.MESSAGE );
        Component component = punishment.fillSettingMessage( settingToFill );
        event.disallow( AsyncPlayerPreLoginEvent.Result.KICK_BANNED, component );
    }
}