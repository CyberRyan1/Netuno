package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.helpers.command.CyberCommand;
import com.github.cyberryan1.cybercore.utils.CoreUtils;
import com.github.cyberryan1.cybercore.utils.VaultUtils;
import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import org.bukkit.command.CommandSender;

import java.util.List;

public class NetunoCmd extends CyberCommand {

    private final String COMMAND_ORDER[] = { "help", "punish", "warn", "clearchat", "kick", "mute", "unmute", "history", "ban", "unban",
            "ipinfo", "ipmute", "unipmute", "ipban", "unipban", "report", "reports", "togglesigns", "reload" };

    public NetunoCmd() {
        super(
                "netuno",
                Settings.STAFF_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.string(),
                "netuno [reload/help] [page]"
        );
        register( true );

        demandPermission( true );
    }

    @Override
    public List<String> tabComplete( CommandSender sender, String[] args ) {
        if ( permissionsAllowed( sender ) ) {
            List<String> suggestions = List.of( "reload", "help" );
            if ( args.length == 0 || args[0].length() == 0 ) { return suggestions; }
            if ( args.length == 1 ) { return matchArgs( suggestions, args[0] ); }
        }

        return List.of();
    }

    @Override
    public boolean execute( CommandSender sender, String args[] ) {
        if ( args.length == 0 || args[0].equalsIgnoreCase( "help" ) ) {
            if ( args.length > 1 ) {
                int page = 0;
                try { page = Integer.parseInt( args[1] ) - 1; }
                catch ( NumberFormatException e ) { CoreUtils.sendMsg( sender, "&sInvalid page number!" ); }

                if ( page >= 0 && page < Math.ceil( Netuno.registeredCommands.size() / 6.0 ) ) {
                    sendHelpMessage( sender, page * 6, page * 6 + 6 );
                }

                else {
                    CoreUtils.sendMsg( sender, "&sInvalid page number!" );
                }
            }

            else {
                sendHelpMessage( sender, 0, 6 );
            }
        }

        else if ( args[0].equalsIgnoreCase( "reload" ) ) {
            if ( VaultUtils.hasPerms( sender, Settings.RELOAD_PERMISSION.string() ) ) {
                CoreUtils.sendMsg( sender, "&7Attempting to reload &6Netuno&7..." );
                CoreUtils.logInfo( "Attempting to reload Netuno..." );

                YMLUtils.getConfig().getYMLManager().initialize();

                CoreUtils.sendMsg( sender, "&7Successfully reloaded &6Netuno" );
                CoreUtils.logInfo( "Successfully reloaded Netuno and its files" );
            }

            else {
                sendPermissionMsg( sender );
            }
        }

        else {
            sendHelpMessage( sender, 0, 6 );
        }

        return true;
    }

    private void sendHelpMessage( CommandSender sender, int start, int end ) {
        String toSend = "\n";
        for ( int index = start; index < end; index++ ) {
            toSend += Netuno.registeredCommands.get( index ).getUsage() + "\n";
        }
        toSend += "\n";
        Utils.sendAnyMsg( sender, toSend );
    }
}
