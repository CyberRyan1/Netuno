package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.spigot.command.CyberSubCommand;
import com.github.cyberryan1.cybercore.spigot.command.sent.SentCommand;
import com.github.cyberryan1.cybercore.spigot.command.sent.SentSubCommand;
import com.github.cyberryan1.cybercore.spigot.command.settings.ArgType;
import com.github.cyberryan1.cybercore.spigot.utils.CyberMsgUtils;
import com.github.cyberryan1.netuno.apimplement.ApiNetuno;
import com.github.cyberryan1.netuno.apimplement.models.players.NetunoPlayerCache;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netunoapi.events.history.NetunoHistoryResetEvent;
import com.github.cyberryan1.netunoapi.models.players.NPlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class HistoryResetSubcommand extends CyberSubCommand {

    public HistoryResetSubcommand() {
        super(
                "reset",
                Settings.HISTORY_RESET_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.string(),
                "&8/&shistory &preset (player)"
        );

        setDemandPermission( true );
        setMinArgLength( 1 );
        setArgType( 0, ArgType.OFFLINE_PLAYER );
    }

    @Override
    public List<String> tabComplete( SentCommand command, SentSubCommand subCommand ) {
        return List.of();
    }

    @Override
    public boolean execute( SentCommand command, SentSubCommand subCommand ) {
        final Player player = subCommand.getPlayer();
        final NPlayer target = NetunoPlayerCache.forceLoad( subCommand.getOfflinePlayerAtArg( 0 ) );

        CyberMsgUtils.sendMsg( player, "&sDeleting all punishments for &p" + target.getPlayer().getName() + "&s..." );

        ApiNetuno.getData().getNetunoPuns().removePunishments( target.getPlayer() );

        String plural = ( target.getPunishments().size() == 1 ) ? ( "punishment" ) : ( "punishments" );
        CyberMsgUtils.sendMsg( player, "&sSuccessfully deleted &p" + target.getPunishments().size() + " &s"
                + plural + " from &p" + target.getPlayer().getName() + "&s's history" );

        ApiNetuno.getInstance().getEventDispatcher().dispatch( new NetunoHistoryResetEvent( target, player ) );
        return true;
    }
}