package com.github.cyberryan1.netuno.errors;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class LuckPermsVaultUnsafeNeededException extends Exception {

    public static final String LUCKPERMS_VAULT_UNSAFE_NEEDED = "LuckPerms cannot be used unless \"vault-unsafe-lookups\" in its config is enabled!";

    public LuckPermsVaultUnsafeNeededException() {
        super( LUCKPERMS_VAULT_UNSAFE_NEEDED );
    }

    public LuckPermsVaultUnsafeNeededException( String msg ) {
        super( msg );
    }

    public static boolean isLuckpermsVaultUnsafe() {
        if ( Bukkit.getPluginManager().isPluginEnabled( "LuckPerms" ) ) {
            Plugin luckperms = Bukkit.getPluginManager().getPlugin( "LuckPerms" );
            if ( luckperms.getConfig().getBoolean( "vault-unsafe-lookups" ) == false ) {
                return false;
            }
        }

        return true;
    }
}
