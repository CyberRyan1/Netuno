package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.spigot.command.CyberCommand;
import com.github.cyberryan1.cybercore.spigot.command.sent.SentCommand;
import com.github.cyberryan1.cybercore.spigot.command.settings.ArgType;
import com.github.cyberryan1.cybercore.spigot.utils.CyberCommandUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberVaultUtils;
import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.api.models.ApiPunishment;
import com.github.cyberryan1.netuno.models.commands.CommandHelpInfo;
import com.github.cyberryan1.netuno.models.libraries.PunishmentLibrary;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.TimestampUtils;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;

import java.util.List;

/**
 * A class to automatically generate all punishment related
 * commands
 *
 * @author Ryan
 */
public class PunishmentCommandGenerator {

    private static final String USAGE_MESSAGE_FORMAT = "&8/&s[LABEL] &p[ARGS]";
    private static final List<String> DEFAULT_SUGGESTED_TIMES = List.of( "15m", "1h", "12h", "1d", "3d", "1w", "forever" );

    /**
     * Generates all punishment commands for Netuno
     *
     * @param startingHelpOrder Where to start with the help
     *                          order
     */
    public static void generateCommands( int startingHelpOrder ) {
        for ( CommandSettings setting : CommandSettings.values() ) {
            final String label = setting.name().toLowerCase();
            final CommandSettings settings = CommandSettings.valueOf( label.toUpperCase() );
            final ApiPunishment.PunType punishmentType = ApiPunishment.PunType.valueOf( label.toUpperCase() );
            final String permission = PunishmentLibrary.getSettingForMessageType( punishmentType, PunishmentLibrary.MessageSetting.PERMISSION ).string();

            // Extracting the usage
            String usage = USAGE_MESSAGE_FORMAT.replace( "[LABEL]", label );
            // If the command is an unpunishment, only arg is the player and an optional -s
            if ( settings.isUnpunishment() ) {
                usage = usage.replace( "[ARGS]", "(player) [-s]" );
            }
            // If the command has no length, only args are the player, the reason, and optional -s
            else if ( settings.hasLength() == false ) {
                usage = usage.replace( "[ARGS]", "(player) (reason) [-s]" );
            }
            // Otherwise, the args are the player, the length, the reason, and optional -s
            else {
                usage = usage.replace( "[ARGS]", "(player) (length) (reason) [-s]" );
            }

            CyberCommand command = new CyberCommand( label, permission, Settings.PERM_DENIED_MSG.coloredString(), usage ) {
                @Override
                public List<String> tabComplete( SentCommand command ) {
                    // If the command either has no length or is an unpunishment, we let
                    //      the default provided tab completer deal with it
                    if ( settings.hasLength() == false || settings.isUnpunishment() )
                        return List.of();

                    // If the player is currently at the length arg, we can suggest a
                    //      default list of times
                    if ( command.getArgs().length <= 1 ) {
                        return List.of();
                    }
                    else if ( command.getArg( 1 ).length() == 0 ) {
                        return DEFAULT_SUGGESTED_TIMES;
                    }
                    else if ( command.getArgs().length == 2 ) {
                        return CyberCommandUtils.matchArgs( DEFAULT_SUGGESTED_TIMES, command.getArg( 1 ) );
                    }
                    return List.of();
                }

                @Override
                public boolean execute( SentCommand command ) {
                    // If the command either has no length or is an unpunishment, we can
                    //      go ahead and execute the punishment
                    if ( settings.hasLength() == false || settings.isUnpunishment() ) {
                        executePunishmentFromCommandArgs( command );
                    }

                    // Otherwise if the time provided is not allowed, send an error msg
                    else if ( TimestampUtils.isAllowableLength( command.getArg( 1 ) ) == false ) {
                        CommandErrors.sendInvalidTimespan( command.getSender(), command.getArg( 1 ) );
                    }

                    // Otherwise execute the punishment
                    else {
                        executePunishmentFromCommandArgs( command );
                    }

                    return true;
                }
            };

            // Setting the minimum command args
            int commandMinArgs = 2;
            if ( settings.isUnpunishment ) commandMinArgs = 1;
            else if ( settings.hasLength() ) commandMinArgs = 3;
            command.setMinArgLength( commandMinArgs );

            // Setting the arg type of the first argument
            command.setArgType( 0, ArgType.OFFLINE_PLAYER );
            if ( settings == CommandSettings.KICK )
                command.setArgType( 0, ArgType.ONLINE_PLAYER );

            // Demand the executor of the command has permission to run it
            command.demandPermission( true );
            // Run the command async
            command.setRunAsync( true );

            // Register the command with CommandHelpInfo
            new CommandHelpInfo( command, startingHelpOrder + settings.getHelpOrder() );
            // Register the command, in general, with tab completions enabled
            command.register( true );
        }
    }

    /**
     * Attempts to execute a punishment just from the args of a command
     * @param command The sent command
     */
    public static void executePunishmentFromCommandArgs( SentCommand command ) {
        ApiPunishment.PunType punType = ApiPunishment.PunType.valueOf( command.getCommand().getName().toUpperCase() );
        if ( punType == null ) throw new NullPointerException();

        OfflinePlayer staff = command.getSender() instanceof ConsoleCommandSender ? ApiPunishment.CONSOLE_IS_STAFF : ( OfflinePlayer ) command.getSender();
        OfflinePlayer target = command.getOfflinePlayerAtArg( 0 );
        long duration = ApiPunishment.PUNISHMENT_NO_LENGTH;
        String reason;

        if ( punType.hasNoLength() == false ) {
            duration = TimestampUtils.getTimestampFromUnformulatedLength( command.getArg( 1 ) );
            reason = command.getCombinedArgs( 2 );
        }
        else {
            reason = command.getCombinedArgs( 1 );
        }

        boolean silent = reason.contains( "-s" ) && CyberVaultUtils.hasPerms( command.getSender(), Settings.SILENT_PERMISSION.string() );
        if ( silent ) reason = reason.replaceAll( "-s", "" );

        Netuno.PUNISHMENT_SERVICE.punishmentBuilder()
                .setPlayer( target )
                .setStaff( staff )
                .setType( punType )
                .setLength( duration )
                .setReason( reason )
                .build()
                .execute( silent );
    }

    enum CommandSettings {
        WARN( 1, false, false ),
        KICK( 2, false, false ),
        MUTE( 3, true, false ),
        UNMUTE( 4, false, false ),
        IPMUTE( 7, true, false ),
        UNIPMUTE( 8, false, false ),
        BAN( 5, true, false ),
        UNBAN( 6, false, false ),
        IPBAN( 9, true, false ),
        UNIPBAN( 10, false, false );

        private final int helpOrder;
        private final boolean hasLength;
        private final boolean isUnpunishment;

        CommandSettings( int helpOrder, boolean hasLength, boolean isUnpunishment ) {
            this.helpOrder = helpOrder;
            this.hasLength = hasLength;
            this.isUnpunishment = isUnpunishment;
        }

        public int getHelpOrder() {
            return this.helpOrder;
        }

        public boolean hasLength() {
            return this.hasLength;
        }

        public boolean isUnpunishment() {
            return this.isUnpunishment;
        }
    }
}