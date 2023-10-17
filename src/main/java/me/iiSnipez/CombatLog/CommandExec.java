/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.ConsoleCommandSender
 *  org.bukkit.entity.Player
 */
package me.iiSnipez.CombatLog;

import me.iiSnipez.CombatLog.CombatLog;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CommandExec
implements CommandExecutor {
    public CombatLog plugin;

    public CommandExec(CombatLog plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getLabel().equalsIgnoreCase("combatlog") || cmd.getLabel().equalsIgnoreCase("cl")) {
            if (sender instanceof Player) {
                Player player = (Player)sender;
                if (args.length == 0) {
                    player.sendMessage(this.plugin.translateText("&8[&4CombatLog&8]&7 Originally developed by &cJackProehl&7."));
                    player.sendMessage(this.plugin.translateText("&8[&4CombatLog&8]&7 Update developed and maintained by &ciiSnipez&7."));
                    player.sendMessage(this.plugin.translateText("&8[&4CombatLog&8]&7 This server's tag duration is: &c" + this.plugin.tagDuration + " seconds&7."));
                    player.sendMessage(this.plugin.translateText("&cUse &7/cl help &cto view the commands."));
                } else if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("help")) {
                        player.sendMessage(this.plugin.translateText("&8[&4CombatLog&8]&7Use &c/tag &7or &c/ct &7to view if you are in combat."));
                        if (player.hasPermission("combatlog.reload") || player.isOp()) {
                            player.sendMessage(this.plugin.translateText("&8[&4CombatLog&8]&7Use &c/cl reload &7to reload the configuration."));
                        }
                    } else if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
                        if (player.hasPermission("combatlog.reload") || player.isOp()) {
                            this.plugin.loadSettings();
                            player.sendMessage(this.plugin.translateText("&8[&4CombatLog&8] &aConfiguration reloaded."));
                        } else {
                            player.sendMessage(this.plugin.translateText("&4You do not have permission to use this command."));
                        }
                    } else if (args[0].equalsIgnoreCase("update") && player.hasPermission("combatlog.update")) {
                        if (this.plugin.updateAvaliable) {
                            this.plugin.update(player);
                        } else {
                            player.sendMessage(this.plugin.translateText("&8[&4CombatLog&8] &7No update was detected."));
                        }
                    }
                }
            } else if (sender instanceof ConsoleCommandSender) {
                ConsoleCommandSender console = (ConsoleCommandSender)sender;
                if (args.length == 0) {
                    console.sendMessage("[CombatLog] Use '/cl help' to view all of the commands.");
                } else if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("help")) {
                        console.sendMessage("[CombatLog] /cl reload - reloads the configuration.");
                    } else if (args[0].equalsIgnoreCase("reload")) {
                        this.plugin.loadSettings();
                        console.sendMessage("[CombatLog] Configuration reloaded.");
                    } else if (args[0].equalsIgnoreCase("update")) {
                        if (this.plugin.updateAvaliable) {
                            this.plugin.consoleUpdate(console);
                        } else {
                            console.sendMessage((Object)ChatColor.RED + "[CombatLog] No update was detected.");
                        }
                    }
                }
            }
        } else if (cmd.getLabel().equalsIgnoreCase("tag") || cmd.getLabel().equalsIgnoreCase("ct")) {
            if (sender instanceof Player) {
                Player player = (Player)sender;
                String id = player.getName();
                if (this.plugin.taggedPlayers.containsKey(id) && (long)this.plugin.tagDuration - (this.plugin.getCurrentTime() - Long.valueOf(this.plugin.taggedPlayers.get(id))) >= 1L) {
                    player.sendMessage(this.plugin.translateText(this.plugin.tagTimeMessage.replaceAll("<time>", this.plugin.tagTimeRemaining(id))));
                } else if (this.plugin.taggedPlayers.containsKey(id) && (long)this.plugin.tagDuration - (this.plugin.getCurrentTime() - Long.valueOf(this.plugin.taggedPlayers.get(id))) < 1L) {
                    player.sendMessage(this.plugin.translateText(this.plugin.tagTimeMessage.replaceAll("<time>", this.plugin.tagTimeRemaining(id))));
                } else if (!this.plugin.taggedPlayers.containsKey(id)) {
                    player.sendMessage(this.plugin.translateText(this.plugin.notInCombatMessage));
                }
            } else if (sender instanceof ConsoleCommandSender) {
                ConsoleCommandSender console = (ConsoleCommandSender)sender;
                console.sendMessage("[CombatLog] The console cannot use this command.");
            }
        }
        return false;
    }
}

