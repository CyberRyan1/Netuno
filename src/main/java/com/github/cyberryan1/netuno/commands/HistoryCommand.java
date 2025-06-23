package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.spigot.command.CyberSubCommand;
import com.github.cyberryan1.cybercore.spigot.command.CyberSuperCommand;
import com.github.cyberryan1.cybercore.spigot.command.sent.SentCommand;
import com.github.cyberryan1.cybercore.spigot.command.sent.SentSubCommand;
import com.github.cyberryan1.cybercore.spigot.command.settings.ArgType;
import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.api.models.ApiPunishment;
import com.github.cyberryan1.netuno.guis.history.HistoryConfirmDeleteGui;
import com.github.cyberryan1.netuno.guis.history.HistoryEditGui;
import com.github.cyberryan1.netuno.guis.history.HistoryListGui;
import com.github.cyberryan1.netuno.guis.history.HistoryStaffGui;
import com.github.cyberryan1.netuno.models.commands.CommandHelpInfo;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.TimestampUtils;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class HistoryCommand extends CyberSuperCommand {

    public HistoryCommand( int helpOrder ) {
        super(
                "history",
                Settings.HISTORY_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
            null
        );
        register( true );
        new CommandHelpInfo( this, helpOrder );

        HistoryListSubcommand list = new HistoryListSubcommand();
        new CommandHelpInfo( list, helpOrder + 1 );
        HistoryEditSubcommand edit = new HistoryEditSubcommand();
        new CommandHelpInfo( edit, helpOrder + 2 );
        HistoryStaffSubcommand staff = new HistoryStaffSubcommand();
        new CommandHelpInfo( staff, helpOrder + 3 );
        HistoryDeleteSubcommand delete = new HistoryDeleteSubcommand();
        new CommandHelpInfo( delete, helpOrder + 4 );
        HistoryResetSubcommand reset = new HistoryResetSubcommand();
        new CommandHelpInfo( reset, helpOrder + 5 );
        HistoryRollbackPlayerSubcommand rollbackPlayer = new HistoryRollbackPlayerSubcommand();
        new CommandHelpInfo( rollbackPlayer, helpOrder + 6 );

        addSubCommand( list );
        addSubCommand( edit );
        addSubCommand( staff );
        addSubCommand( delete );
        addSubCommand( reset );
        addSubCommand( rollbackPlayer );

        demandPermission( true );
        demandPlayer( true );
        setMinArgLength( 2 );
    }

    @Override
    public List<String> tabComplete( SentCommand command ) {
        return List.of();
    }

    @Override
    public boolean execute( SentCommand command ) {
        return true;
    }
}

class HistoryListSubcommand extends CyberSubCommand {

    public HistoryListSubcommand() {
        super(
                "list",
                Settings.HISTORY_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                "&8/&shistory &plist (player)"
        );
        setDemandPermission( true );
        setDemandPlayer( true );
        setMinArgLength( 1 );
        setArgType( 0, ArgType.OFFLINE_PLAYER );

    }

    @Override
    public List<String> tabComplete( SentCommand command, SentSubCommand subCommand ) {
        return List.of();
    }

    @Override
    public boolean execute( SentCommand command, SentSubCommand subCommand ) {
        final Player player = subCommand.getPlayer();
        final OfflinePlayer target = subCommand.getOfflinePlayerAtArg( 0 );

        HistoryListGui gui = new HistoryListGui( player, target );
        gui.open();
        return true;
    }
}

class HistoryEditSubcommand extends CyberSubCommand {

    public HistoryEditSubcommand() {
        super(
                "edit",
                Settings.HISTORY_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                "&8/&shistory &pedit (pun ID)"
        );
        setDemandPermission( true );
        setDemandPlayer( true );
        setMinArgLength( 1 );
        setArgType( 0, ArgType.INTEGER );

    }

    @Override
    public List<String> tabComplete( SentCommand command, SentSubCommand subCommand ) {
        return List.of();
    }

    @Override
    public boolean execute( SentCommand command, SentSubCommand subCommand ) {
        final Player player = subCommand.getPlayer();
        final int punId = Integer.parseInt( subCommand.getArg( 0 ) );
        subCommand.respond( "&sLoading punishment &p#" + punId + "&s..." );

        Netuno.PUNISHMENT_SERVICE.getPunishment( punId ).thenAccept( optionalPunishment -> {
            if ( optionalPunishment.isEmpty() ) {
                CommandErrors.sendInvalidPunishmentID( command.getPlayer(), "" + punId );
            }

            else {
                HistoryEditGui gui = new HistoryEditGui( player, optionalPunishment.get().getId() );
                gui.open();
            }
        } ).exceptionally( Netuno.FUTURE_ERROR_HANDLING );
        return true;
    }
}

class HistoryStaffSubcommand extends CyberSubCommand {

    public HistoryStaffSubcommand() {
        super(
                "staff",
                Settings.HISTORY_STAFF_LIST_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                "&8/&shistory &pstaff (player)"
        );
        setDemandPermission( true );
        setDemandPlayer( true );
        setMinArgLength( 1 );
        setArgType( 0, ArgType.OFFLINE_PLAYER );

    }

    @Override
    public List<String> tabComplete( SentCommand command, SentSubCommand subCommand ) {
        return List.of();
    }

    @Override
    public boolean execute( SentCommand command, SentSubCommand subCommand ) {
        final Player player = subCommand.getPlayer();
        final OfflinePlayer target = subCommand.getOfflinePlayerAtArg( 0 );

        HistoryStaffGui gui = new HistoryStaffGui( player, target );
        gui.open();
        return true;
    }
}

class HistoryDeleteSubcommand extends CyberSubCommand {

    public HistoryDeleteSubcommand() {
        super(
                "delete",
                Settings.HISTORY_DELETE_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                "&8/&shistory &pdelete (pun ID)"
        );
        setDemandPermission( true );
        setDemandPlayer( true );
        setMinArgLength( 1 );
        setArgType( 0, ArgType.INTEGER );

    }

    @Override
    public List<String> tabComplete( SentCommand command, SentSubCommand subCommand ) {
        return List.of();
    }

    @Override
    public boolean execute( SentCommand command, SentSubCommand subCommand ) {
        final Player player = subCommand.getPlayer();
        final int punId = Integer.parseInt( subCommand.getArg( 0 ) );
        subCommand.respond( "&sLoading punishment &p#" + punId + "&s..." );

        Netuno.PUNISHMENT_SERVICE.getPunishment( punId ).thenAccept( optionalPunishment -> {
            if ( optionalPunishment.isEmpty() ) {
                CommandErrors.sendInvalidPunishmentID( command.getPlayer(), "" + punId );
            }

            else {
                HistoryConfirmDeleteGui gui = new HistoryConfirmDeleteGui( player, optionalPunishment.get() );
                gui.open();
            }
        } ).exceptionally( Netuno.FUTURE_ERROR_HANDLING );
        return true;
    }
}

class HistoryResetSubcommand extends CyberSubCommand {

    public HistoryResetSubcommand() {
        super(
                "reset",
                Settings.HISTORY_RESET_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                "&8/&shistory &preset (player)"
        );
        setDemandPermission( true );
        setDemandPlayer( true );
        setMinArgLength( 1 );
        setArgType( 0, ArgType.OFFLINE_PLAYER );
    }

    @Override
    public List<String> tabComplete( SentCommand command, SentSubCommand subCommand ) {
        return List.of();
    }

    @Override
    public boolean execute( SentCommand command, SentSubCommand subCommand ) {
        final OfflinePlayer target = subCommand.getOfflinePlayerAtArg( 0 );
        subCommand.respond( "&sDeleting all punishments for &p" +  target.getName() + "&s..." );

        Netuno.SERVICE.getPlayer( target ).thenAccept( apiTarget -> {
            int size = apiTarget.getPunishments().size();
            List<ApiPunishment> punishments = apiTarget.getPunishments();
            for ( int index = size - 1; index >= 0; index-- ) {
                Netuno.PUNISHMENT_SERVICE.deletePunishment( punishments.get( index ) );
            }

            subCommand.respond( "&sSuccessfully deleted &p" + size + "&s punishments" );
        } ).exceptionally( Netuno.FUTURE_ERROR_HANDLING );

        return true;
    }
}

class HistoryRollbackPlayerSubcommand extends CyberSubCommand {

    public HistoryRollbackPlayerSubcommand() {
        super(
                "rollbackplayer",
                Settings.HISTORY_ROLLBACK_PLAYER_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                "&8/&shistory &prollbackplayer (player) (time)"
        );
        setDemandPermission( true );
        setDemandPlayer( true );
        setMinArgLength( 2 );
        setArgType( 0, ArgType.OFFLINE_PLAYER );
    }

    @Override
    public List<String> tabComplete( SentCommand command, SentSubCommand subCommand ) {
        return List.of();
    }

    @Override
    public boolean execute( SentCommand command, SentSubCommand subCommand ) {
        final OfflinePlayer target = subCommand.getOfflinePlayerAtArg( 0 );
        final String timespanArg = subCommand.getArg( 1 );

        if ( TimestampUtils.isAllowableLength( timespanArg ) == false ) {
            CommandErrors.sendInvalidTimespan( command.getPlayer(), timespanArg );
            return true;
        }

        final long duration = TimestampUtils.getTimestampFromUnformulatedLength( timespanArg );
        final String durationString = TimestampUtils.timestampToFormulatedLength( duration, -1 );
        subCommand.respond( "&sRolling back &p" + target.getName() + "&s's punishments by &p" + durationString + "&s..." );

        final long now = TimestampUtils.getCurrentTimestamp();

        Netuno.SERVICE.getPlayer( target ).thenAccept( apiPlayer -> {
            final List<ApiPunishment> punishments = apiPlayer.getPunishments();
            int deleted = 0;

            for ( int index = punishments.size() - 1; index >= 0; index-- ) {
                ApiPunishment current = punishments.get( index );
                // if the timestamp has not expired, then that means it is within the provided duration since the current time
                if ( TimestampUtils.timestampHasExpired( current.getTimestamp(), duration ) == false ) {
                    Netuno.PUNISHMENT_SERVICE.deletePunishment( current );
                    deleted++;
                }
            }

            subCommand.respond( "&sSuccessfully deleted &p" +  deleted + "&s punishments" );
        } ).exceptionally( Netuno.FUTURE_ERROR_HANDLING );

        return true;
    }
}
