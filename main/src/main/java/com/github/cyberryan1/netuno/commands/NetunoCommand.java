package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.cybercore.spigot.command.CyberCommand;
import com.github.cyberryan1.cybercore.spigot.command.CyberSubCommand;
import com.github.cyberryan1.cybercore.spigot.command.CyberSuperCommand;
import com.github.cyberryan1.cybercore.spigot.command.sent.SentCommand;
import com.github.cyberryan1.cybercore.spigot.command.settings.BaseCommand;
import com.github.cyberryan1.cybercore.spigot.utils.CyberColorUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberCommandUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberLogUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberVaultUtils;
import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.api.models.ApiReport;
import com.github.cyberryan1.netuno.debug.CacheDebugPrinter;
import com.github.cyberryan1.netuno.guis.punish.models.PunishSettings;
import com.github.cyberryan1.netuno.models.NetunoPlayer;
import com.github.cyberryan1.netuno.models.NetunoReport;
import com.github.cyberryan1.netuno.models.NetunoStaff;
import com.github.cyberryan1.netuno.models.commands.CommandHelpInfo;
import com.github.cyberryan1.netuno.services.NetunoService;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.UUID;

public class NetunoCommand extends CyberCommand {

    private static final int COMMANDS_PER_HELP_PAGE = 7;
    private static final String HELP_MESSAGE_BASE_MSG = "\n    <gold><b>Netuno</b></gold> <yellow>Command Help</yellow>";
    private static final String HELP_MESSAGE_COMMAND_USAGE_BASE_MSG = "<dark_gray>/</dark_gray><gray>{COMMAND_NAME}</gray> <gold>{COMMAND_ARGS}</gold>";
    private static final String HELP_MESSAGE_CHANGE_PAGE_BASE_MSG = "{PREVIOUS_PAGE}             {NEXT_PAGE}\n";
    private static final String HELP_MESSAGE_PREVIOUS_PAGE_MSG = "<hover:show_text:'<yellow>Previous Page</yellow>'><click:run_command:'/netuno help {PAGE_NUMBER}'><gray> << </gray> <yellow>Previous</click></hover>";
    private static final String HELP_MESSAGE_NEXT_PAGE_MSG = "<hover:show_text:'<yellow>Next Page</yellow>'><click:run_command:'/netuno help {PAGE_NUMBER}'><yellow>Next</yellow> <gray>>></click></hover>";

    public NetunoCommand() {
        super(
                "netuno",
                "&8/&snetuno"
        );

        register( true );
    }

    @Override
    public List<String> tabComplete( SentCommand command ) {
        if ( CyberVaultUtils.hasPerms( command.getSender(), Settings.STAFF_PERMISSION.string() ) == false ) return List.of();

        List<String> suggestions = List.of( "reload", "help", "debug" );
        if ( command.getArgs().length == 0 || command.getArg( 0 ).isEmpty() ) { return suggestions; }
        if ( command.getArgs().length == 1 ) { return CyberCommandUtils.matchArgs( suggestions, command.getArg( 0 ) ); }

        return List.of();
    }

    @Override
    public boolean execute( SentCommand command ) {
        final String versionString = CyberCore.getPlugin().getDescription().getVersion();

        // If player doesn't have staff perms, only send them an info message
        if ( CyberVaultUtils.hasPerms( command.getSender(), Settings.STAFF_PERMISSION.string() ) == false ) {
            Component parsed = MiniMessage.miniMessage().deserialize(
                    "<gold><b>Netuno</b></gold> <dark_gray>(</dark_gray><gray>" + versionString + "</gray><dark_gray>)</dark_gray> <yellow>Punishment Plugin</yellow>\n" +
                    "<dark_gray> > </dark_gray><yellow>Developed by</yellow> <light_purple>CyberRyan</light_purple>\n" +
                    "<dark_gray> > </dark_gray><hover:show_text:'<yellow>Download <gold>Netuno</gold></yellow>'><click:open_url:'https://www.spigotmc.org/resources/netuno.94864/'><yellow>Click <gold>here</gold> to <green>download</green></yellow></click></hover>\n" +
                    "<dark_gray> > </dark_gray><hover:show_text:'<yellow>Join the <aqua>discord</aqua></yellow>'><click:open_url:'https://discord.gg/8gxG4KfvBK'><yellow>Click <gold>here</gold> to join the <aqua>discord</aqua></yellow></click></hover>" );
            command.getPlayer().sendMessage( parsed );
            return true;
        }

        if ( command.getArgs().length > 0 ) {
            if ( command.getArg( 0 ).equalsIgnoreCase( "debug" ) ) {
                if ( CyberVaultUtils.hasPerms( command.getSender(), Settings.RELOAD_PERMISSION.string() ) == false ) {
                    command.respond( Settings.PERM_DENIED_MSG.coloredString() );
                    return true;
                }

                CyberLogUtils.logInfo( "Printing debug information..." );
                command.respond( "&sPrinting debug information..." );

                // Netuno player printer
                CyberLogUtils.logInfo( "Printing player debug information..." );
                CacheDebugPrinter<UUID, NetunoPlayer> playerPrinter = new CacheDebugPrinter<>();
                playerPrinter.setPrinterA( UUID::toString );
                playerPrinter.setPrinterB( NetunoService.DEBUG_PRINTER_NETUNOPLAYER );
                for ( NetunoPlayer player : Netuno.SERVICE.getAll() ) {
                    playerPrinter.getCache().put( player.getUuid(), player );
                }
                playerPrinter.printToFileWithPrefix( "players" );
                CyberLogUtils.logInfo( "Successfully printed debug information for NetunoPlayer" );

                // Netuno staff printer
                CyberLogUtils.logInfo( "Printing staff debug information..." );
                CacheDebugPrinter<UUID, NetunoStaff> staffPrinter = new CacheDebugPrinter<>();
                staffPrinter.setPrinterA( UUID::toString );
                staffPrinter.setPrinterB( staff -> "\tSign Notif. Status = " + ( staff.getSignNotificationStatus() ? "TRUE" : "FALSE" ) + "\n" );
                for ( NetunoStaff staff : Netuno.SERVICE.getAllStaff() ) {
                    staffPrinter.getCache().put( staff.getUuid(), staff );
                }
                staffPrinter.printToFileWithPrefix( "staff" );
                CyberLogUtils.logInfo( "Successfully printed debug information for NetunoStaff" );

                // Netuno report printer
                CyberLogUtils.logInfo( "Printing report debug information..." );
                CacheDebugPrinter<UUID, List<ApiReport>> reportPrinter = new CacheDebugPrinter<>();
                reportPrinter.setPrinterA( UUID::toString );
                reportPrinter.setPrinterB( reports -> {
                    String output = "\tReports (" + reports.size() + " total):\n";
                    for ( ApiReport report : reports ) {
                        output += "\tReport #" + report.getId() + "\n";
                        output += "\t\tPlayer = " + Bukkit.getOfflinePlayer( report.getPlayer() ).getName() + " (UUID \"" + report.getPlayer().toString() + "\")\n";
                        output += "\t\tAuthor = " + Bukkit.getOfflinePlayer( report.getReportAuthor() ).getName() + " (UUID \"" + report.getReportAuthor().toString() + "\")\n";
                        output += "\t\tReport Date = " + report.getReportDate() + "\n";
                        output += "\t\tReasons = " + String.join( NetunoReport.REASON_DELIMITER, report.getReasons() ) + "\n";
                    }
                    return output;
                } );
                reportPrinter.getCache().putAll( Netuno.REPORT_SERVICE.getCache() );
                reportPrinter.printToFileWithPrefix( "reports" );
                CyberLogUtils.logInfo( "Successfully printed debug information for NetunoReport" );

                // IP record printer
                CyberLogUtils.logInfo( "Printing IP record debug information..." );
                CacheDebugPrinter<UUID, List<String>> ipRecordPrinter = new CacheDebugPrinter<>();
                ipRecordPrinter.setPrinterA( UUID::toString );
                ipRecordPrinter.setPrinterB( list -> {
                    String output = "";
                    for ( String str : list ) output += "\t- " + str + "\n";
                    return output;
                } );
                ipRecordPrinter.getCache().putAll( Netuno.ALT_SERVICE.getAllPlayersJoinedIps() );
                ipRecordPrinter.printToFileWithPrefix( "ip_records" );
                CyberLogUtils.logInfo( "Successfully printed debug information for IP Records" );

                // Alt printer
                CyberLogUtils.logInfo( "Printing alt debug information..." );
                CacheDebugPrinter<UUID, List<UUID>> altPrinter = new CacheDebugPrinter<>();
                altPrinter.setPrinterA( UUID::toString );
                altPrinter.setPrinterB( list -> {
                    String output = "";
                    for ( UUID uuid : list ) output += "\t- " + Bukkit.getOfflinePlayer( uuid ).getName() + " (UUID \"" + uuid.toString() + "\")\n";
                    return output;
                } );
                for ( UUID uuid : Netuno.ALT_SERVICE.getAllPlayersJoinedIps().keySet() ) {
                    altPrinter.getCache().put( uuid, Netuno.ALT_SERVICE.getAlts( uuid ) );
                }
                altPrinter.printToFileWithPrefix( "alts" );
                CyberLogUtils.logInfo( "Successfully printed debug information for alts" );


                CyberLogUtils.logInfo( "Successfully printed all debug information" );
                command.respond( "&sSuccessfully printed debug information" );
                return true;
            }

            else if ( command.getArg( 0 ).equalsIgnoreCase( "help" ) ) {
                int startIndex = 0;
                if ( command.getArgs().length > 1 ) {
                    try {
                        startIndex = COMMANDS_PER_HELP_PAGE * ( Integer.parseInt( command.getArg( 1 ) ) - 1 );
                    } catch ( NumberFormatException ignore ) {}
                }
                int endIndex = startIndex + COMMANDS_PER_HELP_PAGE;

                final List<CommandHelpInfo> registry = CommandHelpInfo.getRegistry();

                String unparsedMsg = HELP_MESSAGE_BASE_MSG + "\n";
                for ( int index = startIndex; index < endIndex; index++ ) {
                    if ( index >= registry.size() ) break;

                    final CommandHelpInfo com = registry.get( index );
                    if ( com.getCommand().getUsage() == null ) continue;
                    String commandUsage = HELP_MESSAGE_COMMAND_USAGE_BASE_MSG + "\n";

                    // handling sub commands
                    if ( com.getCommand() instanceof CyberSubCommand ) {
                        CyberSubCommand subCommand = ( CyberSubCommand ) com.getCommand();
                        CyberSuperCommand superCommand = null;

                        // getting the super command of this sub command
                        for ( int i = 0; i < registry.size(); i++ ) {
                            final CommandHelpInfo currentHelpInfo =  registry.get( i );
                            if ( currentHelpInfo.getCommand() instanceof CyberSuperCommand == false ) continue;
                            CyberSuperCommand currentSuper = ( CyberSuperCommand ) currentHelpInfo.getCommand();
                            if ( currentSuper.getSubCommandList().contains( subCommand ) ) {
                                superCommand = currentSuper;
                            }
                            break;
                        }

                        if ( superCommand == null ) {
                            CyberLogUtils.logError( "[!] [!] [!] Could not find super command for sub command" );
                            throw new NullPointerException( "superCommand is null" );
                        }

                        commandUsage = commandUsage.replace( "{COMMAND_NAME}", superCommand.getName() );
                        commandUsage = commandUsage.replace( "{COMMAND_ARGS}", extractCommandHelpArgsOnly( com.getCommand() ) );
                    }

                    else {
                        commandUsage = commandUsage.replace( "{COMMAND_NAME}", com.getCommand().getName() );
                        commandUsage = commandUsage.replace( "{COMMAND_ARGS}", extractCommandHelpArgsOnly( com.getCommand() ) );
                    }

                    unparsedMsg += commandUsage;
                }

                unparsedMsg += getChangePageString( startIndex, registry );
                command.getPlayer().sendMessage( MiniMessage.miniMessage().deserialize( unparsedMsg ) );
                return true;
            }

            else if ( command.getArg( 0 ).equalsIgnoreCase( "reload" ) ) {
                if ( CyberVaultUtils.hasPerms( command.getSender(), Settings.RELOAD_PERMISSION.string() ) == false ) {
                    command.respond( Settings.PERM_DENIED_MSG.coloredString() );
                    return true;
                }

                // Temporarily disabling /netuno reload command until I figure out how to reload the config files correctly
                if ( 1 == 1 ) {
                    command.respond( "&cTemporarily disabled due to bugs. Please restart the server instead" );
                    return true;
                }

                CyberLogUtils.logInfo( "Reloading Netuno..." );
                command.respond( "&sReloading Netuno..." );

                YMLUtils.initializeConfigs();

                for ( Settings setting : Settings.values() ) {
                    setting.reload();
                }
                for ( PunishSettings ps : PunishSettings.values() ) {
                    ps.reload();
                }

                CyberLogUtils.logInfo( "Netuno reloaded!" );
                command.respond( "&sNetuno reloaded!" );
                return true;
            }
        }

        // Sending default info message and, since they are staff, also showing them
        //      how to get help
        Component parsed = MiniMessage.miniMessage().deserialize(
                "<gold><b>Netuno</b></gold> <dark_gray>(</dark_gray><gray>v0.1.2</gray><dark_gray>)</dark_gray> <yellow>Punishment Plugin</yellow>\n" +
                        "<dark_gray> > </dark_gray><yellow>Developed by</yellow> <light_purple>CyberRyan</light_purple>\n" +
                        "<dark_gray> > </dark_gray><hover:show_text:'<yellow>Download <gold>Netuno</gold></yellow>'><click:open_url:'https://www.spigotmc.org/resources/netuno.94864/'><yellow>Click <gold>here</gold> to <green>download</green></yellow></click></hover>\n" +
                        "<dark_gray> > </dark_gray><hover:show_text:'<yellow>Join the <aqua>discord</aqua></yellow>'><click:open_url:'https://discord.gg/8gxG4KfvBK'><yellow>Click <gold>here</gold> to join the <aqua>discord</aqua></yellow></click></hover>\n" +
                        "<hover:show_text:'<yellow>Click for help</yellow>'><click:run_command:'/netuno help 1'><yellow>For help with commands, click <gold>here</gold></yellow></click></hover>"
        );
        command.getPlayer().sendMessage( parsed );
        return true;
    }

    private String getChangePageString( int startIndex, List<CommandHelpInfo> registry ) {
        int currentPage = ( startIndex / COMMANDS_PER_HELP_PAGE ) + 1;
        int maxPage = ( int ) Math.ceil( registry.size() * 1.0 / COMMANDS_PER_HELP_PAGE );

        String changePageLine = HELP_MESSAGE_CHANGE_PAGE_BASE_MSG;
        if ( currentPage == 1 ) {
            String replacePreviousPageWithSpaces = " ".repeat( "{PREVIOUS_PAGE}".length() );
            changePageLine = changePageLine.replace( "{PREVIOUS_PAGE}", replacePreviousPageWithSpaces );
        }
        else if ( currentPage == maxPage ) {
            String replaceNextPageWithSpaces = " ".repeat( "{NEXT_PAGE}".length() );
            changePageLine = changePageLine.replace( "{NEXT_PAGE}", replaceNextPageWithSpaces );
        }

        String previousPage = HELP_MESSAGE_PREVIOUS_PAGE_MSG.replace( "{PAGE_NUMBER}", "" + ( currentPage - 1 ) );
        String nextPage = HELP_MESSAGE_NEXT_PAGE_MSG.replace( "{PAGE_NUMBER}", "" + ( currentPage + 1 ) );
        changePageLine = changePageLine.replace( "{PREVIOUS_PAGE}", previousPage )
                .replace( "{NEXT_PAGE}", nextPage );
        return changePageLine;
    }

    private String extractCommandHelpArgsOnly( BaseCommand command ) {
        String usage = CyberColorUtils.reverseColor( command.getUsage() );
        int startIndex2 = usage.indexOf( " " ); // gets the index of the first space -- everything after that should be the args
        usage = usage.substring( startIndex2 + 1 );
        return CyberColorUtils.deleteColor( CyberColorUtils.getColored( usage ) );
    }
}