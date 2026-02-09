package com.github.cyberryan1.netuno.listeners;

import com.github.cyberryan1.cybercore.spigot.utils.CyberColorUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberLogUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberVaultUtils;
import com.github.cyberryan1.netuno.Netuno;
import com.github.cyberryan1.netuno.api.models.ApiPlayer;
import com.github.cyberryan1.netuno.api.models.ApiPunishment;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.util.Optional;

public class SignChangeListener implements Listener {

    @EventHandler
    public void onSignChange( SignChangeEvent event ) {
        final Sign sign = ( Sign ) event.getBlock().getState();
        final String lines[] = event.getLines();
        if ( lines[0].isBlank() && lines[1].isBlank() && lines[2].isBlank() && lines[3].isBlank() ) return;
        final Player player = event.getPlayer();

        // checking if the player is not allowed to place a sign because they are currently punished
        if ( Settings.ALLOW_SIGNS_WHILE_PUNISHED.bool() == false ) {
            Optional<ApiPlayer> optional = Netuno.SERVICE.getPlayerNow( player );
            if ( optional.isEmpty() )
                throw new RuntimeException( "Player is not cached, even though they are online" );
            final ApiPlayer netunoPlayer = optional.get();
            if ( netunoPlayer.getActivePunishments().stream().anyMatch( pun -> pun.getType() == ApiPunishment.PunType.MUTE || pun.getType() == ApiPunishment.PunType.IPMUTE ) ) {
                event.setCancelled( true );
                player.sendMessage( Settings.SIGN_WHILE_PUNISHED_MESSAGE.coloredString() );
                return;
            }
        }

        // sending a sign notification, if desired
        if ( Settings.SIGN_NOTIFS_ENABLED.bool() ) {
            String msg = String.join( "\n", Settings.SIGN_NOTIFS_MESSAGE.coloredStringlist() );
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

            TextComponent TEXT = CyberColorUtils.getColoredComponent( msg )
                    .clickEvent(ClickEvent.runCommand("/tptosign " + event.getBlock().getWorld().getName() + " " + event.getBlock().getLocation().getBlockX() + " " + event.getBlock().getLocation().getBlockY() + " " + event.getBlock().getLocation().getBlockZ()));

            for ( Player p : Bukkit.getOnlinePlayers() ) {
                if ( CyberVaultUtils.hasPerms( p, Settings.SIGN_NOTIFS_PERMISSION.string() ) == false ) continue;

                Netuno.SERVICE.getStaff( p ).thenAccept( apiStaff -> {
                    if ( apiStaff.getSignNotificationStatus() == false ) return;
                    p.sendMessage( TEXT );
                } ).exceptionally( Netuno.FUTURE_ERROR_HANDLING );
            }
        }
    }
}