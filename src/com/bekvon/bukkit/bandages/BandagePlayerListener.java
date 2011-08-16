/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bekvon.bukkit.bandages;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

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
    public void onPlayerInteract(PlayerInteractEvent event) {

        Bandages.getManager().playerBandageEvent(event.getPlayer());
        super.onPlayerInteract(event);
    }
}
