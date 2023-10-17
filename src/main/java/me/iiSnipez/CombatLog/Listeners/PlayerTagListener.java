package me.iiSnipez.CombatLog.Listeners;

import me.iiSnipez.CombatLog.CombatLog;
import me.iiSnipez.CombatLog.Events.PlayerTagEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerTagListener implements Listener {

    private final CombatLog plugin;
    //Faction faction;

    public PlayerTagListener(CombatLog plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onTagEvent(PlayerTagEvent event) {
        Entity damager = event.getDamager();
        if(this.plugin.disableWorldNames.contains(damager.getWorld().getName())) {
            return;
        }

        Entity defender = event.getDamagee();
        if(damager.getType() == EntityType.PLAYER) {
            this.tagPlayer((Player) damager, defender.getName());
        }

        if(defender.getType() == EntityType.PLAYER) {
            this.tagPlayer((Player) defender, damager.getName());
        }
    }

    private void tagPlayer(Player player, String damager) {
        if(!this.plugin.taggedPlayers.containsKey(player.getName())) {
            if (this.plugin.taggerMessageEnabled) {
                if (this.plugin.taggedMessageEnabled) {
                    player.sendMessage(this.plugin.translateText(this.plugin.taggedMessage.replaceAll("<name>", damager)));
                }
            }

            if (this.plugin.useActionBar) {
                if (this.plugin.newActionBar) {
                    this.plugin.aBar.sendActionBarNew(player, this.plugin.actionBarInCombatMessage.replaceAll("<time>", this.plugin.tagTimeRemaining(player.getName())));
                } else {
                    this.plugin.aBar.sendActionBarOld(player, this.plugin.actionBarInCombatMessage.replaceAll("<time>", this.plugin.tagTimeRemaining(player.getName())));
                }
            }
        }

        if (this.plugin.removeDisguiseEnabled) {
            this.plugin.removeDisguise(player);
        }
        if (this.plugin.removeFlyEnabled) {
            this.plugin.removeFly(player);
        }
        if (this.plugin.removeInvisPotion) {
            this.removePotion(player);
        }

        this.plugin.taggedPlayers.put(player.getName(), this.plugin.getCurrentTime());
    }

/*    private void tagDamager(Entity damager, Entity damagee) {
        Player p;
        if (damager instanceof Player && !(p = (Player)damager).hasPermission("combatlog.bypass") && !this.plugin.disableWorldNames.contains(p.getWorld().getName())) {
            Location l = p.getLocation();
*//*            if (this.plugin.usesFactions) {
                if (this.plugin.useNewFactions) {
                    this.faction = BoardColl.get().getFactionAt(PS.valueOf((Location)l));
                    if (this.faction.getName().equalsIgnoreCase("SafeZone")) {
                        return;
                    }
                }
            }*//*
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
    }*/


    private void removePotion(Player player) {
        for (PotionEffect potion : player.getActivePotionEffects()) {
            if (!potion.getType().equals((Object)PotionEffectType.INVISIBILITY)) continue;
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            if (!this.plugin.removeInvisMessageEnabled) continue;
            player.sendMessage(this.plugin.translateText(this.plugin.removeInvisMessage));
        }
    }
}

