package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.netuno.classes.IPPunishment;
import com.github.cyberryan1.netuno.classes.PrePunishment;
import com.github.cyberryan1.netuno.utils.*;
import com.github.cyberryan1.netuno.utils.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class IPBan implements CommandExecutor {

    private final Database DATA = Utils.getDatabase();

    @Override
    // /ipban (player) (length/forever) (reason)
    public boolean onCommand( CommandSender sender, Command command, String label, String args[] ) {

        if ( VaultUtils.hasPerms( sender, ConfigUtils.getStr( "ipban.perm" ) ) == false ) {
            CommandErrors.sendInvalidPerms( sender );
            return true;
        }

        if ( Utils.isOutOfBounds( args, 2 ) == false ) {

            if ( Utils.isValidUsername( args[0] ) == false ) {
                CommandErrors.sendPlayerNotFound( sender, args[0] );
                return true;
            }

            if ( Time.isAllowableLength( args[1] ) ) {
                OfflinePlayer target = Bukkit.getServer().getOfflinePlayer( args[0] );
                if ( target != null ) {
                    PrePunishment pun = new PrePunishment(
                            target,
                            "IPBan",
                            args[1],
                            Utils.getRemainingArgs( args, 2 )
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
                }

                else {
                    CommandErrors.sendPlayerNotFound( sender, args[0] );
                }

            }

            else {
                CommandErrors.sendInvalidTimespan( sender, args[1] );
            }

        }

        else {
            CommandErrors.sendCommandUsage( sender, "ipban" );
        }

        return true;
    }
}
