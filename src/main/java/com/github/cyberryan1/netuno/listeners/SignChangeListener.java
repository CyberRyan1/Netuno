package com.github.cyberryan1.netuno.listeners;

import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.cybercore.spigot.utils.CyberLogUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberVaultUtils;
import com.github.cyberryan1.netuno.apimplement.database.SignNotifs;
import com.github.cyberryan1.netuno.apimplement.models.players.NetunoPlayer;
import com.github.cyberryan1.netuno.apimplement.models.players.NetunoPlayerCache;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import com.github.cyberryan1.netunoapi.models.punishments.NPunishment;
import com.github.cyberryan1.netunoapi.models.punishments.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.util.List;
import java.util.stream.Collectors;

public class SignChangeListener implements Listener {

    @EventHandler
    public void onSignChange( SignChangeEvent event ) {
        Sign sign = ( Sign ) event.getBlock().getState();
        String lines[] = event.getLines();

        if ( lines[0].equals( "" ) == false || lines[1].equals( "" ) == false || lines[2].equals( "" ) == false || lines[3].equals( "" ) == false ) {
            Player player = event.getPlayer();

            if ( Settings.ALLOW_SIGNS_WHILE_PUNISHED.bool() == false ) {
                final NetunoPlayer nPlayer = NetunoPlayerCache.getOrLoad( player.getUniqueId().toString() );

                List<NPunishment> puns = nPlayer.getPunishments().stream()
                        .filter( pun -> pun.isActive() && ( pun.getPunishmentType() == PunishmentType.MUTE || pun.getPunishmentType() == PunishmentType.IPMUTE ) )
                        .collect( Collectors.toList() );
                if ( puns.size() > 0 ) {
                    event.setCancelled( true );
                    player.sendMessage( Settings.SIGN_WHILE_PUNISHED_MESSAGE.coloredString() );
                    return;
                }
            }

            if ( Settings.SIGN_NOTIFS_ENABLED.bool() ) {
                String msg = Utils.getCombinedString( YMLUtils.getConfig().getColoredStrList( "signs.notifs-msg" ) );

                if ( msg.contains( "[LINE_1]" ) == false || msg.contains( "[LINE_2]" ) == false
                        || msg.contains( "[LINE_3]" ) == false || msg.contains( "[LINE_4]" ) == false ) {
                    CyberLogUtils.logError( "\"signs.notifs-msg\" in the config_default.yml does not contain "
                            + "\"[LINE_1]\", \"[LINE_2]\", \"[LINE_3]\", or \"[LINE_4]\", so it will not be sent" );
                    return;
                }

                msg = msg.replace( "[TARGET]", player.getName() );
                msg = msg.replace( "[LINE_1]", lines[0] ).replace( "[LINE_2]", lines[1] );
                msg = msg.replace( "[LINE_3]", lines[2] ).replace( "[LINE_4]", lines[3] );

                if ( msg.contains( "[LOC]" ) ) {
                    String loc = sign.getX() + ", " + sign.getY() + ", " + sign.getZ();
                    msg = msg.replace( "[LOC]", loc );
                }

                final String finalMsg = msg;
                for ( Player p : Bukkit.getOnlinePlayers() ) {
                    if ( CyberVaultUtils.hasPerms( p, Settings.SIGN_NOTIFS_PERMISSION.string() )
                            && CyberVaultUtils.hasPerms( p, Settings.STAFF_PERMISSION.string() ) ) {
                        Bukkit.getScheduler().runTaskAsynchronously( CyberCore.getPlugin(), () -> {
                            if ( SignNotifs.playerHasNoSignNotifs( p.getUniqueId().toString() ) == false ) {
                                Utils.sendAnyMsg( p, finalMsg );
                            }
                        } );
                    }
                }
            }
        }
    }
}
