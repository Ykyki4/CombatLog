/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  de.robingrether.idisguise.api.DisguiseEvent
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 */
package me.iiSnipez.CombatLog.Listeners;

import de.robingrether.idisguise.api.DisguiseEvent;
import me.iiSnipez.CombatLog.CombatLog;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PlayeriDisguiseListener
implements Listener {
    CombatLog plugin;

    public PlayeriDisguiseListener(CombatLog plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayeriDisguise(DisguiseEvent event) {
        Player player = event.getPlayer();
        if (this.plugin.usesiDisguise && this.plugin.removeDisguiseEnabled && this.plugin.taggedPlayers.containsKey(player.getName())) {
            this.plugin.dAPI.undisguise((OfflinePlayer)player);
            event.setCancelled(true);
            if (this.plugin.removeModesMessageEnabled) {
                player.sendMessage(this.plugin.translateText(this.plugin.removeModesMessage.replaceAll("<mode>", "disguise")));
            }
        }
    }
}

