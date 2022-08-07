package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.helpers.command.ArgType;
import com.github.cyberryan1.cybercore.helpers.command.CyberCommand;
import com.github.cyberryan1.netuno.apimplement.models.punishments.NetunoPrePunishment;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netunoapi.models.punishments.PunishmentType;
import com.github.cyberryan1.netunoapi.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class IpMuteCommand extends CyberCommand {

    public IpMuteCommand() {
        super(
                "ipmute",
                Settings.IPMUTE_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.string(),
                "&8/&sipmute &p(player) (length/forever) (reason) [-s]"
        );
        register( true );

        demandPermission( true );
        setMinArgs( 3 );
        setArgType( 0, ArgType.OFFLINE_PLAYER );
        setAsync( true );
    }

    @Override
    public List<String> tabComplete( CommandSender sender, String[] args ) {
        if ( permissionsAllowed( sender ) ) {
            List<String> suggestionTimes = List.of( "15m", "1h", "12h", "1d", "3d", "1w", "forever" );
            if ( args.length <= 1 ) { return List.of(); }
            else if ( args[1].length() == 0 ) { return suggestionTimes; }
            else if ( args.length == 2 ) { return matchArgs( suggestionTimes, args[1] ); }
        }

        return List.of();
    }

    @Override
    // /ipmute (player) (length/forever) (reason)
    public boolean execute( CommandSender sender, String args[] ) {
        if ( TimeUtils.isAllowableLength( args[1] ) == false ) {
            CommandErrors.sendInvalidTimespan( sender, args[1] );
            return true;
        }

        final OfflinePlayer target = Bukkit.getOfflinePlayer( args[0] );

        NetunoPrePunishment prePun = new NetunoPrePunishment();
        prePun.getPunishment().setPlayer( target );
        prePun.getPunishment().setPunishmentType( PunishmentType.IPMUTE );
        prePun.getPunishment().setTimestamp( TimeUtils.getCurrentTimestamp() );
        prePun.getPunishment().setReason( Utils.getRemainingArgs( args, 2 ) );
        prePun.getPunishment().setLength( TimeUtils.durationFromUnformatted( args[1] ).timestamp() );
        prePun.getPunishment().setActive( true );

        prePun.getPunishment().setStaffUuid( "CONSOLE" );
        if ( sender instanceof Player ) {
            Player staff = ( Player ) sender;
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
