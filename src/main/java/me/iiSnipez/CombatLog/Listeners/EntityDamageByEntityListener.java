package me.iiSnipez.CombatLog.Listeners;

import me.iiSnipez.CombatLog.CombatLog;
import me.iiSnipez.CombatLog.Events.PlayerTagEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageByEntityListener implements Listener {

    private final CombatLog plugin;

    public EntityDamageByEntityListener(CombatLog plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(event.isCancelled()) {
            return;
        }

        Entity defender = event.getEntity();
        Entity damager = event.getDamager();
        if(defender.getType() == EntityType.PLAYER) {
            if(defender.hasPermission("combatlog.bypass")) {
                return;
            }
        }

        if(damager.getType() == EntityType.PLAYER) {
            if(defender.hasPermission("combatlog.bypass")) {
                return;
            }
        }

        if(damager.getType() == EntityType.PLAYER || damager.getType() == EntityType.PLAYER) {
            PlayerTagEvent playerTagEvent = new PlayerTagEvent(damager, defender, this.plugin.tagDuration);
            this.plugin.getServer().getPluginManager().callEvent(playerTagEvent);
            return;
        }

        if(damager instanceof Projectile) {
            if(((Projectile) damager).getShooter() instanceof Player) {
                Player player = ((Player) ((Projectile) damager).getShooter()).getPlayer();
                if(player.hasPermission("combatlog.bypass")) {
                    return;
                }

                PlayerTagEvent playerTagEvent = new PlayerTagEvent(player, defender, this.plugin.tagDuration);
                this.plugin.getServer().getPluginManager().callEvent(playerTagEvent);
            }
        }
    }
}

