/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 */
package me.iiSnipez.CombatLog;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import me.iiSnipez.CombatLog.CombatLog;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ActionBar {
    public String nmserver;
    public boolean old = false;
    CombatLog plugin;

    public ActionBar(CombatLog plugin) {
        this.plugin = plugin;
    }

    public void getBukkitVersion() {
        this.nmserver = Bukkit.getServer().getClass().getPackage().getName();
        this.nmserver = this.nmserver.substring(this.nmserver.lastIndexOf(".") + 1);
        if (this.nmserver.equalsIgnoreCase("v1_8_R1") || this.nmserver.startsWith("v1_7_")) {
            this.old = true;
        }
    }

    public void sendActionBarOld(Player player, String msg) {
        if (!player.isOnline()) {
            return;
        }
        try {
            Object ppoc;
            Class<?> c3;
            Class<?> c2;
            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + this.nmserver + ".entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast((Object)player);
            Class<?> c4 = Class.forName("net.minecraft.server." + this.nmserver + ".PacketPlayOutChat");
            Class<?> c5 = Class.forName("net.minecraft.server." + this.nmserver + ".Packet");
            if (this.old) {
                c2 = Class.forName("net.minecraft.server." + this.nmserver + ".ChatSerializer");
                c3 = Class.forName("net.minecraft.server." + this.nmserver + ".IChatBaseComponent");
                Method m3 = c2.getDeclaredMethod("a", String.class);
                Object cbc = c3.cast(m3.invoke(c2, "{\"text\": \"" + msg + "\"}"));
                ppoc = c4.getConstructor(c3, Byte.TYPE).newInstance(cbc, (byte)2);
            } else {
                c2 = Class.forName("net.minecraft.server." + this.nmserver + ".ChatComponentText");
                c3 = Class.forName("net.minecraft.server." + this.nmserver + ".IChatBaseComponent");
                Object o = c2.getConstructor(String.class).newInstance(msg);
                ppoc = c4.getConstructor(c3, Byte.TYPE).newInstance(o, (byte)2);
            }
            Method m1 = craftPlayerClass.getDeclaredMethod("getHandle", new Class[0]);
            Object h = m1.invoke(craftPlayer, new Object[0]);
            Field f1 = h.getClass().getDeclaredField("playerConnection");
            Object pc = f1.get(h);
            Method m5 = pc.getClass().getDeclaredMethod("sendPacket", c5);
            m5.invoke(pc, ppoc);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sendActionBarNew(Player player, String msg) {
        if (!player.isOnline()) {
            return;
        }
        try {
            Class<?> playerClass = Class.forName("org.bukkit.craftbukkit." + this.nmserver + ".entity.CraftPlayer");
            Object p = playerClass.cast((Object)player);
            Class<?> c2 = Class.forName("net.minecraft.server." + this.nmserver + ".PacketPlayOutChat");
            Class<?> c3 = Class.forName("net.minecraft.server." + this.nmserver + ".Packet");
            Class<?> c4 = Class.forName("net.minecraft.server." + this.nmserver + ".ChatComponentText");
            Class<?> c5 = Class.forName("net.minecraft.server." + this.nmserver + ".IChatBaseComponent");
            Class<?> chatMessageTypeClass = Class.forName("net.minecraft.server." + this.nmserver + ".ChatMessageType");
            Object[] chatMessageTypes = chatMessageTypeClass.getEnumConstants();
            Object chatMessageType = null;
            Object[] arrobj = chatMessageTypes;
            int n = chatMessageTypes.length;
            for (int i = 0; i < n; ++i) {
                Object obj = arrobj[i];
                if (!obj.toString().equals("GAME_INFO")) continue;
                chatMessageType = obj;
            }
            Object o = c4.getConstructor(String.class).newInstance(this.plugin.translateText(msg));
            Object ppoc = c2.getConstructor(c5, chatMessageTypeClass).newInstance(o, chatMessageType);
            Method m1 = playerClass.getDeclaredMethod("getHandle", new Class[0]);
            Object h = m1.invoke(p, new Object[0]);
            Field f1 = h.getClass().getDeclaredField("playerConnection");
            Object pc = f1.get(h);
            Method m2 = pc.getClass().getDeclaredMethod("sendPacket", c3);
            m2.invoke(pc, ppoc);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

