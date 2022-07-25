package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.helpers.command.ArgType;
import com.github.cyberryan1.cybercore.helpers.command.CyberSubcommand;
import com.github.cyberryan1.cybercore.helpers.command.SubcommandStatus;
import com.github.cyberryan1.cybercore.utils.VaultUtils;
import com.github.cyberryan1.netuno.classes.Punishment;
import com.github.cyberryan1.netuno.guis.history.NewHistoryEditGUI;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class HistoryEditSubcommand extends CyberSubcommand {

    public HistoryEditSubcommand() {
        super(
                "edit",
                null,
                Settings.PERM_DENIED_MSG.coloredString(),
                "&8/&shistory &pedit (pun ID)"
        );

        setDemandPlayer( true );
        setDemandPermission( true );
        setMinArgs( 2 );
        setArgType( 1, ArgType.INTEGER );
        setAsync( true );
    }

    @Override
    public List<String> tabComplete( CommandSender sender, String args[] ) {
        return List.of();
    }

    @Override
    public SubcommandStatus execute( CommandSender sender, String args[] ) {
        final Player player = ( Player ) sender;
        final int punId = Integer.parseInt( args[1] );

        Punishment pun = Utils.getDatabase().getPunishment( punId );
        if ( pun == null ) { pun = Utils.getDatabase().getIPPunishment( punId ); }
        if ( pun != null ) {
            final OfflinePlayer target = Bukkit.getOfflinePlayer( UUID.fromString( pun.getPlayerUUID() ) );

            NewHistoryEditGUI gui = new NewHistoryEditGUI( target, player, punId );
            gui.open();
        }

        else {
            CommandErrors.sendPunishmentIDNotFound( player, punId );
        }

        return SubcommandStatus.NORMAL;
    }

    @Override
    public boolean permissionsAllowed( CommandSender sender ) {
        return VaultUtils.hasPerms( sender, Settings.HISTORY_TIME_PERMISSION.string() )
                || VaultUtils.hasPerms( sender, Settings.HISTORY_REASON_PERMISSION.string() );
    }
}