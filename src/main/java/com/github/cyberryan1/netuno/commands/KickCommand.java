package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.spigot.command.sent.SentCommand;
import com.github.cyberryan1.cybercore.spigot.command.settings.ArgType;
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

public class KickCommand extends HelpableCommand {

    public KickCommand( int helpOrder ) {
        super(
                "kick",
                Settings.KICK_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.string(),
                "&8/&skick &p(player) (reason) [-s]"
        );
        register( true );

        demandPermission( true );
        setMinArgLength( 2 );
        setArgType( 0, ArgType.ONLINE_PLAYER );
        setRunAsync( true );
    }

    @Override
    public List<String> tabComplete( SentCommand command ) {
        return List.of();
    }

    @Override
    // /kick (player) (reason)
    public boolean execute( SentCommand command ) {
        final OfflinePlayer target = command.getOfflinePlayerAtArg( 0 );

        NetunoPrePunishment prePun = new NetunoPrePunishment();
        prePun.getPunishment().setPlayer( target );
        prePun.getPunishment().setPunishmentType( PunishmentType.KICK );
        prePun.getPunishment().setTimestamp( TimeUtils.getCurrentTimestamp() );
        prePun.getPunishment().setReason( command.getCombinedArgs( 1 ) );
        prePun.getPunishment().setLength( 0 );

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
