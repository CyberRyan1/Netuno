package com.github.cyberryan1.netuno.commands;

import com.github.cyberryan1.cybercore.spigot.command.CyberCommand;
import com.github.cyberryan1.cybercore.spigot.command.sent.SentCommand;
import com.github.cyberryan1.cybercore.spigot.command.settings.ArgType;
import com.github.cyberryan1.cybercore.spigot.utils.CyberCommandUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberVaultUtils;
import com.github.cyberryan1.cybercore.spigot.utils.time.Timestamp;
import com.github.cyberryan1.netuno.guis.punish.MainPunishGui;
import com.github.cyberryan1.netuno.guis.punish.PunishmentGuiExecutor;
import com.github.cyberryan1.netuno.guis.punish.models.PunishSettings;
import com.github.cyberryan1.netuno.guis.punish.models.SinglePunishButton;
import com.github.cyberryan1.netuno.models.NetunoPunishment;
import com.github.cyberryan1.netuno.models.commands.CommandHelpInfo;
import com.github.cyberryan1.netuno.utils.CommandErrors;
import com.github.cyberryan1.netuno.utils.Duplex;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

// TODO allow console to use instant punishments (but not the GUI itself, duh)
public class PunishCommand extends CyberCommand {

    private List<SinglePunishButton> punishButtons;
    private List<String> instantKeys;

    //          Target's UUID
    //                       Instant Key
    //                               Timestamp
    private Map<UUID, Duplex<String, Timestamp>> instantPunishCooldowns = new HashMap<>();

    public PunishCommand( int helpOrder ) {
        super(
                "punish",
                Settings.PUNISH_PERMISSION.string(),
                Settings.PERM_DENIED_MSG.coloredString(),
                "&8/&spunish &p(player) [punishment code]"
        );
        register( true );
        new CommandHelpInfo( this, helpOrder );

        demandPermission( true );
//        demandPlayer( true ); // eventually want this to be able to work
//                              //    for console as well (only for instant punishments)
        setMinArgLength( 1 );
        setArgType( 0, ArgType.OFFLINE_PLAYER );

        punishButtons = PunishSettings.getAllSinglePunishments();
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
    public boolean execute( SentCommand command ) {
        final OfflinePlayer target = command.getOfflinePlayerAtArg( 0 );

        if ( command.getSender() instanceof ConsoleCommandSender ) {
            if ( command.getArgs().length < 2 ) {
                sendUsage( command.getSender() );
                return true;
            }

            if ( instantKeys.contains( command.getArg( 1 ).toLowerCase() ) == false ) {
                command.respond( "&sInvalid instant key" );
                return true;
            }

            SinglePunishButton instantKeyParent = punishButtons.stream()
                    .filter( button -> button.getInstantKey().equalsIgnoreCase( command.getArg( 1 ) ) )
                    .findFirst()
                    .orElseThrow( IllegalArgumentException::new );

            // For sending console instant punishments, they can add the "-o" flag at the end
            // If they do this, then it will override any cooldown
            // TODO remove excess comments
            //CyberMsgUtils.broadcast( "command.getArgs().length == " + command.getArgs().length ); // ! debug
            //CyberMsgUtils.broadcast( "command.getArg( 2 ) == " + command.getArg( 2 ) ); // ! debug
            boolean override = command.getArgs().length >= 3 && command.getArg( 2 ).equalsIgnoreCase( "-o" );
            //CyberMsgUtils.broadcast( "override == " + override ); // ! debug

            if ( override == false ) {
                if ( checkForCooldown( command, target, instantKeyParent ) ) {
                    command.respond( "&p" + target.getName() + " &shas already been punished for this recently" );
                    return true;
                }
            }

            instantPunishCooldowns.put( target.getUniqueId(), new Duplex<>( instantKeyParent.getInstantKey(), new Timestamp() ) );
            PunishmentGuiExecutor.executePunish( instantKeyParent, command.getSender(), target, 1.0f, false ); // for now, will assume all instant punishments are not silent

            return true;
        }

        final Player staff = command.getPlayer();
        if ( NetunoPunishment.checkPlayerCanPunish( staff, target ) == false ) {
            CommandErrors.sendPlayerCannotBePunished( staff, target.getName() );
            return true;
        }

        if ( command.getArgs().length > 1 && Settings.PUNISH_INSTANT_ENABLED.bool()
                && instantKeys.contains( command.getArg( 1 ).toLowerCase() ) ) {
            SinglePunishButton instantKeyParent = punishButtons.stream()
                    .filter( button -> button.getInstantKey().equalsIgnoreCase( command.getArg( 1 ) ) )
                    .findFirst()
                    .orElseThrow( IllegalArgumentException::new );

            String permission = switch ( instantKeyParent.getGuiType() ) {
                case MAIN -> throw new IllegalArgumentException();
                case WARN -> PunishSettings.WARN_PERMISSION.string();
                case MUTE -> PunishSettings.MUTE_PERMISSION.string();
                case BAN -> PunishSettings.BAN_PERMISSION.string();
                case IPMUTE -> PunishSettings.IPMUTE_PERMISSION.string();
                case IPBAN -> PunishSettings.IPBAN_PERMISSION.string();
            };

            if ( CyberVaultUtils.hasPerms( staff, permission ) == false ) {
                command.respond( Settings.PERM_DENIED_MSG.coloredString() );
                return true;
            }

            if ( checkForCooldown( command, target, instantKeyParent ) ) {
                command.respond( "&p" + target.getName() + " &shas already been punished for this recently" );
                return true;
            }

            instantPunishCooldowns.put( target.getUniqueId(), new Duplex<>( instantKeyParent.getInstantKey(), new Timestamp() ) );
            PunishmentGuiExecutor.executePunish( instantKeyParent, staff, target, 1.0f, false ); // for now, will assume all instant punishments are not silent
            return true;
        }

        MainPunishGui gui = new MainPunishGui( staff, target );
        gui.open();
        return true;
    }

    private boolean checkForCooldown( SentCommand command, OfflinePlayer target, SinglePunishButton instantKeyParent ) {
        return ( instantPunishCooldowns.containsKey( target.getUniqueId() )
                    && instantPunishCooldowns.get( target.getUniqueId() ).getFirst()
                        .equalsIgnoreCase( command.getArg( 1 ) ) )
                && ( new Timestamp().getTimestamp() - instantPunishCooldowns.get( target.getUniqueId() )
                        .getSecond().getTimestamp() < Settings.PUNISH_INSTANT_COOLDOWN.integer() );
    }
}
