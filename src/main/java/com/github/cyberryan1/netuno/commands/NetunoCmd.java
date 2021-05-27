package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.ConfigUtils;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.VaultUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class NetunoCmd implements CommandExecutor {

    private final String helpMsg[] = { "\n", CommandErrors.getCommandUsage( "warn" ), CommandErrors.getCommandUsage( "kick" ),
            CommandErrors.getCommandUsage( "mute" ), CommandErrors.getCommandUsage( "unmute" ), CommandErrors.getCommandUsage( "ban" ),
            CommandErrors.getCommandUsage( "unban" ), Utils.getColored( "&8/&6netuno &7reload" ), "\n"};

    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String args[] ) {

        if ( VaultUtils.hasPerms( sender, ConfigUtils.getStr( "general.staff-perm" ) ) == false ) {
            CommandErrors.sendInvalidPerms( sender );
            return true;
        }

        if ( Utils.isOutOfBounds( args, 0 ) == false ) {
            if ( args[0].equalsIgnoreCase( "reload" ) ) {
                if ( VaultUtils.hasPerms( sender, ConfigUtils.getStr( "general.reload-perm" ) ) ) {
                    sender.sendMessage( Utils.getColored( "&7Attempting to reload &6Netuno&7..." ) );
                    Utils.logInfo( "Attempting to reload Netuno..." );

                    if ( ConfigUtils.getConfigManager().getConfigFile().exists() == false || ConfigUtils.getConfigManager().getConfigFile() == null ) {
                        Utils.logInfo( "Found no config file, recreating it..." );
                        ConfigUtils.getConfigManager().saveDefaultConfig();
                    }

                    ConfigUtils.getConfigManager().reloadConfig();

                    sender.sendMessage( Utils.getColored( "&7Successfully reloaded &6Netuno" ) );
                    Utils.logInfo( "Successfully reloaded Netuno and its files" );
                }

                else {
                    sender.sendMessage( ConfigUtils.getColoredStr( "perm-denied.msg" ) );
                }
            }

            else if ( args[0].equalsIgnoreCase( "help" ) ) {
                for ( String str : helpMsg ) {
                    sender.sendMessage( str );
                }
            }

            else {
                for ( String str : helpMsg ) {
                    sender.sendMessage( str );
                }
            }
        }

        else {
            for ( String str : helpMsg ) {
                sender.sendMessage( str );
            }
        }

        return true;
    }
}
