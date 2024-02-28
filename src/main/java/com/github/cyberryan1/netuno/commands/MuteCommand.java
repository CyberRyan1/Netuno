package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.spigot.command.sent.SentCommand;
import com.github.cyberryan1.cybercore.spigot.command.settings.ArgType;
import com.github.cyberryan1.cybercore.spigot.utils.CyberCommandUtils;
import com.github.cyberryan1.netuno.apimplement.models.punishments.NetunoPrePunishment;
import com.github.cyberryan1.netuno.models.commands.HelpableCommand;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netunoapi.models.punishments.PunishmentType;
import com.github.cyberryan1.netunoapi.utils.TimeUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

;

public class MuteCommand extends HelpableCommand {

    private static final List<String> suggestedTimes = List.of( "15m", "1h", "12h", "1d", "3d", "1w", "forever" );

    public MuteCommand( int helpOrder ) {
        super(
                "mute",
                Settings.MUTE_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.string(),
                "&8/&smute &p(player) (length/forever) (reason) [-s]"
        );
        register( true );

        demandPermission( true );
        setMinArgLength( 3 );
        setArgType( 0, ArgType.OFFLINE_PLAYER );
        setRunAsync( true );
    }

    @Override
    public List<String> tabComplete( SentCommand command ) {
        if ( command.getArgs().length <= 1 ) { return List.of(); }
        else if ( command.getArg( 1 ).length() == 0 ) { return suggestedTimes; }
        else if ( command.getArgs().length == 2 ) { return CyberCommandUtils.matchArgs( suggestedTimes, command.getArg( 1 ) ); }
        return List.of();
    }

    @Override
    // /mute (player) (length/forever) (reason)
    public boolean execute( SentCommand command ) {
        if ( TimeUtils.isAllowableLength( command.getArg( 1 ) ) == false ) {
            CommandErrors.sendInvalidTimespan( command.getSender(), command.getArg( 1 ) );
            return true;
        }

        final OfflinePlayer target = command.getOfflinePlayerAtArg( 0 );
        NetunoPrePunishment prePun = new NetunoPrePunishment();
        prePun.getPunishment().setPlayer( target );
        prePun.getPunishment().setPunishmentType( PunishmentType.MUTE );
        prePun.getPunishment().setTimestamp( TimeUtils.getCurrentTimestamp() );
        prePun.getPunishment().setReason( command.getCombinedArgs( 2 ) );
        prePun.getPunishment().setLength( TimeUtils.durationFromUnformatted( command.getArg( 1 ) ).timestamp() );
        prePun.getPunishment().setActive( true );

        prePun.getPunishment().setStaffUuid( "CONSOLE" );
        if ( command.getSender() instanceof Player ) {
            Player staff = ( Player ) command.getSender();
            prePun.getPunishment().setStaff( staff );

            if ( Utils.checkStaffPunishmentAllowable( staff, target ) == false ) {
                CommandErrors.sendPlayerCannotBePunished( staff, target.getName() );
                return true;
            }
        }

        prePun.executePunishment();
        return true;
    }
}