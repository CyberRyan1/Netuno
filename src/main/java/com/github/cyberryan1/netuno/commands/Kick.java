package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.netuno.classes.BaseCommand;
import com.github.cyberryan1.netuno.classes.PrePunishment;
import com.github.cyberryan1.netuno.utils.*;
import com.github.cyberryan1.netuno.utils.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class Kick extends BaseCommand {

    private final Database DATA = Utils.getDatabase();

    public Kick() {
        super( "kick", ConfigUtils.getStr( "kick.perm" ), ConfigUtils.getColoredStr( "general.perm-denied-msg" ), getColorizedStr( "&8/&ukick &y(player) (reason) [-s]" ) );
    }

    @Override
    public List<String> onTabComplete( CommandSender sender, Command command, String label, String[] args ) {
        if ( permissionsAllowed( sender ) ) {
            if ( args.length == 0 || args[0].length() == 0 ) {
                return getAllOnlinePlayerNames();
            }
            else if ( args.length == 1 ) {
                return matchOnlinePlayers( args[0] );
            }
        }
        return Collections.emptyList();
    }

    @Override
    // /kick (player) (reason)
    public boolean onCommand( CommandSender sender, Command command, String label, String args[] ) {
        if ( VaultUtils.hasPerms( sender, ConfigUtils.getStr( "kick.perm" ) ) ) {
            if ( Utils.isOutOfBounds( args, 1 ) == false ) {

                if ( Utils.isValidUsername( args[0] ) == false ) {
                    CommandErrors.sendPlayerNotFound( sender, args[0] );
                    return true;
                }

                Player target = Bukkit.getServer().getPlayer( args[0] );
                if ( target != null ) {
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
                }

                else {
                    CommandErrors.sendPlayerNotFound( sender, args[0] );
                }
            }

            else {
                CommandErrors.sendCommandUsage( sender, "kick" );
            }
        }

        else {
            CommandErrors.sendInvalidPerms( sender );
        }


        return true;
    }
}
