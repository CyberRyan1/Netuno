package com.github.cyberryan1.netuno.listeners;

import com.github.cyberryan1.netuno.classes.IPPunishment;
import com.github.cyberryan1.netuno.classes.Punishment;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.VaultUtils;
import com.github.cyberryan1.netuno.utils.database.Database;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.util.ArrayList;

public class SignChangeListener implements Listener {

    private final Database DATA = Utils.getDatabase();

    @EventHandler
    public void onSignChange( SignChangeEvent event ) {
        Sign sign = ( Sign ) event.getBlock().getState();
        String lines[] = event.getLines();

        if ( lines[0].equals( "" ) == false || lines[1].equals( "" ) == false || lines[2].equals( "" ) == false || lines[3].equals( "" ) == false ) {
            Player player = event.getPlayer();

            if ( YMLUtils.getConfig().getBool( "signs.allow-while-muted" ) == false ) {
                ArrayList<Punishment> puns = DATA.getPunishment( player.getUniqueId().toString(), "mute", true );
                ArrayList<IPPunishment> ipPuns = DATA.getIPPunishment( player.getUniqueId().toString(), "ipmute", true );
                if ( ipPuns.size() + puns.size() > 0 ) {
                    event.setCancelled( true );
                    player.sendMessage( YMLUtils.getConfig().getColoredStr( "signs.sign-while-muted-attempt" ) );
                    return;
                }
            }

            if ( YMLUtils.getConfig().getBool( "signs.notifs" ) ) {
                String msg = Utils.getCombinedString( YMLUtils.getConfig().getColoredStrList( "signs.notifs-msg" ) );

                if ( msg.contains( "[LINE_1]" ) == false || msg.contains( "[LINE_2]" ) == false
                        || msg.contains( "[LINE_3]" ) == false || msg.contains( "[LINE_4]" ) == false ) {
                    Utils.logError( "\"signs.notifs-msg\" in the config.yml does not contain "
                            + "\"[LINE_1]\", \"[LINE_2]\", \"[LINE_3]\", or \"[LINE_4]\", so it will not be sent" );
                    return;
                }

                msg = msg.replace( "[TARGET]", player.getName() );
                msg = msg.replace( "[LINE_1]", lines[0] ).replace( "[LINE_2]", lines[1] );
                msg = msg.replace( "[LINE_3]", lines[2] ).replace( "[LINE_4]", lines[3] );

                if ( msg.contains( "[LOC]" ) ) {
                //    String loc = "%s, %s, %s".format( sign.getLocation().getX() + "", sign.getLocation().getY() + "", sign.getLocation().getZ() + "" );
                    String loc = sign.getX() + ", " + sign.getY() + ", " + sign.getZ();
                    msg = msg.replace( "[LOC]", loc );
                }

                for ( Player p : Bukkit.getOnlinePlayers() ) {
                    if ( VaultUtils.hasPerms( p, YMLUtils.getConfig().getStr( "signs.notifs-perm" ) )
                            && VaultUtils.hasPerms( p, YMLUtils.getConfig().getStr( "general.staff-perm" ) ) ) {
                        if ( DATA.checkPlayerNoSignNotifs( p ) == false ) {
                            Utils.sendAnyMsg( p, msg );
                        }
                    }
                }
            }
        }
    }
}
