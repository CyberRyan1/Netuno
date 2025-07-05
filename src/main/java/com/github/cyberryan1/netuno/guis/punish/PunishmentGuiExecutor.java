package com.github.cyberryan1.netuno.guis.punish;

import com.github.cyberryan1.cybercore.spigot.utils.CyberColorUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberMsgUtils;
import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.api.models.ApiPlayer;
import com.github.cyberryan1.netuno.api.models.ApiPunishment;
import com.github.cyberryan1.netuno.guis.punish.models.PunGuiType;
import com.github.cyberryan1.netuno.guis.punish.models.PunishSettings;
import com.github.cyberryan1.netuno.guis.punish.models.SinglePunishButton;
import com.github.cyberryan1.netuno.models.NetunoPunishment;
import com.github.cyberryan1.netuno.models.libraries.PunishmentLibrary;
import com.github.cyberryan1.netuno.utils.PrettyStringLibrary;
import com.github.cyberryan1.netuno.utils.TimestampUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * A helper class to execute punishments that were selected
 * via the <code>/punish</code> GUI. This is also used to help
 * with executing quick punishments <i>(i.e. <code>/punish
 * (player) (quick code)</code></i>
 *
 * @author Ryan
 */
public class PunishmentGuiExecutor {

    private static final String REASON_FORMAT = "[REASON] ([NUMBER] Offense)";
    private static final String HIGHEST_MUTED_ALT_CONFIG_VARIABLE = "HIGHEST_MUTED_ALT";
    private static final String HIGHEST_BANNED_ALT_CONFIG_VARIABLE = "HIGHEST_BANNED_ALT";
    private static final String LENGTH_REMAINING_CONFIG_VARIABLE = "LENGTH_REMAINING";

    /**
     * Executes this punishment on the provided player as if
     * it was ran by the provided staff
     * <b>Note</b> Everything ran by this method is done async
     *
     * @param staffSender The staff member
     * @param offlinePlayer The target player
     * @param silent Whether to handle this punishment silently
     */
    public static void executePunish( SinglePunishButton button, CommandSender staffSender, OfflinePlayer offlinePlayer, boolean silent ) {
        Netuno.SERVICE.getPlayer( offlinePlayer ).thenAcceptAsync( player -> {
            button.generatePreviousPunCount( offlinePlayer ).thenAccept( previousPunCount -> {
                String reason = REASON_FORMAT.replace( "[REASON]", CyberColorUtils.deleteColor( CyberColorUtils.getColored( button.getItemName() ) ) );
                reason = reason.replace( "[NUMBER]", PrettyStringLibrary.getIntegerAsAmount( previousPunCount + 1 ) );

                final PunGuiType punGuiType = button.getGuiType();
                ApiPunishment.PunType punType = ApiPunishment.PunType.valueOf( punGuiType.name() );
                long duration = ApiPunishment.PUNISHMENT_NO_LENGTH;

                // Handling warns
                if ( punGuiType == PunGuiType.WARN ) {
                    // If the amount of warns the player has had is greater then
                    //      or equal to the number of warns to start punishing
                    //      after, execute the higher tier punish on them
                    if ( previousPunCount >= button.getPunishAfter() ) {
                        punType = button.getPunishTypeAfter();

                        // If the punishment has a length, calculate the duration
                        //      and scale it, if autoscaling is enabled for this
                        //      punishment
                        if ( punType.hasNoLength() == false ) {
                            duration = TimestampUtils.getTimestampFromUnformulatedLength( button.getStartingTime() );
                            if ( button.isAutoscaleEnabled() ) duration = getScaledDuration( button, previousPunCount );
                        }
                    }
                }

                // Handling everything else
                // Else if the punishment type is an IP punishment and contains
                //      an IP punishment related config variable, handle that
                else if ( punType.isIpPunishment() && ( button.getStartingTime().equalsIgnoreCase( HIGHEST_MUTED_ALT_CONFIG_VARIABLE )
                        || button.getStartingTime().equalsIgnoreCase( HIGHEST_BANNED_ALT_CONFIG_VARIABLE ) ) ) {
                    // Whether the length to punish this player for is the length remaining on the
                    //      highest punishment (true) or the original length of the highest
                    //      punishment (false)
                    boolean highestDurationIsRemaining = isHighestPunishDurationRemaining( punGuiType, button.getPunishTypeAfter() );

                    // Getting all active punishments from all alts, provided
                    //      the punishment type is equal to this.punishTypeAfter
                    List<NetunoPunishment> activeAltPunishments = new ArrayList<>();
                    try {
                        List<ApiPlayer> altsList = Netuno.ALT_SERVICE.getAlts( ( ApiPlayer ) player ).get();
                        for ( ApiPlayer alt : altsList ) {
                            activeAltPunishments.addAll( alt.getActivePunishments().stream()
                                    .filter( pun -> pun.getType() == button.getPunishTypeAfter() )
                                    .map( pun -> ( NetunoPunishment ) pun )
                                    .collect( Collectors.toList() ) );
                        }
                    } catch ( InterruptedException |
                              ExecutionException e ) {
                        throw new RuntimeException( e );
                    }

                    // If the player has no alts with active
                    if ( activeAltPunishments.isEmpty() ) {
                        String punTypeString = button.getPunishTypeAfter().name().toLowerCase() + "s";
                        CyberMsgUtils.sendMsg( staffSender, "&p" + player.getPlayer().getName() + " &shas no alts with active " + punTypeString );
                        return;
                    }

                    NetunoPunishment highestPunishment = highestDurationIsRemaining
                            ? PunishmentLibrary.getPunishmentWithHighestDurationRemaining( activeAltPunishments )
                            : PunishmentLibrary.getPunishmentWithHighestOriginalLength( activeAltPunishments );
                    duration = highestPunishment.getDurationRemaining();
                }

                // Handling everything else
                else {
                    // Setting the duration
                    duration = TimestampUtils.getTimestampFromUnformulatedLength( button.getStartingTime() );
                    if ( button.isAutoscaleEnabled() ) duration = getScaledDuration( button, previousPunCount );
                }

                // Executing the punishment
                UUID staffUuid = ( staffSender instanceof ConsoleCommandSender )
                        ? ( ApiPunishment.CONSOLE_UUID ) : ( ( ( Player ) staffSender ).getUniqueId() );
                Netuno.PUNISHMENT_SERVICE.punishmentBuilder()
                        .setPlayer( player.getUuid() )
                        .setStaff( staffUuid )
                        .setType( punType )
                        .setLength( duration )
                        .setReason( reason )
                        .markAsGuiPunishment( true )
                        .build()
                        .execute( silent );

                if ( staffSender instanceof Player ) {
                    Player staff = ( Player ) staffSender;
                    staff.playSound( staff.getLocation(), Sound.ENTITY_ENDER_EYE_DEATH, 10, 1 );
                }
            } );
        } );
    }

    /**
     * @param type The type of the GUI
     * @param punType The punishment type
     * @return True if the punish duration is set to be the punishment
     * of an alt with the longest duration remaining, false if the
     * duration is specified to be the punishment of an alt with the
     * longest length
     */
    private static boolean isHighestPunishDurationRemaining( PunGuiType type, ApiPunishment.PunType punType ) {
        PunishSettings setting =  switch ( type ) {
            case IPMUTE -> punType == ApiPunishment.PunType.MUTE ? PunishSettings.IPMUTE_SETTING_HIGHEST_MUTE_LENGTH : PunishSettings.IPMUTE_SETTING_HIGHEST_BAN_LENGTH;
            case IPBAN -> punType == ApiPunishment.PunType.MUTE ? PunishSettings.IPBAN_SETTING_HIGHEST_MUTE_LENGTH : PunishSettings.IPBAN_SETTING_HIGHEST_BAN_LENGTH;
            default -> throw new RuntimeException();
        };
        return setting.string().equalsIgnoreCase( LENGTH_REMAINING_CONFIG_VARIABLE );
    }

    /**
     * Scales the starting time
     * <code>this.previousPunCount - this.punishAfter + 1</code>
     * times with a scale of 2
     * @return The scaled duration
     */
    private static long getScaledDuration( SinglePunishButton button, int previousPunCount ) {
        long originalDuration = TimestampUtils.getTimestampFromUnformulatedLength( button.getStartingTime() );
        return TimestampUtils.getScaledDuration( originalDuration, 2, previousPunCount - button.getPunishAfter() + 1 );
    }

    /**
     * Replaces the [TARGET] and [PREVIOUS] config settings
     * within the provided string with the correct data
     * @param str The config string
     * @param target The target
     * @param punCount The amount of previous punishments
     * @return The replaced string
     */
    public static String replaceVariables( String str, OfflinePlayer target, int punCount ) {
        return str.replace( "[TARGET]", target.getName() ).replace( "[PREVIOUS]", punCount + "" );
    }

}