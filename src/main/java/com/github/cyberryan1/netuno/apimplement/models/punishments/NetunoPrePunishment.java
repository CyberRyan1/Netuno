package com.github.cyberryan1.netuno.apimplement.models.punishments;

import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.cybercore.spigot.utils.CyberLogUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberMsgUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberVaultUtils;
import com.github.cyberryan1.netuno.apimplement.ApiNetuno;
import com.github.cyberryan1.netuno.apimplement.models.players.NetunoPlayer;
import com.github.cyberryan1.netuno.apimplement.models.players.NetunoPlayerCache;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netuno.utils.settings.SoundSettingEntry;
import com.github.cyberryan1.netunoapi.events.punish.NetunoPrePunishEvent;
import com.github.cyberryan1.netunoapi.models.alts.UuidIpRecord;
import com.github.cyberryan1.netunoapi.models.punishments.NPrePunishment;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;
import com.github.cyberryan1.netunoapi.models.punishments.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class NetunoPrePunishment implements NPrePunishment {

    private final NPunishment pun;

    public NetunoPrePunishment( NPunishment pun ) {
        this.pun = pun;
    }

    public NetunoPrePunishment() {
        this.pun = new NPunishment();
    }

    public NPunishment getPunishment() {
        return this.pun;
    }

    public void executePunishment() {
        Bukkit.getScheduler().runTaskAsynchronously( CyberCore.getPlugin(), () -> {
            // Checking if the punishment is silent
            boolean silent = pun.getReason().contains( "-s" );
            // Removing all -s from the reason
            pun.setReason( pun.getReason().replace( "-s", "" ) );

            // Set if the player needs a notification or not
            // Notifications are only sent when an offline player is warned
            pun.setNeedsNotifSent( pun.getPunishmentType() == PunishmentType.WARN && pun.getPlayer().isOnline() == false );

            // Setting the reference ID if the punishment is an IP punishment
            if ( pun.getPunishmentType().isIpPunishment() ) {
                pun.setReferencePunId( -1 );
            }

            // Dispatching the event and seeing if it was cancelled
            NetunoPrePunishEvent event = new NetunoPrePunishEvent( pun, silent );
            ApiNetuno.getInstance().getEventDispatcher().dispatch( event );
            if ( event.isCancelled() ) { return; }

            // Executing the punishment in the database
            ApiNetuno.getData().getNetunoPuns().addPunishment( pun );

            // Iterating through all the player's alts and adding reference punishments to them.
            //      Also confirming the player's alt group is loaded in the cache.
            //      Only if the punishment is an IP punishment.
            if ( pun.getPunishmentType().isIpPunishment() ) {
                final int recentlyAddedId = ApiNetuno.getData().getNetunoPuns().getRecentlyInsertedId();
                if ( recentlyAddedId == -1 ) {
                    CyberLogUtils.logError( "An error occurred while trying to get the most recently inserted ID, "
                            + "so the reference punishment could not be added to the player's alts." );
                    return;
                }

                // Getting an IP to query with
                String ip = "";
                if ( pun.getPlayer().isOnline() ) { ip = pun.getPlayer().getPlayer().getAddress().getAddress().getHostAddress(); }
                else {
                    List<UuidIpRecord> entries = new ArrayList<>( ApiNetuno.getData().getIpHistoryDatabase().queryByUuid( UUID.fromString( pun.getPlayerUuid() ) ) );
                    ip = entries.get( 0 ).getIp();
                }

                // Querying for alts using the above IP
                for ( UUID altUuid : ApiNetuno.getInstance().getAltInfoLoader().queryAccounts( ip ) ) {
                    if ( altUuid.equals( pun.getPlayer().getUniqueId() ) == false ) {
                        final OfflinePlayer alt = Bukkit.getOfflinePlayer( altUuid );
                        NPunishment altPun = pun.copy();
                        altPun.setId( -1 );
                        altPun.setPlayer( alt );
                        altPun.setReferencePunId( recentlyAddedId );
                        NetunoPrePunishment altPrePun = new NetunoPrePunishment( altPun );
                        altPrePun.executeAsReferencePunishment();
                    }
                }
            }

            // For unpunishments (unmutes, unbans, etc.), need to go through all their previous punishments of
            //      the unpunishment type and set them as unactive.
            if ( pun.getPunishmentType().hasNoReason() ) {
                final PunishmentType unpunType = switch ( pun.getPunishmentType() ) {
                    case UNMUTE -> PunishmentType.MUTE;
                    case UNBAN -> PunishmentType.BAN;
                    case UNIPMUTE -> PunishmentType.IPMUTE;
                    case UNIPBAN -> PunishmentType.IPBAN;
                    default -> null;
                };

                if ( unpunType == null ) {
                    CyberLogUtils.logError( "An error occurred while trying to get the unpunishment type, "
                            + "so the previous punishments could not be set as unactive." );
                    return;
                }

                // If the punishment type is an un IP punishment, need to go through all the player's alts
                //      Otherwise just need to go through the player
                if ( pun.getPunishmentType().isIpPunishment() == false ) {
                    final NetunoPlayer nPlayer = NetunoPlayerCache.forceLoad( pun.getPlayer() );
                    final List<NPunishment> puns = nPlayer.getPunishments().stream()
                            .filter( playerPun -> playerPun.getPunishmentType() == unpunType && playerPun.isActive() )
                            .collect( Collectors.toList() );
                    nPlayer.getPunishments().removeAll( puns );

                    puns.forEach( playerPun -> {
                        playerPun.setActive( false );
                        ApiNetuno.getData().getNetunoPuns().updatePunishment( playerPun );
                    } );
                }

                else {
                    // Getting an IP to query with
                    String ip = "";
                    if ( pun.getPlayer().isOnline() ) { ip = pun.getPlayer().getPlayer().getAddress().getAddress().getHostAddress(); }
                    else {
                        List<UuidIpRecord> entries = new ArrayList<>( ApiNetuno.getData().getIpHistoryDatabase().queryByUuid( UUID.fromString( pun.getPlayerUuid() ) ) );
                        ip = entries.get( 0 ).getIp();
                    }

                    for ( UUID altUuid : ApiNetuno.getInstance().getAltInfoLoader().queryAccounts( ip ) ) {
                        final OfflinePlayer alt = Bukkit.getOfflinePlayer( altUuid );
                        final NetunoPlayer nAlt = NetunoPlayerCache.forceLoad( alt );
                        final List<NPunishment> puns = nAlt.getPunishments().stream()
                                .filter( playerPun -> playerPun.getPunishmentType() == unpunType && playerPun.isActive() )
                                .collect( Collectors.toList() );
                        nAlt.getPunishments().removeAll( puns );

                        puns.forEach( playerPun -> {
                            playerPun.setActive( false );
                            ApiNetuno.getData().getNetunoPuns().updatePunishment( playerPun );
                        } );
                    }
                }
            }

            Bukkit.getScheduler().runTask( CyberCore.getPlugin(), () -> {
                // Kicking the player from the server if the punishment is a kick, ban, or ipban and the player is online
                attemptKickPlayer();
                // Sending the player a notification if the punishment is a warn, mute, unmute, ipmute, or unipmute and the player is online
                attemptSendMessage();
                // Sending the broadcasts
                doBroadcasts( silent );
            } );
        } );
    }

    private void executeAsReferencePunishment() {
        // Executing the punishment in the database
        ApiNetuno.getData().getNetunoPuns().addPunishment( pun );

        Bukkit.getScheduler().runTask( CyberCore.getPlugin(), () -> {
            // Kicking the player from the server if the punishment is a kick, ban, or ipban and the player is online
            attemptKickPlayer();
            // Sending the player a notification if the punishment is a warn, mute, unmute, ipmute, or unipmute and the player is online
            attemptSendMessage();
        } );
    }

    private void attemptKickPlayer() {
        if ( pun.getPlayer().isOnline() == false ) { return; }
        String lines = switch ( pun.getPunishmentType() ) {
            case KICK -> Utils.getCombinedString( Settings.KICK_KICKED_LINES.coloredStringlist() );
            case BAN -> Utils.getCombinedString( Settings.BAN_MESSAGE.coloredStringlist() );
            case IPBAN -> Utils.getCombinedString( Settings.IPBAN_MESSAGE.coloredStringlist() );
            default -> null;
        };

        if ( lines != null && lines.replace( "\n", "" ).isBlank() == false ) {
            lines = Utils.replaceAllVariables( lines, pun );
            if ( Bukkit.isPrimaryThread() ) {
                pun.getPlayer().getPlayer().kickPlayer( lines );
            }
            else {
                final String linesMsg = lines;
                Bukkit.getScheduler().runTask( CyberCore.getPlugin(), () -> pun.getPlayer().getPlayer().kickPlayer( linesMsg ) );
            }
        }
    }

    private void attemptSendMessage() {
        if ( pun.getPlayer().isOnline() == false ) { return; }
        String lines[] = switch ( pun.getPunishmentType() ) {
            case WARN -> Settings.WARN_MESSAGE.coloredStringlist();
            case MUTE -> Settings.MUTE_MESSAGE.coloredStringlist();
            case UNMUTE -> Settings.UNMUTE_MESSAGE.coloredStringlist();
            case IPMUTE -> Settings.IPMUTE_MESSAGE.coloredStringlist();
            case UNIPMUTE -> Settings.UNIPMUTE_MESSAGE.coloredStringlist();
            default -> null;
        };

        if ( lines == null || lines.length == 0 ) { return; }
        else {
            boolean empty = true;
            for ( String line : lines ) {
                if ( line.replace( "\n", "" ).isBlank() == false ) {
                    empty = false;
                    break;
                }
            }

            if ( empty ) { return; }
        }

        List<String> message = Arrays.asList( Utils.replaceAllVariables( lines, pun ) );
        CyberMsgUtils.sendMsg( pun.getPlayer().getPlayer(), message );
    }

    private void doBroadcasts( boolean silent ) {
        // Getting the public and staff announcement messages from the settings
        final String publicBroadcastLines[] = switch ( pun.getPunishmentType() ) {
            case WARN -> Settings.WARN_BROADCAST.coloredStringlist();
            case KICK -> Settings.KICK_BROADCAST.coloredStringlist();
            case MUTE -> Settings.MUTE_BROADCAST.coloredStringlist();
            case UNMUTE -> Settings.UNMUTE_BROADCAST.coloredStringlist();
            case BAN -> Settings.BAN_BROADCAST.coloredStringlist();
            case UNBAN -> Settings.UNBAN_BROADCAST.coloredStringlist();
            case IPMUTE -> Settings.IPMUTE_BROADCAST.coloredStringlist();
            case UNIPMUTE -> Settings.UNIPMUTE_BROADCAST.coloredStringlist();
            case IPBAN -> Settings.IPBAN_BROADCAST.coloredStringlist();
            case UNIPBAN -> Settings.UNIPBAN_BROADCAST.coloredStringlist();
        };
        final String staffBroadcastLines[] = switch ( pun.getPunishmentType() ) {
            case WARN -> Settings.WARN_STAFF_BROADCAST.coloredStringlist();
            case KICK -> Settings.KICK_STAFF_BROADCAST.coloredStringlist();
            case MUTE -> Settings.MUTE_STAFF_BROADCAST.coloredStringlist();
            case UNMUTE -> Settings.UNMUTE_STAFF_BROADCAST.coloredStringlist();
            case BAN -> Settings.BAN_STAFF_BROADCAST.coloredStringlist();
            case UNBAN -> Settings.UNBAN_STAFF_BROADCAST.coloredStringlist();
            case IPMUTE -> Settings.IPMUTE_STAFF_BROADCAST.coloredStringlist();
            case UNIPMUTE -> Settings.UNIPMUTE_STAFF_BROADCAST.coloredStringlist();
            case IPBAN -> Settings.IPBAN_STAFF_BROADCAST.coloredStringlist();
            case UNIPBAN -> Settings.UNIPBAN_STAFF_BROADCAST.coloredStringlist();
        };

        boolean publicBroadcastIsEmpty = false;
        if ( publicBroadcastLines.length == 0 ) { publicBroadcastIsEmpty = true; }
        else {
            for ( String line : publicBroadcastLines ) {
                if ( line.replace( "\n", "" ).isBlank() == false ) {
                    publicBroadcastIsEmpty = false;
                    break;
                }
                publicBroadcastIsEmpty = true;
            }
        }

        // Send the public punishment announcement to all online, non-staff players who are not the target.
        //      No public broadcast will be sent if the punishment is silent or if the publicBroadcastLines
        //      is null or empty. Will send the publicBroadcastLines to staff if the staffBroadcastLines
        //      variable is either null or has no contents.
        boolean staffBroadcastNoExist = ( staffBroadcastLines == null ) || ( staffBroadcastLines.length == 0 );
        if ( silent == false && publicBroadcastLines != null && publicBroadcastIsEmpty == false ) {
            final List<String> publicBroadcast = Arrays.asList( Utils.replaceAllVariables( publicBroadcastLines, pun ) );
            CyberMsgUtils.broadcast( publicBroadcast, player -> player.getUniqueId().equals( pun.getPlayer().getUniqueId() ) == false
                    && ( staffBroadcastNoExist || CyberVaultUtils.hasPerms( player, Settings.STAFF_PERMISSION.string() ) == false ) );
        }

        // Send the staff punishment announcement to all online players, even if the staff is the target.
        //      No staff broadcast will be sent if the staffBroadcastNoExist variable is true. If the
        //      silent variable is true, the staff announcement needs to be sent with the silent prefix
        //      and the broadcast should only be sent to staff who have the view silent punishments permission.
        if ( staffBroadcastNoExist == false ) {
            // If the punishment is silent, this adds the silent prefix to all non-empty lines
            for ( int index = 0; index < staffBroadcastLines.length; index++ ) {
                if ( silent && staffBroadcastLines[index].replace( "\n", "" ).isBlank() == false ) {
                    staffBroadcastLines[index] = Settings.SILENT_PREFIX.coloredString() + " " + staffBroadcastLines[index];
                }
            }

            final List<String> staffBroadcast = Arrays.asList( Utils.replaceAllVariables( staffBroadcastLines, pun ) );
            // Getting the permission needed to see the broadcast
            final String permission = silent ? Settings.SILENT_PERMISSION.string() : Settings.STAFF_PERMISSION.string();
            CyberMsgUtils.broadcast( staffBroadcast, player -> CyberVaultUtils.hasPerms( player, permission ) );
        }

        // Playing sounds for the players, target, and staff.
        SoundSettingEntry SOUND_TARGET = switch ( pun.getPunishmentType() ) {
            case WARN -> Settings.SOUND_PUNISHMENT_WARN_TARGET.sound();
            case MUTE -> Settings.SOUND_PUNISHMENT_MUTE_TARGET.sound();
            case UNMUTE -> Settings.SOUND_PUNISHMENT_UNMUTE_TARGET.sound();
            case IPMUTE -> Settings.SOUND_PUNISHMENT_IPMUTE_TARGET.sound();
            case UNIPMUTE -> Settings.SOUND_PUNISHMENT_UNIPMUTE_TARGET.sound();
            default -> null;
        };
        SoundSettingEntry SOUND_STAFF = switch ( pun.getPunishmentType() ) {
            case WARN -> Settings.SOUND_PUNISHMENT_WARN_STAFF.sound();
            case KICK -> Settings.SOUND_PUNISHMENT_KICK_STAFF.sound();
            case MUTE -> Settings.SOUND_PUNISHMENT_MUTE_STAFF.sound();
            case UNMUTE -> Settings.SOUND_PUNISHMENT_UNMUTE_STAFF.sound();
            case BAN -> Settings.SOUND_PUNISHMENT_BAN_STAFF.sound();
            case UNBAN -> Settings.SOUND_PUNISHMENT_UNBAN_STAFF.sound();
            case IPMUTE -> Settings.SOUND_PUNISHMENT_IPMUTE_STAFF.sound();
            case UNIPMUTE -> Settings.SOUND_PUNISHMENT_UNIPMUTE_STAFF.sound();
            case IPBAN -> Settings.SOUND_PUNISHMENT_IPBAN_STAFF.sound();
            case UNIPBAN -> Settings.SOUND_PUNISHMENT_UNIPBAN_STAFF.sound();
        };
        SoundSettingEntry SOUND_GLOBAL = switch ( pun.getPunishmentType() ) {
            case WARN -> Settings.SOUND_PUNISHMENT_WARN_GLOBAL.sound();
            case KICK -> Settings.SOUND_PUNISHMENT_KICK_GLOBAL.sound();
            case MUTE -> Settings.SOUND_PUNISHMENT_MUTE_GLOBAL.sound();
            case UNMUTE -> Settings.SOUND_PUNISHMENT_UNMUTE_GLOBAL.sound();
            case BAN -> Settings.SOUND_PUNISHMENT_BAN_GLOBAL.sound();
            case UNBAN -> Settings.SOUND_PUNISHMENT_UNBAN_GLOBAL.sound();
            case IPMUTE -> Settings.SOUND_PUNISHMENT_IPMUTE_GLOBAL.sound();
            case UNIPMUTE -> Settings.SOUND_PUNISHMENT_UNIPMUTE_GLOBAL.sound();
            case IPBAN -> Settings.SOUND_PUNISHMENT_IPBAN_GLOBAL.sound();
            case UNIPBAN -> Settings.SOUND_PUNISHMENT_UNIPBAN_GLOBAL.sound();
        };

        if ( SOUND_STAFF == null || SOUND_STAFF.isEnabled() == false ) { SOUND_STAFF = SOUND_GLOBAL; }
        if ( SOUND_TARGET != null && SOUND_TARGET.isEnabled() == false ) { SOUND_TARGET = SOUND_GLOBAL; }
        if ( silent ) { SOUND_GLOBAL = null; }

        if ( SOUND_TARGET != null && SOUND_TARGET.isEnabled() && pun.getPlayer().isOnline() ) {
            SOUND_TARGET.playSound( pun.getPlayer().getPlayer() );
        }
        if ( SOUND_STAFF != null && SOUND_STAFF.isEnabled() ) {
            SOUND_STAFF.playSoundMany( p -> p.getUniqueId().equals( pun.getPlayer().getUniqueId() ) == false
                    && CyberVaultUtils.hasPerms( p, Settings.STAFF_PERMISSION.string() ) );
        }
        if ( SOUND_GLOBAL != null && SOUND_GLOBAL.isEnabled() ) {
            SOUND_GLOBAL.playSoundMany( p -> p.getUniqueId().equals( pun.getPlayer().getUniqueId() ) == false
                    && CyberVaultUtils.hasPerms( p, Settings.STAFF_PERMISSION.string() ) == false );
        }
    }

    // ! debug method
    private static void temp( String str ) {
        str = str.replaceAll( "\n", "/n" );
        CyberMsgUtils.broadcast( "&fdebug >> " + str );
    }
}