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

public class KickCommand extends CyberCommand {

    public KickCommand() {
        super(
                "kick",
                Settings.KICK_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.string(),
                "&8/&skick &p(player) (reason) [-s]"
        );
        register( true );

        demandPermission( true );
        setMinArgs( 2 );
        setArgType( 0, ArgType.ONLINE_PLAYER );
        setAsync( true );
    }

    @Override
    public List<String> tabComplete( CommandSender sender, String[] args ) {
        return List.of();
    }

    @Override
    // /kick (player) (reason)
    public boolean execute( CommandSender sender, String args[] ) {
        final OfflinePlayer target = Bukkit.getOfflinePlayer( args[0] );

        NetunoPrePunishment pun = new NetunoPrePunishment();
        pun.setPlayer( target );
        pun.setPunishmentType( PunishmentType.KICK );
        pun.setTimestamp( TimeUtils.getCurrentTimestamp() );
        pun.setReason( Utils.getRemainingArgs( args, 1 ) );
        pun.setLength( 0 );

        pun.setStaffUuid( "CONSOLE" );
        if ( sender instanceof Player ) {
            Player staff = ( Player ) sender;
            pun.setStaff( staff );

            if ( Utils.checkStaffPunishmentAllowable( staff, target ) == false ) {
                CommandErrors.sendPlayerCannotBePunished( staff, target.getName() );
                return true;
            }
        }

        pun.executePunishment();
        return true;
    }
}
