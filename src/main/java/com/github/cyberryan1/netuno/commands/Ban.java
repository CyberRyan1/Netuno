package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.helpers.command.ArgType;
import com.github.cyberryan1.cybercore.helpers.command.CyberCommand;
import com.github.cyberryan1.cybercore.utils.VaultUtils;
import com.github.cyberryan1.netuno.classes.PrePunishment;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.Time;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Ban extends CyberCommand {

    public Ban() {
        super(
                "ban",
                Settings.BAN_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                "&8/&sban &p(player) (length/forever) (reason) [-s]"
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
    // /ban (player) (length/forever) (reason)
    public boolean execute( CommandSender sender, String args[] ) {
        if ( Time.isAllowableLength( args[1] ) == false ) {
            CommandErrors.sendInvalidTimespan( sender, args[1] );
            return true;
        }

        final OfflinePlayer target = Bukkit.getOfflinePlayer( args[0] );

        if ( Settings.BAN_MAX_TIME_ENABLED.bool() && VaultUtils.hasPerms( sender, Settings.BAN_MAX_TIME_BYPASS_PERMISSION.string() ) == false ) {
            long maxBanLength = Time.getTimestampFromLength( Settings.BAN_MAX_TIME_LENGTH.string() );
            long banLength = Time.getTimestampFromLength( args[1] );
            if ( maxBanLength < banLength ) {
                CommandErrors.sendBanLengthTooLarge( sender );
                return true;
            }
        }

        PrePunishment pun = new PrePunishment(
                target,
                "Ban",
                args[1],
                combineArgs( args, 2 )
        );

        pun.setConsoleSender( true );
        if ( sender instanceof Player ) {
            Player staff = ( Player ) sender;
            pun.setStaff( staff );
            pun.setConsoleSender( false );

            if ( Utils.checkStaffPunishmentAllowable( staff, target ) == false ) {
                CommandErrors.sendPlayerCannotBePunished( staff, target.getName() );
                return true;
            }
        }

        pun.executePunishment();
        return true;
    }
}