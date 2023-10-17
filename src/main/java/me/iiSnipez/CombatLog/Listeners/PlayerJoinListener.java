/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerJoinEvent
 */
package me.iiSnipez.CombatLog.Listeners;

import me.iiSnipez.CombatLog.CombatLog;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener
implements Listener {
    CombatLog plugin;

    public PlayerJoinListener(CombatLog plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (this.plugin.updateCheckEnabled && player.hasPermission("combatlog.update") && this.plugin.updateAvaliable && this.plugin.updateCheckMessageEnabled) {
            player.sendMessage(this.plugin.translateText(this.plugin.updateCheckMessage));
        }
        if (this.plugin.MOTDEnabled && this.plugin.MOTDMessageEnabled) {
            player.sendMessage(this.plugin.translateText(this.plugin.MOTDMessage));
        }
        if (this.plugin.killMessageEnabled && this.plugin.killPlayers.contains(player.getUniqueId().toString())) {
            player.sendMessage(this.plugin.translateText(this.plugin.killMessage));
            this.plugin.killPlayers.remove(player.getUniqueId().toString());
        }
    }
}

