/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldguard.bukkit.WorldGuardPlugin
 *  de.robingrether.idisguise.api.DisguiseAPI
 *  me.libraryaddict.disguise.DisguiseAPI
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.ConsoleCommandSender
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package me.iiSnipez.CombatLog;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import de.robingrether.idisguise.api.DisguiseAPI;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import me.iiSnipez.CombatLog.Events.PlayerUntagEvent;
import me.iiSnipez.CombatLog.Listeners.EntityDamageByEntityListener;
import me.iiSnipez.CombatLog.Listeners.PlayerCommandPreprocessListener;
import me.iiSnipez.CombatLog.Listeners.PlayerDeathListener;
import me.iiSnipez.CombatLog.Listeners.PlayerInteractListener;
import me.iiSnipez.CombatLog.Listeners.PlayerJoinListener;
import me.iiSnipez.CombatLog.Listeners.PlayerKickListener;
import me.iiSnipez.CombatLog.Listeners.PlayerMoveListener;
import me.iiSnipez.CombatLog.Listeners.PlayerQuitListener;
import me.iiSnipez.CombatLog.Listeners.PlayerTagListener;
import me.iiSnipez.CombatLog.Listeners.PlayerTeleportListener;
import me.iiSnipez.CombatLog.Listeners.PlayerToggleFlightListener;
import me.iiSnipez.CombatLog.Listeners.PlayerUntagListener;
import me.iiSnipez.CombatLog.Listeners.PlayeriDisguiseListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class CombatLog
extends JavaPlugin {
    public Logger log = Logger.getLogger("Minecraft");
    public PluginFile clConfig;
    public CommandExec commandExec;
    public Variables vars;
    public ActionBar aBar;
    public BStatsMetrics bsm;
    public WorldGuardPlugin wg;
    public DisguiseAPI dAPI;
    public boolean usesLibsDisguise = false;
    public boolean usesiDisguise = false;
    public boolean usesFactions = false;
    public boolean usesWorldGuard = false;
    public Updater updater;
    public boolean updateCheckEnabled = false;
    public boolean autoDownloadEnabled = false;
    public boolean updateAvaliable = false;
    public boolean MOTDEnabled = false;
    public boolean broadcastEnabled = false;
    public int tagDuration = 10;
    public boolean useActionBar = false;
    public boolean useBossBar = false;
    public boolean versionSupported = false;
    public boolean removeFlyEnabled = false;
    public boolean removeDisguiseEnabled = false;
    public boolean removeTagOnKick = false;
    public boolean removeTagOnLagout = false;
    public boolean removeTagInSafeZone = false;
    public boolean removeTagInPvPDisabledArea = false;
    public boolean removeInvisPotion = false;
    public boolean blockCommandsEnabled = false;
    public List<String> commandNames = new ArrayList<String>();
    public boolean whitelistModeEnabled = false;
    public boolean executeCommandsEnabled = false;
    public List<String> executeCommandList = new ArrayList<String>();
    public boolean blockTeleportationEnabled = false;
    public List<String> disableWorldNames = new ArrayList<String>();
    public boolean killEnabled = false;
    public String updateCheckMessage = "";
    public boolean updateCheckMessageEnabled = false;
    public String MOTDMessage = "";
    public boolean MOTDMessageEnabled = false;
    public String broadcastMessage = "";
    public boolean broadcastMessageEnabled = false;
    public String taggerMessage = "";
    public boolean taggerMessageEnabled = false;
    public String taggedMessage = "";
    public boolean taggedMessageEnabled = false;
    public String untagMessage = "";
    public boolean untagMessageEnabled = false;
    public String tagTimeMessage = "";
    public boolean tagTimeMessageEnabled = false;
    public String notInCombatMessage = "";
    public boolean notInCombatMessageEnabled = false;
    public String actionBarInCombatMessage = "";
    public String actionBarUntagMessage = "";
    public String scoreboardInCombatMessage = "";
    public boolean scoreboardInCombatMessageEnabled = false;
    public String removeModesMessage = "";
    public boolean removeModesMessageEnabled = false;
    public String removeInvisMessage = "";
    public boolean removeInvisMessageEnabled = false;
    public String blockCommandsMessage = "";
    public boolean blockCommandsMessageEnabled = false;
    public String blockTeleportationMessage = "";
    public boolean blockTeleportationMessageEnabled = false;
    public String killMessage = "";
    public boolean killMessageEnabled = false;
    public HashMap<String, Long> taggedPlayers = new HashMap();
    public ArrayList<String> killPlayers = new ArrayList();
    public boolean useNewFactions = false;
    public boolean useLegacyFactions = false;
    public String nmserver;
    public boolean newActionBar = false;
    public int combatlogs;

    public void onEnable() {
        this.checkForPlugins();
        this.initiateVars();
        this.loadSettings();
        this.updateCheck();
        this.initiateListeners();
        this.initiateCmds();
        this.LogHandler();
        this.enableTimer();
        if (this.clConfig.getCLConfig().getBoolean("Metrics")) {
            this.startMetrics();
            this.metricsTimer();
        }
        this.logInfo("[CombatLog] v" + this.getDescription().getVersion() + " has been enabled.");
    }

    public void onDisable() {
        this.taggedPlayers.clear();
        this.logInfo("[CombatLog] Disabled.");
    }

    public void updateCheck() {
        if (this.updateCheckEnabled) {
            if (this.autoDownloadEnabled) {
                this.updater = new Updater((Plugin)this, 45749, this.getFile(), Updater.UpdateType.DEFAULT, false);
                this.logInfo("[CombatLog] Update detected! Downloading now.");
                if (this.updater.getResult() == Updater.UpdateResult.SUCCESS) {
                    this.logInfo("[CombatLog] Successfully downloaded new update.");
                } else {
                    this.logInfo("[CombatLog] Something went wrong. AutoUpdate failed :(");
                }
            } else {
                this.updater = new Updater((Plugin)this, 45749, this.getFile(), Updater.UpdateType.NO_DOWNLOAD, false);
                if (this.updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE) {
                    this.logInfo("[CombatLog] New update at: " + this.updater.getLatestFileLink());
                    this.logInfo("[CombatLog] Use the command 'cl update' to download the newest version.");
                    this.updateAvaliable = true;
                }
            }
        }
    }

    public void update(Player player) {
        this.updater = new Updater((Plugin)this, 45749, this.getFile(), Updater.UpdateType.NO_VERSION_CHECK, false);
        if (this.updater.getResult() == Updater.UpdateResult.SUCCESS) {
            player.sendMessage(this.translateText("&8[&4CombatLog&8] &aUpdate downloaded successfully!"));
        } else if (this.updater.getResult() == Updater.UpdateResult.FAIL_DOWNLOAD) {
            player.sendMessage(this.translateText("&8[&4CombatLog&8] &cFailed to download the newest version!"));
        }
    }

    public void consoleUpdate(ConsoleCommandSender console) {
        this.updater = new Updater((Plugin)this, 45749, this.getFile(), Updater.UpdateType.NO_VERSION_CHECK, false);
        if (this.updater.getResult() == Updater.UpdateResult.SUCCESS) {
            console.sendMessage((Object)ChatColor.GREEN + "[CombatLog] Update downloaded successfully!");
        } else if (this.updater.getResult() == Updater.UpdateResult.FAIL_DOWNLOAD) {
            console.sendMessage((Object)ChatColor.RED + "[CombatLog] Failed to download the newest version!");
        }
    }

    public void checkForPlugins() {
        if (this.getServer().getPluginManager().getPlugin("LibsDisguises") == null) {
            this.usesLibsDisguise = false;
        } else {
            this.logInfo("[CombatLog] LibsDisguises plugin found! Disguise removal will work.");
            this.usesLibsDisguise = true;
        }
        if (this.getServer().getPluginManager().getPlugin("iDisguise") == null) {
            this.usesiDisguise = false;
        } else {
            this.usesiDisguise = true;
            this.logInfo("[CombatLog] iDisguise plugin found! Disguise removal will work.");
        }
        if (this.getServer().getPluginManager().getPlugin("Factions") == null) {
            this.usesFactions = false;
        } else {
            this.usesFactions = true;
            String version = this.getServer().getPluginManager().getPlugin("Factions").getDescription().getVersion();
            if (version.substring(0, 1).equalsIgnoreCase("2")) {
                this.useNewFactions = true;
                this.logInfo("[CombatLog] New Factions plugin v" + version + " found! Safezone regions are now detected.");
            } else if (version.substring(0, 1).equalsIgnoreCase("1")) {
                this.useLegacyFactions = true;
                this.logInfo("[CombatLog] Legacy Factions plugin v" + version + " found! Safezone regions are now detected.");
            }
        }
        if (this.getServer().getPluginManager().getPlugin("WorldGuard") == null) {
            this.usesWorldGuard = false;
        } else {
            this.usesWorldGuard = true;
            this.logInfo("[CombatLog] WorldGuard plugin found! PvP regions are now detected.");
            this.wg = (WorldGuardPlugin)Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
        }
    }

    public void startMetrics() {
        try {
            Metrics metrics = new Metrics((Plugin)this);
            metrics.start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        this.bsm = new BStatsMetrics(this);
        this.bsm.addCustomChart(new BStatsMetrics.SingleLineChart("combatlogs", new Callable<Integer>(){

            @Override
            public Integer call() throws Exception {
                return CombatLog.this.combatlogs;
            }
        }));
    }

    public void enableTimer() {
        this.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)this, new Runnable(){

            @Override
            public void run() {
                Iterator<Map.Entry<String, Long>> iter = CombatLog.this.taggedPlayers.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, Long> c = iter.next();
                    Player player = CombatLog.this.getServer().getPlayer(c.getKey());
                    if (CombatLog.this.useActionBar) {
                        if (CombatLog.this.newActionBar) {
                            CombatLog.this.aBar.sendActionBarNew(player, CombatLog.this.actionBarInCombatMessage.replaceAll("<time>", CombatLog.this.tagTimeRemaining(player.getName())));
                        } else {
                            CombatLog.this.aBar.sendActionBarOld(player, CombatLog.this.actionBarInCombatMessage.replaceAll("<time>", CombatLog.this.tagTimeRemaining(player.getName())));
                        }
                    }
                    if (CombatLog.this.getCurrentTime() - c.getValue() < (long)CombatLog.this.tagDuration) continue;
                    iter.remove();
                    PlayerUntagEvent event = new PlayerUntagEvent(player, PlayerUntagEvent.UntagCause.TIME_EXPIRE);
                    CombatLog.this.getServer().getPluginManager().callEvent((Event)event);
                }
            }
        }, 0L, 20L);
    }

    private void metricsTimer() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask(){

            @Override
            public void run() {
                CombatLog.this.combatlogs = 0;
            }
        }, 301500L, 1800000L);
    }

    public void LogHandler() {
        this.log.addHandler(new Handler(){

            @Override
            public void publish(LogRecord logRecord) {
                String s = logRecord.getMessage();
                if (s.contains(" lost connection: ")) {
                    String[] a = s.split(" ");
                    String DisconnectMsg = a[3];
                    PlayerQuitListener.setDisconnectMsg(DisconnectMsg);
                }
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() throws SecurityException {
            }
        });
    }

    public void initiateCmds() {
        this.getCommand("combatlog").setExecutor((CommandExecutor)this.commandExec);
        this.getCommand("tag").setExecutor((CommandExecutor)this.commandExec);
    }

    public void initiateListeners() {
        this.getServer().getPluginManager().registerEvents((Listener)new EntityDamageByEntityListener(this), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new PlayerCommandPreprocessListener(this), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new PlayerDeathListener(this), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new PlayerInteractListener(this), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new PlayerJoinListener(this), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new PlayerKickListener(this), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new PlayerMoveListener(this), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new PlayerQuitListener(this), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new PlayerTagListener(this), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new PlayerTeleportListener(this), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new PlayerToggleFlightListener(this), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new PlayerUntagListener(this), (Plugin)this);
        if (this.usesiDisguise) {
            Bukkit.getServer().getPluginManager().registerEvents((Listener)new PlayeriDisguiseListener(this), (Plugin)this);
        }
    }

    public void loadSettings() {
        this.clConfig.getCLConfig().options().copyDefaults(true);
        this.clConfig.saveDefault();
        this.clConfig.reloadCLConfig();
        this.vars.getValues();
    }

    public void initiateVars() {
        this.clConfig = new PluginFile(this);
        this.commandExec = new CommandExec(this);
        this.vars = new Variables(this);
        this.aBar = new ActionBar(this);
        this.aBar.getBukkitVersion();
        if (this.usesiDisguise) {
            this.dAPI = this.vars.initDis();
        }
    }

    public void removeDisguise(Player player) {
        if (this.usesiDisguise && this.removeDisguiseEnabled && this.dAPI.isDisguised((OfflinePlayer)player)) {
            this.dAPI.undisguise((OfflinePlayer)player);
            if (this.removeModesMessageEnabled) {
                player.sendMessage(this.translateText(this.removeModesMessage.replaceAll("<mode>", "disguise")));
            }
        }
    }

    public void removeFly(Player player) {
        if (player.isFlying() && this.removeFlyEnabled) {
            player.setFlying(false);
            if (this.removeModesMessageEnabled) {
                player.sendMessage(this.translateText(this.removeModesMessage.replaceAll("<mode>", "flight")));
            }
        }
    }

    public String tagTimeRemaining(String id) {
        return "" + ((long)this.tagDuration - (this.getCurrentTime() - Long.valueOf(this.taggedPlayers.get(id))));
    }

    public long tagTime(String id) {
        return (long)this.tagDuration - (this.getCurrentTime() - Long.valueOf(this.taggedPlayers.get(id)));
    }

    public long getCurrentTime() {
        return System.currentTimeMillis() / 1000L;
    }

    public void broadcastMsg(String string) {
        this.getServer().broadcastMessage(this.translateText(string));
    }

    public void logInfo(String string) {
        this.log.info(this.translateText(string));
    }

    public String translateText(String string) {
        return ChatColor.translateAlternateColorCodes((char)'&', (String)string);
    }
}

