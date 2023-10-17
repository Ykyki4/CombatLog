/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.massivecraft.factions.Board
 *  com.massivecraft.factions.FLocation
 *  com.massivecraft.factions.entity.BoardColl
 *  com.massivecraft.factions.entity.Faction
 *  com.massivecraft.massivecore.ps.PS
 *  org.bukkit.Location
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 */
package me.iiSnipez.CombatLog.Listeners;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.ps.PS;
import me.iiSnipez.CombatLog.CombatLog;
import me.iiSnipez.CombatLog.Events.PlayerTagEvent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerTagListener
implements Listener {
    CombatLog plugin;
    Faction faction;

    public PlayerTagListener(CombatLog plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onTagEvent(PlayerTagEvent event) {
        Entity damager = event.getDamager();
        Entity damagee = event.getDamagee();
        this.tagDamager(damager, damagee);
        this.tagDamagee(damager, damagee);
    }

    private void tagDamager(Entity damager, Entity damagee) {
        Player p;
        if (damager instanceof Player && !(p = (Player)damager).hasPermission("combatlog.bypass") && !this.plugin.disableWorldNames.contains(p.getWorld().getName())) {
            Location l = p.getLocation();
            if (this.plugin.usesFactions) {
                if (this.plugin.useNewFactions) {
                    this.faction = BoardColl.get().getFactionAt(PS.valueOf((Location)l));
                    if (this.faction.getName().equalsIgnoreCase("SafeZone")) {
                        return;
                    }
                }
            }
            if (!this.plugin.taggedPlayers.containsKey(p.getName())) {
                this.plugin.taggedPlayers.put(p.getName(), this.plugin.getCurrentTime());
                if (this.plugin.taggerMessageEnabled) {
                    if (this.plugin.taggedMessageEnabled) {
                        p.sendMessage(this.plugin.translateText(this.plugin.taggedMessage.replaceAll("<name>", damagee.getName())));
                    }
                }
                if (this.plugin.useActionBar) {
                    if (this.plugin.newActionBar) {
                        this.plugin.aBar.sendActionBarNew(p, this.plugin.actionBarInCombatMessage.replaceAll("<time>", this.plugin.tagTimeRemaining(p.getName())));
                    } else {
                        this.plugin.aBar.sendActionBarOld(p, this.plugin.actionBarInCombatMessage.replaceAll("<time>", this.plugin.tagTimeRemaining(p.getName())));
                    }
                }
                if (this.plugin.usesLibsDisguise && this.plugin.removeDisguiseEnabled) {
                    this.plugin.removeDisguise(p);
                }
                if (this.plugin.removeFlyEnabled) {
                    this.plugin.removeFly(p);
                }
                if (this.plugin.removeInvisPotion) {
                    this.removePotion(p);
                    if(damagee instanceof Player) {
                        this.removePotion((Player)damagee);
                    }
                }
            } else {
                this.plugin.taggedPlayers.remove(p.getName());
                this.plugin.taggedPlayers.put(p.getName(), this.plugin.getCurrentTime());
                if (this.plugin.removeDisguiseEnabled) {
                    this.plugin.removeDisguise(p);
                }
                if (this.plugin.removeFlyEnabled) {
                    this.plugin.removeFly(p);
                }
                if (this.plugin.removeInvisPotion) {
                    this.removePotion(p);
                    if (damagee instanceof Player) {
                        this.removePotion((Player)damagee);
                    }
                }
            }
        }
    }

    private void tagDamagee(Entity damager, Entity damagee) {
        if (damagee instanceof Player) {
            Player p = (Player)damagee;
            Location l = p.getLocation();
            if (!p.hasPermission("combatlog.bypass") && !this.plugin.disableWorldNames.contains(p.getWorld().getName())) {
                if (this.plugin.usesFactions) {
                    if (this.plugin.useNewFactions) {
                        this.faction = BoardColl.get().getFactionAt(PS.valueOf((Location)l));
                        if (this.faction.getName().equalsIgnoreCase("SafeZone")) {
                            return;
                        }
                    }
                }
                if (!this.plugin.taggedPlayers.containsKey(p.getName())) {
                    this.plugin.taggedPlayers.put(p.getName(), this.plugin.getCurrentTime());
                    if (this.plugin.taggedMessageEnabled) {
                        p.sendMessage(this.plugin.translateText(this.plugin.taggedMessage.replaceAll("<name>", damager.getName())));
                    }
                    if (this.plugin.useActionBar) {
                        if (this.plugin.newActionBar) {
                            this.plugin.aBar.sendActionBarNew(p, this.plugin.actionBarInCombatMessage.replaceAll("<time>", this.plugin.tagTimeRemaining(p.getName())));
                        } else {
                            this.plugin.aBar.sendActionBarOld(p, this.plugin.actionBarInCombatMessage.replaceAll("<time>", this.plugin.tagTimeRemaining(p.getName())));
                        }
                    }
                    if (this.plugin.usesLibsDisguise && this.plugin.removeDisguiseEnabled) {
                        this.plugin.removeDisguise(p);
                    }
                    if (this.plugin.removeFlyEnabled) {
                        this.plugin.removeFly(p);
                    }
                    if (this.plugin.removeInvisPotion) {
                        this.removePotion(p);
                        if (damager instanceof Player) {
                            this.removePotion((Player) damager);
                        }
                    }
                } else {
                    this.plugin.taggedPlayers.remove(p.getName());
                    this.plugin.taggedPlayers.put(p.getName(), this.plugin.getCurrentTime());
                    if (this.plugin.removeDisguiseEnabled) {
                        this.plugin.removeDisguise(p);
                    }
                    if (this.plugin.removeFlyEnabled) {
                        this.plugin.removeFly(p);
                    }
                    if (this.plugin.removeInvisPotion) {
                        this.removePotion(p);
                        if (damager instanceof Player) {
                            this.removePotion((Player) damager);
                        }
                    }
                }
            }
        }
    }

    private void removePotion(Player player) {
        for (PotionEffect potion : player.getActivePotionEffects()) {
            if (!potion.getType().equals((Object)PotionEffectType.INVISIBILITY)) continue;
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            if (!this.plugin.removeInvisMessageEnabled) continue;
            player.sendMessage(this.plugin.translateText(this.plugin.removeInvisMessage));
        }
    }
}

