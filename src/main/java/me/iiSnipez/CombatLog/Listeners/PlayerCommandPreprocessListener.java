/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerCommandPreprocessEvent
 */
package me.iiSnipez.CombatLog.Listeners;

import me.iiSnipez.CombatLog.CombatLog;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerCommandPreprocessListener
implements Listener {
    public CombatLog plugin;

    public PlayerCommandPreprocessListener(CombatLog plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String cmd = event.getMessage();
        if (this.plugin.taggedPlayers.containsKey(player.getName()) && !cmd.equalsIgnoreCase("ct") && !cmd.equalsIgnoreCase("tag")) {
            cmd = cmd.replaceAll("/", "");
            if (this.plugin.blockCommandsEnabled) {
                if (this.plugin.commandNames.contains(cmd)) {
                    event.setCancelled(true);
                    if (this.plugin.blockCommandsMessageEnabled) {
                        player.sendMessage(this.plugin.translateText(String.valueOf(this.plugin.blockCommandsMessage)));
                    }
                }
            } else if (this.plugin.whitelistModeEnabled && !this.plugin.commandNames.contains(cmd)) {
                event.setCancelled(true);
                if (this.plugin.blockCommandsMessageEnabled) {
                    player.sendMessage(this.plugin.translateText(String.valueOf(this.plugin.blockCommandsMessage)));
                }
            }
        }
    }
}

