package com.github.cyberryan1.netuno;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import com.github.cyberryan1.cybercore.spigot.CyberCore;
import com.github.cyberryan1.cybercore.spigot.command.settings.BaseCommand;
import com.github.cyberryan1.cybercore.spigot.utils.CyberColorUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberLogUtils;
import com.github.cyberryan1.cybercore.spigot.utils.CyberVaultUtils;
import com.github.cyberryan1.netuno.apimplement.ApiNetuno;
import com.github.cyberryan1.netuno.commands.*;
import com.github.cyberryan1.netuno.guis.history.HistoryEditManager;
import com.github.cyberryan1.netuno.listeners.*;
import com.github.cyberryan1.netuno.managers.ChatslowManager;
import com.github.cyberryan1.netuno.managers.WatchlistManager;
import com.github.cyberryan1.netuno.skriptelements.conditions.RegisterConditions;
import com.github.cyberryan1.netuno.skriptelements.expressions.RegisterExpressions;
import com.github.cyberryan1.netuno.utils.settings.Settings;
import com.github.cyberryan1.netuno.utils.yml.YMLUtils;
import com.github.cyberryan1.netunoapi.NetunoApi;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/* notes in to do:
    - ! = bug needs to be fixed
    - * = working on
    - space = will work on later
    - ? = unsure if doing
    - % = needs testing */
// ! TODO history and alts may not work if they have none in Punish GUI

// TODO add silent option in punish GUI
// TODO add option to disable books for muted/ipmuted players

// ? TODO add a "protected" permission (basically not allowing them to be punished)
// ? TODO staffchat command & prefix
// ? TODO add a way to convert vanilla bans to netuno bans
public final class Netuno extends JavaPlugin {

    public static final List<BaseCommand> registeredCommands = new ArrayList<>();

    private ChatslowManager chatslowManager;

    // Skript
    public SkriptAddon addon;
    public boolean enabled = true;

    @Override
    public void onEnable() {
        // Initialize things
        CyberCore.setPlugin( this );
        new CyberVaultUtils();

        // Update/reload config files
        YMLUtils.initializeConfigs();

        // Set the primary & secondary colors from the config
        CyberColorUtils.setPrimaryColor( Settings.PRIMARY_COLOR.string() );
        CyberColorUtils.setSecondaryColor( Settings.SECONDARY_COLOR.string() );

        ApiNetuno.setupInstance();
        this.getServer().getServicesManager().register( NetunoApi.class, ApiNetuno.getInstance(), this, ServicePriority.Highest );
        chatslowManager = new ChatslowManager();

        // Setup the watchlist manager
        WatchlistManager.initialize();

        registerSkript();
        registerCommands();
        registerEvents();
    }

    @Override
    public void onDisable() {
        // Save the watchlist
        WatchlistManager.save();

        this.getServer().getServicesManager().unregister( NetunoApi.class, ApiNetuno.getInstance() );
        ApiNetuno.deleteInstance();
    }

    private void registerSkript() {
        try {
            addon = Skript.registerAddon( this );
            try {
                addon.loadClasses( "com.github.cyberryan1", "skriptelements" );
            } catch ( IOException e ) {
                CyberLogUtils.logWarn( "Could not enable as a skript addon, will still enable without this syntax!" );
                enabled = false;
            }
            CyberLogUtils.logInfo( "Successfully enabled as a skript addon" );
            RegisterExpressions.register();
            RegisterConditions.register();
        } catch ( NoClassDefFoundError error ) {
            CyberLogUtils.logWarn( "Could not enable as a skript addon, will still enable without this syntax!" );
            enabled = false;
        }
    }

    private void registerCommands() {
        registeredCommands.add( new NetunoCommand() );
        registeredCommands.add( new BanCommand() );
        registeredCommands.add( new ChatslowCommand() );
        registeredCommands.add( new ClearchatCommand() );
        registeredCommands.add( new HistorySupercommand() );
        registeredCommands.add( new IpBanCommand() );
        registeredCommands.add( new IpInfoCommand() );
        registeredCommands.add( new IpMuteCommand() );
        registeredCommands.add( new KickCommand() );
        registeredCommands.add( new MuteCommand() );
        registeredCommands.add( new MutechatCommand() );
        registeredCommands.add( new PunishCommand() );
        registeredCommands.add( new ReportCommand() );
        registeredCommands.add( new ReportsCommand() );
        registeredCommands.add( new TogglesignsCommand() );
        registeredCommands.add( new UnbanCommand() );
        registeredCommands.add( new UnIpbanCommand() );
        registeredCommands.add( new UnIpmuteCommand() );
        registeredCommands.add( new UnmuteCommand() );
        registeredCommands.add( new WarnCommand() );
        registeredCommands.add( new WatchlistCommand() );
    }

    private void registerEvents() {
        this.getServer().getPluginManager().registerEvents( new JoinListener(), this );
        this.getServer().getPluginManager().registerEvents( new ChatListener(), this );
        this.getServer().getPluginManager().registerEvents( new LeaveListener(), this );
        this.getServer().getPluginManager().registerEvents( new SignChangeListener(), this );
        this.getServer().getPluginManager().registerEvents( new CommandListener(), this );
        this.getServer().getPluginManager().registerEvents( new HistoryEditManager(), this );
    }
}
