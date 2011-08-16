/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bekvon.bukkit.bandages;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Administrator
 */
public class Bandages extends JavaPlugin {

    private static PermissionHandler authority;
    private boolean firstRun = true;
    private static BandageManager bmanager;
    private static BandagePlayerListener plistener;
    private static BandageEntityListener elistener;

    public void onDisable() {
        bmanager.killThread();
        Logger.getLogger("Minecraft").log(Level.INFO,"[Bandages] Disabled!");
    }

    public void onEnable() {
        this.getConfiguration().load();
        if(firstRun)
        {
            bmanager = new BandageManager();
            plistener = new BandagePlayerListener();
            elistener = new BandageEntityListener();
            getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, plistener, Priority.Normal, this);
            getServer().getPluginManager().registerEvent(Event.Type.PLAYER_MOVE, plistener, Priority.Normal, this);
            getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DAMAGE, elistener, Priority.Lowest, this);
            checkPermissions();
            firstRun = false;
        }
        bmanager.loadConfig(this.getConfiguration());
        bmanager.startThread();
        Logger.getLogger("Minecraft").log(Level.INFO,"[Bandages] Enabled! Version:" + this.getDescription().getVersion() + " by bekvon");
    }

    public static BandageManager getManager()
    {
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

    public static boolean hasAuthority(Player player, String permission, boolean def)
    {
        if(player.hasPermission(permission))
            return true;
        if(authority == null)
            return def;
        else
            return authority.has(player, permission);
    }
}
