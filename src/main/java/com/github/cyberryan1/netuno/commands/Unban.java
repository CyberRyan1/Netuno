package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.helpers.command.ArgType;
import com.github.cyberryan1.cybercore.helpers.command.CyberCommand;
import com.github.cyberryan1.netuno.classes.Punishment;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.Time;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Unban extends CyberCommand {

    public Unban() {
        super(
                "unban",
                Settings.UNBAN_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.string(),
                "&8/&sunban &p(player)"
        );
        register( true );

        demandPermission( true );
        setMinArgs( 1 );
        setArgType( 0, ArgType.OFFLINE_PLAYER );
        setAsync( true );
    }

    @Override
    public List<String> tabComplete( CommandSender sender, String[] args ) {
        return List.of();
    }

    @Override
    public boolean execute( CommandSender sender, String args[] ) {
        final OfflinePlayer target = Bukkit.getOfflinePlayer( args[0] );

        ArrayList<Punishment> punishments = Utils.getDatabase().getPunishment( target.getUniqueId().toString(), "ban", true );
        if ( punishments.size() >= 1 ) {
            for ( Punishment pun : punishments ) {
                Utils.getDatabase().setPunishmentActive( pun.getID(), false );
            }

            Punishment unbanPun = new Punishment( "", "", "Unban", -1, -1, "", false );
            unbanPun.setPlayerUUID( target.getUniqueId().toString() );
            unbanPun.setDate( Time.getCurrentTimestamp() );

            unbanPun.setStaffUUID( "CONSOLE" );
            if ( sender instanceof Player ) {
                Player staff = ( Player ) sender;
                unbanPun.setStaffUUID( staff.getUniqueId().toString() );
            }

            Utils.getDatabase().addPunishment( unbanPun );

            Utils.doPublicPunBroadcast( unbanPun );
            Utils.doStaffPunBroadcast( unbanPun );
        }

        else {
            CommandErrors.sendNoPunishments( sender, target.getName(), "ban" );
        }

        return true;
    }
}
