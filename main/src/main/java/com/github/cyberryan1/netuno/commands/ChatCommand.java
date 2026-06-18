package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.spigot.command.CyberSubCommand;
import com.github.cyberryan1.cybercore.spigot.command.CyberSuperCommand;
import com.github.cyberryan1.cybercore.spigot.command.sent.SentCommand;
import com.github.cyberryan1.cybercore.spigot.command.sent.SentSubCommand;
import com.github.cyberryan1.cybercore.spigot.command.settings.ArgType;
import com.github.cyberryan1.cybercore.spigot.utils.CyberMsgUtils;
import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.models.commands.CommandHelpInfo;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class ChatCommand extends CyberSuperCommand {

    public ChatCommand( int helpOrder ) {
        super(
                "chat",
                Settings.CHAT_COMMAND_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                null
        );
        register( true );
        new CommandHelpInfo( this, helpOrder );

        ChatInfoSubcommand info = new ChatInfoSubcommand();
        ChatMuteSubcommand mute = new ChatMuteSubcommand();
        ChatUnmuteSubcommand unmute = new ChatUnmuteSubcommand();
        ChatClearSubcommand clear = new ChatClearSubcommand();
        ChatSlowSubcommand slow = new ChatSlowSubcommand();
        new CommandHelpInfo( info, helpOrder + 1 );
        new CommandHelpInfo( mute, helpOrder + 2 );
        new CommandHelpInfo( unmute, helpOrder + 3 );
        new CommandHelpInfo( clear, helpOrder + 4 );
        new CommandHelpInfo( slow, helpOrder + 5 );

        addSubCommand( info );
        addSubCommand( mute );
        addSubCommand( unmute );
        addSubCommand( clear );
        addSubCommand( slow );

        setDemandPermission( true );
        setMinArgLength( 1 );
    }


    @Override
    public List<String> tabComplete( SentCommand sentCommand ) {
        return List.of();
    }

    @Override
    public boolean execute( SentCommand sentCommand ) {
        return true;
    }
}

class ChatInfoSubcommand extends CyberSubCommand {

    public ChatInfoSubcommand() {
        super(
                "info",
                Settings.CHAT_COMMAND_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                "&8/&schat &pinfo"
        );

        setDemandPermission( true );
    }

    @Override
    public List<String> tabComplete( SentCommand command, SentSubCommand subcommand ) {
        return List.of();
    }

    @Override
    public boolean execute( SentCommand command, SentSubCommand subcommand ) {
        String chatStatus = Netuno.CHAT_SERVICE.isChatDisabled() ? "&cdisabled" : "&aenabled";
        command.respond( "&sChat Status:" );
        command.respond( "&s- Chat is currently " + chatStatus );

        int slowdown = Netuno.CHAT_SERVICE.getChatSlowdown();
        String unit = slowdown == 1 ? "second" : "seconds";
        command.respond( "&s- Chat slow is set to &p" + slowdown + " " + unit );
        return true;
    }
}

class ChatMuteSubcommand extends CyberSubCommand {

    public ChatMuteSubcommand() {
        super(
                "mute",
                Settings.MUTECHAT_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                "&8/&schat &pmute"
        );

        setDemandPermission( true );
    }

    @Override
    public List<String> tabComplete( SentCommand command, SentSubCommand subcommand ) {
        return List.of();
    }

    @Override
    public boolean execute( SentCommand command, SentSubCommand subcommand ) {
        if ( Netuno.CHAT_SERVICE.isChatDisabled() ) {
            command.respond( "&sChat is already disabled." );
            return true;
        }

        Netuno.CHAT_SERVICE.setChatDisabled( true );
        String msgs[] = Settings.MUTECHAT_CHAT_DISABLE_BROADCAST.coloredStringlist();
        CyberMsgUtils.broadcast( Arrays.asList( msgs ) );
        return true;
    }
}

class ChatUnmuteSubcommand extends CyberSubCommand {

    public ChatUnmuteSubcommand() {
        super(
                "unmute",
                Settings.MUTECHAT_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                "&8/&schat &punmute"
        );

        setDemandPermission( true );
    }

    @Override
    public List<String> tabComplete( SentCommand command, SentSubCommand subcommand ) {
        return List.of();
    }

    @Override
    public boolean execute( SentCommand command, SentSubCommand subcommand ) {
        if ( !Netuno.CHAT_SERVICE.isChatDisabled() ) {
            command.respond( "&sChat is already enabled." );
            return true;
        }

        Netuno.CHAT_SERVICE.setChatDisabled( false );
        String msgs[] = Settings.MUTECHAT_CHAT_ENABLE_BROADCAST.coloredStringlist();
        CyberMsgUtils.broadcast( Arrays.asList( msgs ) );
        return true;
    }
}

class ChatClearSubcommand extends CyberSubCommand {

    public ChatClearSubcommand() {
        super(
                "clear",
                Settings.CLEARCHAT_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                "&8/&schat &pclear"
        );

        setDemandPermission( true );
    }

    @Override
    public List<String> tabComplete( SentCommand command, SentSubCommand subcommand ) {
        return List.of();
    }

    @Override
    public boolean execute(SentCommand command, SentSubCommand subcommand) {
        String staffName = (command.getSender() instanceof ConsoleCommandSender)
                ? "CONSOLE"
                : command.getPlayer().getName();

        String playerMsg = Settings.CLEARCHAT_BROADCAST.coloredString();
        String staffMsg = Settings.CLEARCHAT_STAFF_BROADCAST
                .coloredString()
                .replace("[STAFF]", staffName);

        String staffPerm = Settings.STAFF_PERMISSION.string();
        boolean bypass = Settings.CLEARCHAT_STAFF_BYPASS.bool();

        for (Player player : Bukkit.getOnlinePlayers()) {

            boolean isStaff = player.hasPermission(staffPerm);

            if (!isStaff || !bypass) {
                for (int i = 0; i < 300; i++) {
                    CyberMsgUtils.sendMsg(player, "&r");
                }
            }

            CyberMsgUtils.sendMsg(player, isStaff ? staffMsg : playerMsg);
        }

        return true;
    }
}

class ChatSlowSubcommand extends CyberSubCommand {

    public ChatSlowSubcommand() {
        super(
                "slow",
                Settings.CHATSLOW_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                "&8/&schat &pslow (length)"
        );

        setDemandPermission( true );
        setMinArgLength( 1 );
        setArgType( 0, ArgType.INTEGER );
    }

    @Override
    public List<String> tabComplete( SentCommand command, SentSubCommand subcommand ) {
        return List.of();
    }

    @Override
    public boolean execute( SentCommand command, SentSubCommand subcommand ) {
        int length = subcommand.getIntegerAtArg( 0 );
        if ( length < 0 ) {
            command.respond( "&sThe length must be a positive number." );
            return true;
        }

        Netuno.CHAT_SERVICE.setChatSlowdown( length );
        String unit = ( length == 1 ) ? "second" : "seconds";
        command.respond( "&sChat will be slowed down to &p" + length + " " + unit + "&s." );

        if ( Settings.CHATSLOW_BROADCAST.string().isBlank() == false ) {
            CyberMsgUtils.broadcast( Settings.CHATSLOW_BROADCAST.coloredString().replace( "[AMOUNT]", String.valueOf( length ) ) );
        }

        return true;
    }
}

