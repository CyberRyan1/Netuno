package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.netuno.classes.BaseCommand;
import com.github.cyberryan1.netuno.guis.punish.MainPunishGUI;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.VaultUtils;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class Punish extends BaseCommand {

    public Punish() {
        super( "punish", YMLUtils.getConfig().getStr( "punish.perm" ), getColorizedStr( "&8/&upunish &y(player)") );
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
    // /punish (player)
    public boolean onCommand( CommandSender sender, Command command, String label, String args[] ) {

        if ( VaultUtils.hasPerms( sender, YMLUtils.getConfig().getStr( "punish.perm" ) ) == false ) {
            CommandErrors.sendInvalidPerms( sender );
            return true;
        }

        if ( sender instanceof Player == false ) {
            CommandErrors.sendCanOnlyBeRanByPlayer( sender );
            return true;
        }
        Player staff = ( Player ) sender;

        if ( Utils.isOutOfBounds( args, 0 ) == false ) {

            if ( Utils.isValidUsername( args[0] ) == false ) {
                CommandErrors.sendPlayerNotFound( sender, args[0] );
                return true;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer( args[0] );
            if ( target != null ) {

                if ( YMLUtils.getConfig().getBool( "punish.staff-punish" ) == false ) {
                    if ( Utils.checkStaffPunishmentAllowable( staff, target ) == false ) {
                        CommandErrors.sendPlayerCannotBePunished( staff, target.getName() );
                        return true;
                    }
                }

                MainPunishGUI gui = new MainPunishGUI( staff, target );
                gui.openInventory();
            }

            else {
                CommandErrors.sendPlayerNotFound( sender, args[0] );
            }
        }

        else {
            CommandErrors.sendCommandUsage( sender, "punish" );
        }

        return true;
    }
}
