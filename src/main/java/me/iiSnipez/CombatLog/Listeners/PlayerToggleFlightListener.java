/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerToggleFlightEvent
 */
package me.iiSnipez.CombatLog.Listeners;

import me.iiSnipez.CombatLog.CombatLog;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class PlayerToggleFlightListener
implements Listener {
    CombatLog plugin;

    public PlayerToggleFlightListener(CombatLog plugin) {
        this.plugin = plugin;
    }

    public void onFlightToggle(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (this.plugin.removeFlyEnabled && !player.hasPermission("combatlog.bypass") && this.plugin.taggedPlayers.containsKey(player.getName())) {
            player.setFlying(false);
            player.setAllowFlight(false);
            event.setCancelled(true);
            if (this.plugin.removeModesMessageEnabled) {
                player.sendMessage(this.plugin.translateText(this.plugin.removeModesMessage));
            }
        }
    }
}

