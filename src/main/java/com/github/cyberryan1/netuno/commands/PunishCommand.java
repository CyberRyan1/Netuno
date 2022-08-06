package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.helpers.command.ArgType;
import com.github.cyberryan1.cybercore.helpers.command.CyberCommand;
import com.github.cyberryan1.netuno.guis.punish.MainPunishGUI;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
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
        setMinArgs( 1 );
        setArgType( 0, ArgType.OFFLINE_PLAYER );
    }

    @Override
    public List<String> tabComplete( CommandSender sender, String[] args ) {
        return List.of();
    }

    @Override
    // /punish (player)
    public boolean execute( CommandSender sender, String args[] ) {
        final Player staff = ( Player ) sender;
        final OfflinePlayer target = Bukkit.getOfflinePlayer( args[0] );

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
