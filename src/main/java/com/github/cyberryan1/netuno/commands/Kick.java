package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.helpers.command.CyberCommand;
import com.github.cyberryan1.netuno.classes.PrePunishment;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Kick extends CyberCommand {

    public Kick() {
        super(
                "kick",
                Settings.KICK_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.string(),
                "&8/&skick &p(player) (reason) [-s]"
        );
    }

    @Override
    public List<String> tabComplete( CommandSender sender, String[] args ) {
        return List.of();
    }

    @Override
    // /kick (player) (reason)
    public boolean execute( CommandSender sender, String args[] ) {
        final OfflinePlayer target = Bukkit.getOfflinePlayer( args[0] );
        PrePunishment pun = new PrePunishment(
                target,
                "Kick",
                Utils.getRemainingArgs( args, 1 )
        );

        pun.setConsoleSender( true );
        if ( sender instanceof Player ) {
            Player staff = ( Player ) sender;
            pun.setStaff( staff );
            pun.setConsoleSender( false );

            if ( Utils.checkStaffPunishmentAllowable( staff, target ) == false ) {
                CommandErrors.sendPlayerCannotBePunished( sender, target.getName() );
                return true;
            }
        }

        pun.executePunishment();
        return true;
    }
}
