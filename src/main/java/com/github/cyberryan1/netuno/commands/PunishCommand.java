package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.spigot.command.sent.SentCommand;
import com.github.cyberryan1.cybercore.spigot.command.settings.ArgType;
import com.github.cyberryan1.cybercore.spigot.utils.CyberCommandUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberVaultUtils;
import com.github.cyberryan1.cybercore.spigot.utils.time.Timestamp;
import com.github.cyberryan1.netuno.guis.punish.MainPunishGUI;
import com.github.cyberryan1.netuno.guis.punish.utils.MainButton;
import com.github.cyberryan1.netuno.guis.punish.utils.PunishSettings;
import com.github.cyberryan1.netuno.guis.punish.utils.SinglePunishButton;
import com.github.cyberryan1.netuno.models.commands.HelpableCommand;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.Duplex;
import com.github.cyberryan1.netuno.utils.Utils;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PunishCommand extends HelpableCommand {

    private List<SinglePunishButton> punishButtons;
    private List<String> instantKeys;
    private List<MainButton> mainButtons;

    //          Target's UUID
    //                       Instant Key
    //                               Timestamp
    private Map<UUID, Duplex<String, Timestamp>> instantPunishCooldowns;

    public PunishCommand( int helpOrder ) {
        super(
                helpOrder,
                "punish",
                Settings.PUNISH_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.string(),
                "&8/&spunish &p(player)",
                "&sOpens a GUI to punish a player"
        );
        register( true );

        demandPermission( true );
        demandPlayer( true );
        setMinArgLength( 1 );
        setArgType( 0, ArgType.OFFLINE_PLAYER );

        punishButtons = PunishSettings.getAllSinglePunishments();
        mainButtons = PunishSettings.getAllMainButtons();
        instantKeys = punishButtons.stream()
                .map( SinglePunishButton::getInstantKey )
                .map( String::toLowerCase )
                .collect( Collectors.toList() );
    }

    @Override
    public List<String> tabComplete( SentCommand command ) {
        if ( Settings.PUNISH_INSTANT_ENABLED.bool() && command.getArgs().length >= 2
                && CyberVaultUtils.hasPerms( command.getSender(), Settings.PUNISH_INSTANT_PERMISSION.name() ) ) {
            if ( command.getArgs()[1].isEmpty() ) { return instantKeys; }
            return CyberCommandUtils.matchArgs( instantKeys, command.getArg( 1 ) );
        }

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

        if ( command.getArgs().length > 1 && Settings.PUNISH_INSTANT_ENABLED.bool()
                && instantKeys.contains( command.getArg( 1 ).toLowerCase() ) ) {
            SinglePunishButton instantKeyParent = punishButtons.stream()
                    .filter( button -> button.getInstantKey().equalsIgnoreCase( command.getArg( 1 ) ) )
                    .findFirst()
                    .orElseThrow( IllegalArgumentException::new );

            String permission = switch ( instantKeyParent.getGuiType().toLowerCase() ) {
                case "warn" -> PunishSettings.WARN_PERMISSION.string();
                case "mute" -> PunishSettings.MUTE_PERMISSION.string();
                case "ban" -> PunishSettings.BAN_PERMISSION.string();
                case "ipmute" -> PunishSettings.IPMUTE_PERMISSION.string();
                case "ipban" -> PunishSettings.IPBAN_PERMISSION.string();
                default -> throw new IllegalArgumentException();
            };

            if ( CyberVaultUtils.hasPerms( staff, permission ) == false ) {
                command.respond( Settings.PERM_DENIED_MSG.coloredString() );
                return true;
            }

            if ( instantPunishCooldowns.containsKey( target.getUniqueId() )
                    && instantPunishCooldowns.get( target.getUniqueId() ).getFirst().equalsIgnoreCase( command.getArg( 1 ) ) ) {
                if ( new Timestamp().getTimestamp() - instantPunishCooldowns.get( target.getUniqueId() ).getSecond().getTimestamp() < Settings.PUNISH_INSTANT_COOLDOWN.getLong() ) {
                    command.respond( "&p" + target.getName() + " &shas already been punished for this recently" );
                    return true;
                }
            }

            instantPunishCooldowns.put( target.getUniqueId(), new Duplex<>( instantKeyParent.getInstantKey(), new Timestamp() ) );
            instantKeyParent.executePunish( staff, target );
            return true;
        }

        MainPunishGUI gui = new MainPunishGUI( staff, target );
        gui.open();
        return true;
    }
}
