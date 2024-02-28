package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.spigot.command.CyberCommand;
import com.github.cyberryan1.cybercore.spigot.command.sent.SentCommand;
import com.github.cyberryan1.cybercore.spigot.command.settings.BaseCommand;
import com.github.cyberryan1.cybercore.spigot.utils.*;
import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.apimplement.ApiNetuno;
import com.github.cyberryan1.netuno.debug.DebugInfo;
import com.github.cyberryan1.netuno.debug.NetunoDebugger;
import com.github.cyberryan1.netuno.guis.punish.utils.PunishSettings;
import com.github.cyberryan1.netuno.models.commands.GenericHelpableCommand;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class NetunoCommand extends CyberCommand {

    private final List<String> HELP_COMMAND_ORDER = new ArrayList<>();

    public NetunoCommand() {
        super(
                "netuno",
                Settings.STAFF_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.string(),
                "&8/&snetuno &p[reload/help] [page]"
        );
        register( true );

        demandPermission( true );
        setMinArgLength( 0 );
    }

    @Override
    public List<String> tabComplete( SentCommand command ) {
        List<String> suggestions = List.of( "reload", "help" );
        if ( command.getArgs().length == 0 || command.getArg( 0 ).length() == 0 ) { return suggestions; }
        if ( command.getArgs().length == 1 ) { return CyberCommandUtils.matchArgs( suggestions, command.getArg( 0 ) ); }

        return List.of();
    }

    @Override
    public boolean execute( SentCommand command ) {
        if ( command.getArgs().length == 0 || command.getArg( 0 ).equalsIgnoreCase( "help" ) ) {
            if ( HELP_COMMAND_ORDER.isEmpty() ) { initiateHelpMessage(); }
            
            if ( command.getArgs().length > 1 ) {
                int page = command.getIntegerAtArg( 1 );

                if ( page >= 0 && page < Math.ceil( Netuno.registeredCommands.size() / 6.0 ) ) {
                    sendHelpMessage( command.getSender(), page * 6, page * 6 + 6 );
                }

                else {
                    CyberMsgUtils.sendMsg( command.getSender(), "&sInvalid page number!" );
                }
            }

            else {
                sendHelpMessage( command.getSender(), 0, 6 );
            }
        }

        else if ( command.getArg( 0 ).equalsIgnoreCase( "reload" ) ) {
            if ( CyberVaultUtils.hasPerms( command.getSender(), Settings.RELOAD_PERMISSION.string() ) ) {
                CyberMsgUtils.sendMsg( command.getSender(), "&7Attempting to reload &6Netuno&7..." );
                CyberLogUtils.logInfo( "Attempting to reload Netuno..." );

                YMLUtils.initializeConfigs();

                CyberLogUtils.logInfo( "Reloading all settings from the config files..." );
                for ( Settings setting : Settings.values() ) { setting.reload(); }
                for ( PunishSettings setting : PunishSettings.values() ) { setting.reload(); }
                CyberLogUtils.logInfo( "Reloaded " + ( Settings.values().length + PunishSettings.values().length ) + " settings" );

                CyberColorUtils.setPrimaryColor( Settings.PRIMARY_COLOR.string() );
                CyberColorUtils.setSecondaryColor( Settings.SECONDARY_COLOR.string() );

                ApiNetuno.getData().getNetunoReports().reloadSettings();

                CyberMsgUtils.sendMsg( command.getSender(), "&7Successfully reloaded &6Netuno" );
                CyberLogUtils.logInfo( "Successfully reloaded Netuno and its files" );
            }

            else {
                sendPermissionMsg( command.getSender() );
            }
        }

        else if ( command.getArg( 0 ).equalsIgnoreCase( "debug" ) ) {
            if ( CyberVaultUtils.hasPerms( command.getSender(), Settings.RELOAD_PERMISSION.string() ) ) {
                if ( command.getArgs().length <= 1 || ( List.of( "all", "players", "alts", "reports" ).contains( command.getArg( 1 ) ) == false ) ) {
                    CyberMsgUtils.sendMsg( command.getSender(), "&8/&snetuno &pdebug (all/players/alts/reports)" );
                    return true;
                }

                final DebugInfo debugInfo = DebugInfo.valueOf( command.getArg( 1 ).toUpperCase() );
                CyberMsgUtils.sendMsg( command.getSender(), "&7Sending debug messages with level " + debugInfo.name().toLowerCase() + "..." );

                NetunoDebugger debugger = new NetunoDebugger( debugInfo );
                debugger.start();

                CyberMsgUtils.sendMsg( command.getSender(), "&7Finished sending debug messages" );
            }

            else {
                sendPermissionMsg( command.getSender() );
            }
        }

        else {
            sendHelpMessage( command.getSender(), 0, 6 );
        }

        return true;
    }

    private void sendHelpMessage( CommandSender sender, int start, int end ) {
        String toSend = "\n";
        for ( int index = start; index < end; index++ ) {
            toSend += HELP_COMMAND_ORDER.get( index ) + "\n";
        }
        toSend += "\n";
        CyberMsgUtils.sendMsg( sender, toSend );
    }
    
    private void initiateHelpMessage() {
        HELP_COMMAND_ORDER.clear();
        HELP_COMMAND_ORDER.add( this.getUsage() ); // This command has the highest priority
        
        List<GenericHelpableCommand> order = new ArrayList<>();
        for ( BaseCommand cmd : Netuno.registeredCommands ) {
            if ( cmd instanceof GenericHelpableCommand == false ) { continue; }
            if ( ( ( GenericHelpableCommand ) cmd ).getHelpOrder() == -1 ) { continue; }
            order.add( ( GenericHelpableCommand ) cmd );
        }
        
        order.sort( ( h1, h2 ) -> h2.getHelpOrder() - h1.getHelpOrder() );
        for ( GenericHelpableCommand command : order ) {
            HELP_COMMAND_ORDER.add( CyberColorUtils.getColored( command.getHelpMsg() ) );
        }
    }
}
