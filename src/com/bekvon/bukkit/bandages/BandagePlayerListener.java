/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bekvon.bukkit.bandages;

import java.util.Map;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.Configuration;

/**
 *
 * @author Administrator
 */
public class BandagePlayerListener extends PlayerListener {


    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        Bandages.getManager().playerMovement(event.getPlayer());
        super.onPlayerMove(event);
    }

    @Override
    public void onPlayerItem(PlayerItemEvent event) {
        Bandages.getManager().playerBandageEvent(event.getPlayer());
        super.onPlayerItem(event);
    }



}
