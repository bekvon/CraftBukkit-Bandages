/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bekvon.bukkit.bandages;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Administrator
 */
public class BandagesListener extends PlayerListener {

    public boolean run;
    public final Bandages parent;
    public Map<String,Long> playerMap;
    public Thread bandageThread;

    public BandagesListener(Bandages parentplugin) {
        parent = parentplugin;
    }

    public void startListening()
    {
        playerMap = new HashMap<String, Long>();
        playerMap = java.util.Collections.synchronizedMap(playerMap);
        bandageThread = new Thread(new Runnable() {
            public void run() {
                while (run) {
                    doBandage();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(BandagesListener.class.getName()).log(Level.SEVERE, null, ex);
                        run = false;
                    }
                }
            }
        });
        run = true;
        bandageThread.start();
    }

    public void stopListening()
    {
        run = false;
    }

    @Override
    public void onPlayerItem(PlayerItemEvent event) {
        if(run)
        {
            if(parent.authority == null || parent.authority.has(event.getPlayer(), "bandages.use"))
            {
                ItemStack item = event.getPlayer().getItemInHand();
                if(item.getAmount()>0 && item.getTypeId() == parent.getConfiguration().getInt("itemId", Material.PAPER.getId()))
                {
                    Player player = event.getPlayer();
                    if(playerMap.containsKey(player.getName()))
                    {
                        player.sendMessage("§cYou are already applying bandages!");
                        return;
                    }
                    if(player.getHealth() >= parent.getConfiguration().getInt("maxHealth", 20))
                    {
                        player.sendMessage("§eYou are already at full health!");
                        return;
                    }
                    player.sendMessage("§eYou begin applying bandages...");
                    playerMap.put(player.getName(), System.currentTimeMillis() + (parent.getConfiguration().getInt("bandageDelay", 4)*1000));
                }
            }
        }
        super.onPlayerItem(event);
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        if(run)
        {
            if(!parent.getConfiguration().getBoolean("allowMove", false))
            {
                if(playerMap.containsKey(event.getPlayer().getName()))
                {
                    event.getPlayer().sendMessage("§cMoving has cancelled your applying bandages!");
                    playerMap.remove(event.getPlayer().getName());
                }
            }
        }
        super.onPlayerMove(event);
    }

    public void doBandage ()
    {
        if(!playerMap.isEmpty())
        {
            Iterator<Entry<String, Long>> thisIt = playerMap.entrySet().iterator();
            while(thisIt.hasNext())
            {
                Entry<String, Long> next = thisIt.next();
                if(next.getValue() <= System.currentTimeMillis())
                {
                    playerMap.remove(next.getKey());
                    Player thisPlayer = parent.getServer().getPlayer(next.getKey());
                    Inventory playerInv = thisPlayer.getInventory();
                    int itemId = parent.getConfiguration().getInt("itemId", Material.PAPER.getId());
                    if (playerInv.contains(itemId)) {
                        ItemStack stack = thisPlayer.getItemInHand();
                        if(thisPlayer.getItemInHand().getTypeId() != itemId)
                            stack = playerInv.getItem(playerInv.first(itemId));
                        if(stack.getAmount() <= 1)
                        {
                            playerInv.remove(stack);
                        }
                        else
                        {
                            stack.setAmount(stack.getAmount()-1);
                        }
                        int health = thisPlayer.getHealth();
                        health = health + parent.getConfiguration().getInt("healAmount", 5);
                        int maxhealth = parent.getConfiguration().getInt("maxHealth", 20);
                        if (health > maxhealth) {
                            health = maxhealth;
                        }
                        thisPlayer.setHealth(health);
                        thisPlayer.sendMessage("§aYou finish applying bandages...");
                    }
                    else
                    {
                        thisPlayer.sendMessage("§cBandages not found in inventory!");
                    }
                }
            }
        }
    }
}
