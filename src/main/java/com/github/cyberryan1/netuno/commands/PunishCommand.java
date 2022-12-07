package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.spigot.command.CyberCommand;
import com.github.cyberryan1.cybercore.spigot.command.sent.SentCommand;
import com.github.cyberryan1.cybercore.spigot.command.settings.ArgType;
import com.github.cyberryan1.netuno.guis.punish.MainPunishGUI;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class PunishCommand extends CyberCommand {

    public PunishCommand() {
        super(
                "punish",
                Settings.PUNISH_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.string(),
                "&8/&spunish &p(player)"
        );
        register( true );

        demandPermission( true );
        demandPlayer( true );
        setMinArgLength( 1 );
        setArgType( 0, ArgType.OFFLINE_PLAYER );
    }

    @Override
    public List<String> tabComplete( SentCommand command ) {
        return List.of();
    }

    @Override
    // /punish (player)
    public boolean execute( SentCommand command ) {
        final Player staff = command.getPlayer();
        final OfflinePlayer target = command.getOfflinePlayerAtArg( 0 );

        if ( Settings.PUNISH_OTHER_STAFF.bool() == false ) {
            if ( Utils.checkStaffPunishmentAllowable( staff, target ) == false ) {
                CommandErrors.sendPlayerCannotBePunished( staff, target.getName() );
                return true;
            }
        }

        MainPunishGUI gui = new MainPunishGUI( staff, target );
        gui.open();
        return true;
    }
}
