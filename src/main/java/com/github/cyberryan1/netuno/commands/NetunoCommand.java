package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.cybercore.spigot.command.CyberCommand;
import com.github.cyberryan1.cybercore.spigot.command.sent.SentCommand;
import com.github.cyberryan1.cybercore.spigot.command.settings.BaseCommand;
import com.github.cyberryan1.cybercore.spigot.utils.CyberColorUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberCommandUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberVaultUtils;
import com.github.cyberryan1.netuno.models.commands.CommandHelpInfo;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.List;

public class NetunoCommand extends CyberCommand {

    private static final int COMMAND_PER_HELP_PAGE = 7;
    private static final String HELP_MESSAGE_BASE_MSG = "    <gold><b>Netuno</b></gold> <yellow>Command Help</yellow>";
    private static final String HELP_MESSAGE_COMMAND_USAGE_BASE_MSG = "<dark_gray>/</dark_gray><gray>{COMMAND_NAME}</gray> <gold>{COMMAND_ARGS}</gold>";
    private static final String HELP_MESSAGE_CHANGE_PAGE_BASE_MSG = "{PREVIOUS_PAGE}             {NEXT_PAGE}";
    private static final String HELP_MESSAGE_PREVIOUS_PAGE_MSG = "<click:run_command:'/netuno help {PAGE_NUMBER}'><gray> << </gray> <yellow>Previous</click>";
    private static final String HELP_MESSAGE_NEXT_PAGE_MSG = "<click:run_command:'/netuno help {PAGE_NUMBER}'>Next <gray>>></click>";

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
                // TODO
                return true;
            }

            else if ( command.getArg( 0 ).equalsIgnoreCase( "help" ) ) {
                int startIndex = 0;
                if ( command.getArgs().length > 1 ) {
                    try {
                        startIndex = Integer.parseInt( command.getArg( 1 ) );
                    } catch ( NumberFormatException ignore ) {}
                }
                int endIndex = startIndex + COMMAND_PER_HELP_PAGE;

                final List<CommandHelpInfo> registry = CommandHelpInfo.getRegistry();

                String unparsedMsg = HELP_MESSAGE_BASE_MSG + "\n";
                for ( int index = startIndex; index < endIndex; index++ ) {
                    if ( index >= registry.size() ) break;

                    final CommandHelpInfo com = registry.get( index );
                    String commandUsage = HELP_MESSAGE_COMMAND_USAGE_BASE_MSG + "\n";
                    commandUsage = commandUsage.replace( "{COMMAND_NAME}", com.getCommand().getName() );
                    commandUsage = commandUsage.replace( "{COMMAND_ARGS}", extractCommandHelpArgsOnly( com.getCommand() ) );
                    unparsedMsg += commandUsage + "\n";
                }

                int currentPage = startIndex / COMMAND_PER_HELP_PAGE;
                int maxPage = ( int ) Math.ceil( registry.size() * 1.0 / COMMAND_PER_HELP_PAGE );

                String changePageLine = HELP_MESSAGE_CHANGE_PAGE_BASE_MSG + "";
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

                unparsedMsg += changePageLine;
                command.getPlayer().sendMessage( MiniMessage.miniMessage().deserialize( unparsedMsg ) );
                return true;
            }

            else if ( command.getArg( 0 ).equalsIgnoreCase( "reload" ) ) {
                // TODO
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
                        "<click:run_command:'/netuno help 1'><yellow>For help with commands, click <gold>here</gold></yellow></click>"
        );
        command.getPlayer().sendMessage( parsed );
        return true;
    }

    private String extractCommandHelpArgsOnly( BaseCommand command ) {
        String usage = command.getUsage();
        int startIndex = 1 + command.getName().length() + 1; // 1 is the slash and the rest is from the name and another 1 is from the space
        usage = usage.substring( startIndex );
        // Removing the color and returning that
        return CyberColorUtils.deleteColor( usage );
    }
}