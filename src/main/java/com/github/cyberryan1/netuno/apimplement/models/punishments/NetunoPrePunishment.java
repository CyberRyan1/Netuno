package com.github.cyberryan1.netuno.apimplement.models.punishments;

import com.github.cyberryan1.cybercore.utils.CoreUtils;
import com.github.cyberryan1.cybercore.utils.VaultUtils;
import com.github.cyberryan1.netuno.apimplement.ApiNetuno;
import com.github.cyberryan1.netuno.apimplement.models.players.NetunoPlayer;
import com.github.cyberryan1.netuno.apimplement.models.players.NetunoPlayerCache;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;
import com.github.cyberryan1.netunoapi.models.punishments.PunishmentType;
import com.github.cyberryan1.netunoapi.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class NetunoPrePunishment extends NPunishment {

    public NetunoPrePunishment( OfflinePlayer player, OfflinePlayer staff, PunishmentType punishmentType, String unformattedLength, String reason ) {
        super.setPlayer( player );
        super.setStaff( staff );
        super.setPunishmentType( punishmentType );
        super.setLength( TimeUtils.durationFromUnformatted( unformattedLength ).timestamp() );
        super.setReason( reason );
    }

    public NetunoPrePunishment( OfflinePlayer player, OfflinePlayer staff, PunishmentType punishmentType, String reason ) {
        super.setPlayer( player );
        super.setStaff( staff );
        super.setPunishmentType( punishmentType );
        super.setReason( reason );
    }

    public NetunoPrePunishment( NPunishment pun ) {
        this.id = pun.getId();
        this.punishmentType = pun.getPunishmentType();
        this.playerUuid = pun.getPlayerUuid();
        this.staffUuid = pun.getStaffUuid();
        this.length = pun.getLength();
        this.timestamp = pun.getTimestamp();
        this.reason = pun.getReason();
        this.referencePunId = pun.getReferencePunId();
        this.guiPun = pun.isGuiPun();
        this.active = pun.isActive();
        this.needsNotifSent = pun.needsNotifSent();
    }

    public NetunoPrePunishment() {}

    public void executePunishment() {
        // Checking if the punishment is silent
        boolean silent = super.getReason().contains( "-s" );
        // Removing all -s from the reason
        super.setReason( super.getReason().replace( "-s", "" ) );

        // Set if the player needs a notification or not
        // Notifications are only sent when an offline player is warned
        super.setNeedsNotifSent( super.getPunishmentType() == PunishmentType.WARN && super.getPlayer().isOnline() == false );

        // Setting the reference ID if the punishment is an IP punishment
        if ( super.getPunishmentType().isIpPunishment() ) {
            super.setReferencePunId( -1 );
        }

        // Executing the punishment in the database
        ApiNetuno.getData().getNetunoPuns().addPunishment( this );

        // Iterating through all the player's alts and adding reference punishments to them
        //      Only if the punishment is an IP punishment.
        if ( super.getPunishmentType().isIpPunishment() ) {
            final int recentlyAddedId = ApiNetuno.getData().getNetunoPuns().getRecentlyInsertedId();
            if ( recentlyAddedId == -1 ) {
                CoreUtils.logError( "An error occurred while trying to get the most recently inserted ID, "
                        + "so the reference punishment could not be added to the player's alts." );
                return;
            }

            for ( OfflinePlayer alt : ApiNetuno.getData().getNetunoAlts().getAlts( super.getPlayer() ) ) {
                if ( alt.getUniqueId().equals( super.getPlayer().getUniqueId() ) == false ) {
                    NetunoPrePunishment altPun = new NetunoPrePunishment( this );
                    altPun.setId( -1 );
                    altPun.setPlayer( alt );
                    altPun.setReferencePunId( recentlyAddedId );
                    altPun.executeAsReferencePunishment();
                }
            }
        }

        // For unpunishments (unmutes, unbans, etc.), need to go through all their previous punishments of
        //      the unpunishment type and set them as unactive.
        if ( super.getPunishmentType().hasNoReason() ) {
            final PunishmentType unpunType = switch ( super.getPunishmentType() ) {
                case UNMUTE -> PunishmentType.MUTE;
                case UNBAN -> PunishmentType.BAN;
                case UNIPMUTE -> PunishmentType.IPMUTE;
                case UNIPBAN -> PunishmentType.IPBAN;
                default -> null;
            };

            if ( unpunType == null ) {
                CoreUtils.logError( "An error occurred while trying to get the unpunishment type, "
                        + "so the previous punishments could not be set as unactive." );
                return;
            }

            // If the punishment type is an un IP punishment, need to go through all the player's alts
            //      Otherwise just need to go through the player
            if ( super.getPunishmentType().isIpPunishment() == false ) {
                final NetunoPlayer nPlayer = NetunoPlayerCache.forceLoad( super.getPlayer() );
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
                for ( OfflinePlayer alt : ApiNetuno.getData().getNetunoAlts().getAlts( super.getPlayer() ) ) {
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

        // Kicking the player from the server if the punishment is a kick, ban, or ipban and the player is online
        attemptKickPlayer();
        // Sending the player a notification if the punishment is a warn, mute, unmute, ipmute, or unipmute and the player is online
        attemptSendMessage();
        // Sending the broadcasts
        doBroadcasts( silent );
    }

    private void executeAsReferencePunishment() {
        // Executing the punishment in the database
        ApiNetuno.getData().getNetunoPuns().addPunishment( this );

        // Kicking the player from the server if the punishment is a kick, ban, or ipban and the player is online
        attemptKickPlayer();
        // Sending the player a notification if the punishment is a warn, mute, unmute, ipmute, or unipmute and the player is online
        attemptSendMessage();
    }

    private void attemptKickPlayer() {
        if ( super.getPlayer().isOnline() == false ) { return; }
        String lines = switch ( super.getPunishmentType() ) {
            case KICK -> Utils.getCombinedString( Settings.KICK_KICKED_LINES.coloredStringlist() );
            case BAN -> Utils.getCombinedString( Settings.BAN_MESSAGE.coloredStringlist() );
            case IPBAN -> Utils.getCombinedString( Settings.IPBAN_MESSAGE.coloredStringlist() );
            default -> null;
        };

        if ( lines != null && lines.replace( "\n", "" ).isBlank() == false ) {
            lines = Utils.replaceAllVariables( lines, this );
            super.getPlayer().getPlayer().kickPlayer( lines );
        }
    }

    private void attemptSendMessage() {
        if ( super.getPlayer().isOnline() == false ) { return; }
        String lines = switch ( super.getPunishmentType() ) {
            case WARN -> Utils.getCombinedString( Settings.WARN_MESSAGE.coloredStringlist() );
            case MUTE -> Utils.getCombinedString( Settings.MUTE_MESSAGE.coloredStringlist() );
            case UNMUTE -> Utils.getCombinedString( Settings.UNMUTE_MESSAGE.coloredStringlist() );
            case IPMUTE -> Utils.getCombinedString( Settings.IPMUTE_MESSAGE.coloredStringlist() );
            case UNIPMUTE -> Utils.getCombinedString( Settings.UNIPMUTE_MESSAGE.coloredStringlist() );
            default -> null;
        };

        if ( lines != null && lines.replace( "\n", "" ).isBlank() == false ) {
            lines = Utils.replaceAllVariables( lines, this );
            super.getPlayer().getPlayer().sendMessage( lines );
        }
    }

    private void doBroadcasts( boolean silent ) {
        // Getting the public and staff announcement messages from the settings
        final String publicBroadcastLines[] = switch ( super.getPunishmentType() ) {
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
        final String staffBroadcastLines[] = switch ( super.getPunishmentType() ) {
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

        // Send the public punishment announcement to all online, non-staff players who are not the target.
        //      No public broadcast will be sent if the punishment is silent or if the publicBroadcastLines
        //      is null or empty. Will send the publicBroadcastLines to staff if the staffBroadcastLines
        //      variable is either null or has no contents.
        boolean staffBroadcastNoExist = ( staffBroadcastLines == null ) || ( staffBroadcastLines.length == 0 );
        if ( silent == false && publicBroadcastLines != null && publicBroadcastLines.length > 0 ) {
            final String publicBroadcast = Utils.replaceAllVariables( Utils.getCombinedString( publicBroadcastLines ), this );

            for ( Player player : Bukkit.getOnlinePlayers() ) {
                if ( player.getUniqueId().toString().equals( super.getPlayerUuid() ) == false ) {
                    if ( staffBroadcastNoExist || VaultUtils.hasPerms( player, Settings.STAFF_PERMISSION.string() ) == false ) {
                        Utils.sendAnyMsg( player, publicBroadcast );
                    }
                }
            }
        }

        // Send the staff punishment announcement to all online players, even if the staff is the target.
        //      No staff broadcast will be sent if the staffBroadcastNoExist variable is true. If the
        //      silent variable is true, the staff announcement needs to be sent with the silent prefix
        //      and the broadcast should only be sent to staff who have the view silent punishments permission.
        if ( staffBroadcastNoExist == false ) {
            String staffBroadcast = "";
            for ( String line : staffBroadcastLines ) {
                if ( silent && line.replace( "\n", "" ).isBlank() == false ) {
                    staffBroadcast += Settings.SILENT_PREFIX.coloredString() + " ";
                }
                staffBroadcast += Utils.replaceAllVariables( line, this ) + "\n";
            }

            String permission = Settings.STAFF_PERMISSION.string();
            if ( silent ) { permission = Settings.SILENT_PERMISSION.string(); }
            for ( Player player : Bukkit.getOnlinePlayers() ) {
                if ( VaultUtils.hasPerms( player, permission ) ) {
                    Utils.sendAnyMsg( player, staffBroadcast );
                }
            }
        }
    }
}