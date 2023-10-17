/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.player.PlayerInteractEvent
 */
package me.iiSnipez.CombatLog.Listeners;

import me.iiSnipez.CombatLog.CombatLog;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener
implements Listener {
    CombatLog plugin;

    public PlayerInteractListener(CombatLog plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (this.plugin.blockTeleportationEnabled && this.plugin.taggedPlayers.containsKey(player.getName()) && (event.getAction().equals((Object)Action.RIGHT_CLICK_AIR) || event.getAction().equals((Object)Action.RIGHT_CLICK_BLOCK)) && event.getMaterial().equals((Object)Material.ENDER_PEARL)) {
            event.setCancelled(true);
            if (this.plugin.blockTeleportationMessageEnabled) {
                player.sendMessage(this.plugin.translateText(this.plugin.blockTeleportationMessage));
            }
        }
    }
}

