/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bekvon.bukkit.bandages;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Administrator
 */
public class Bandages extends JavaPlugin {

    private static PermissionHandler authority;
    private boolean firstRun = true;
    private static BandageManager bmanager;


    public void onDisable() {
        bmanager.killThread();
        Logger.getLogger("Minecraft").log(Level.INFO, "[Bandages] Disabled!");
    }

    public void onEnable() {
        if (firstRun) {
            bmanager = new BandageManager();
            BandageEntityListener bandageEntityListener = new BandageEntityListener();
            getServer().getPluginManager().registerEvents(bandageEntityListener, this);
            BandagePlayerListener bandagePlayerListener = new BandagePlayerListener();
            getServer().getPluginManager().registerEvents(bandagePlayerListener, this);
             checkPermissions();
            firstRun = false;
        }
        bmanager.loadConfig(getConfig());
        bmanager.startThread();
        Logger.getLogger("Minecraft").log(Level.INFO, "[Bandages] Enabled! Version:" + this.getDescription().getVersion() + " by bekvon");
    }


    public static BandageManager getManager() {
        return bmanager;
    }

    private void checkPermissions() {
        Plugin p = getServer().getPluginManager().getPlugin("Permissions");
        if (p != null) {
            authority = ((Permissions) p).getHandler();
            Logger.getLogger("Minecraft").log(Level.INFO, "[Bandages] Found Permissions Plugin!");
        } else {
            authority = null;
            Logger.getLogger("Minecraft").log(Level.INFO, "[Bandages] Permissions Plugin NOT Found!");
        }
    }

    public static boolean hasAuthority(Player player, String permission, boolean def) {
        if (player.hasPermission(permission))
            return true;
        if (authority == null)
            return def;
        else
            return authority.has(player, permission);
    }
}
