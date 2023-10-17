/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Arrow
 *  org.bukkit.entity.Egg
 *  org.bukkit.entity.EnderPearl
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Projectile
 *  org.bukkit.entity.Snowball
 *  org.bukkit.entity.ThrownPotion
 *  org.bukkit.entity.TippedArrow
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 */
package me.iiSnipez.CombatLog.Listeners;

import me.iiSnipez.CombatLog.CombatLog;
import me.iiSnipez.CombatLog.Events.PlayerTagEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.TippedArrow;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageByEntityListener
implements Listener {
    CombatLog plugin;

    public EntityDamageByEntityListener(CombatLog plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!event.isCancelled()) {
            PlayerTagEvent event1;
            Entity damagee = event.getEntity();
            Entity damager = event.getDamager();
            if (damagee instanceof Player || damager instanceof Player) {
                if (damagee instanceof Player) {
                    if (damagee.hasPermission("combatlog.bypass")){
                        return;
                    }
                }
                if (damager instanceof Player) {
                    if (damager.hasPermission("combatlog.bypass")){
                        return;
                    }
                }


                event1 = new PlayerTagEvent(damager, damagee, this.plugin.tagDuration);
                this.plugin.getServer().getPluginManager().callEvent((Event)event1);
            }
            if ((damager instanceof Projectile && ((Projectile)damager).getShooter() instanceof Player) || (damager instanceof Projectile && damagee instanceof Player)) {
                if (((Projectile)damager).getShooter() instanceof Player) {
                    if (((Player)((Projectile)damager).getShooter()).hasPermission("combatlog.bypass")){
                        return;
                    }
                }
                if (damagee.hasPermission("combatlog.bypass")) {
                    return;
                }
                if ((Bukkit.getVersion().contains("1.12") || Bukkit.getVersion().contains("1.11") || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.9")) && damager instanceof TippedArrow) {
                    event1 = new PlayerTagEvent((Entity)((Projectile)damager).getShooter(), (Entity)damagee, this.plugin.tagDuration);
                    this.plugin.getServer().getPluginManager().callEvent((Event)event1);
                }
                if (damager instanceof Arrow) {
                    event1 = new PlayerTagEvent((Entity)((Projectile)damager).getShooter(), (Entity)damagee, this.plugin.tagDuration);
                    this.plugin.getServer().getPluginManager().callEvent((Event)event1);
                } else if (damager instanceof Snowball) {
                    event1 = new PlayerTagEvent((Entity)((Projectile)damager).getShooter(), (Entity)damagee, this.plugin.tagDuration);
                    this.plugin.getServer().getPluginManager().callEvent((Event)event1);
                } else if (damager instanceof Egg) {
                    event1 = new PlayerTagEvent((Entity)((Projectile)damager).getShooter(), (Entity)damagee, this.plugin.tagDuration);
                    this.plugin.getServer().getPluginManager().callEvent((Event)event1);
                } else if (damager instanceof ThrownPotion) {
                    event1 = new PlayerTagEvent((Entity)((Projectile)damager).getShooter(), (Entity)damagee, this.plugin.tagDuration);
                    this.plugin.getServer().getPluginManager().callEvent((Event)event1);
                } else if (damager instanceof EnderPearl) {
                    return;
                }
            }
        }
    }
}

