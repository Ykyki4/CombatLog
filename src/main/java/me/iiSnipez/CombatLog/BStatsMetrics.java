/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.RegisteredServiceProvider
 *  org.bukkit.plugin.ServicePriority
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.json.simple.JSONArray
 *  org.json.simple.JSONObject
 */
package me.iiSnipez.CombatLog;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.zip.GZIPOutputStream;
import javax.net.ssl.HttpsURLConnection;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class BStatsMetrics {
    public static final int B_STATS_VERSION = 1;
    private static final String URL = "https://bStats.org/submitData/bukkit";
    private static boolean logFailedRequests;
    private static String serverUUID;
    private final JavaPlugin plugin;
    private final List<CustomChart> charts = new ArrayList<CustomChart>();

    static {
        if (System.getProperty("bstats.relocatecheck") == null || !System.getProperty("bstats.relocatecheck").equals("false")) {
            String defaultPackage = new String(new byte[]{111, 114, 103, 46, 98, 115, 116, 97, 116, 115, 46, 98, 117, 107, 107, 105, 116});
            String examplePackage = new String(new byte[]{121, 111, 117, 114, 46, 112, 97, 99, 107, 97, 103, 101});
            if (BStatsMetrics.class.getPackage().getName().equals(defaultPackage) || BStatsMetrics.class.getPackage().getName().equals(examplePackage)) {
                throw new IllegalStateException("bStats Metrics class has not been relocated correctly!");
            }
        }
    }

    public BStatsMetrics(JavaPlugin plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null!");
        }
        this.plugin = plugin;
        File bStatsFolder = new File(plugin.getDataFolder().getParentFile(), "bStats");
        File configFile = new File(bStatsFolder, "config.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration((File)configFile);
        if (!config.isSet("serverUuid")) {
            config.addDefault("enabled", (Object)true);
            config.addDefault("serverUuid", (Object)UUID.randomUUID().toString());
            config.addDefault("logFailedRequests", (Object)false);
            config.options().header("bStats collects some data for plugin authors like how many servers are using their plugins.\nTo honor their work, you should not disable it.\nThis has nearly no effect on the server performance!\nCheck out https://bStats.org/ to learn more :)").copyDefaults(true);
            try {
                config.save(configFile);
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        serverUUID = config.getString("serverUuid");
        logFailedRequests = config.getBoolean("logFailedRequests", false);
        if (config.getBoolean("enabled", true)) {
            boolean found = false;
            for (Class service : Bukkit.getServicesManager().getKnownServices()) {
                try {
                    service.getField("B_STATS_VERSION");
                    found = true;
                    break;
                }
                catch (NoSuchFieldException noSuchFieldException) {
                    // empty catch block
                }
            }
            Bukkit.getServicesManager().register(BStatsMetrics.class, this, plugin, ServicePriority.Normal);
            if (!found) {
                this.startSubmitting();
            }
        }
    }

    public void addCustomChart(CustomChart chart) {
        if (chart == null) {
            throw new IllegalArgumentException("Chart cannot be null!");
        }
        this.charts.add(chart);
    }

    private void startSubmitting() {
        final Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask(){

            @Override
            public void run() {
                if (!BStatsMetrics.this.plugin.isEnabled()) {
                    timer.cancel();
                    return;
                }
                Bukkit.getScheduler().runTask((Plugin)BStatsMetrics.this.plugin, new Runnable(){

                    @Override
                    public void run() {
                        BStatsMetrics.this.submitData();
                    }
                });
            }
        }, 300000L, 1800000L);
    }

    public JSONObject getPluginData() {
        JSONObject data = new JSONObject();
        String pluginName = this.plugin.getDescription().getName();
        String pluginVersion = this.plugin.getDescription().getVersion();
        data.put((Object)"pluginName", (Object)pluginName);
        data.put((Object)"pluginVersion", (Object)pluginVersion);
        JSONArray customCharts = new JSONArray();
        for (CustomChart customChart : this.charts) {
            JSONObject chart = customChart.getRequestJsonObject();
            if (chart == null) continue;
            customCharts.add((Object)chart);
        }
        data.put((Object)"customCharts", (Object)customCharts);
        return data;
    }

    private JSONObject getServerData() {
        int playerAmount;
        try {
            Method onlinePlayersMethod = Class.forName("org.bukkit.Server").getMethod("getOnlinePlayers", new Class[0]);
            playerAmount = onlinePlayersMethod.getReturnType().equals(Collection.class) ? ((Collection)onlinePlayersMethod.invoke((Object)Bukkit.getServer(), new Object[0])).size() : ((Player[])onlinePlayersMethod.invoke((Object)Bukkit.getServer(), new Object[0])).length;
        }
        catch (Exception e) {
            playerAmount = Bukkit.getOnlinePlayers().size();
        }
        int onlineMode = Bukkit.getOnlineMode() ? 1 : 0;
        String bukkitVersion = Bukkit.getVersion();
        bukkitVersion = bukkitVersion.substring(bukkitVersion.indexOf("MC: ") + 4, bukkitVersion.length() - 1);
        String javaVersion = System.getProperty("java.version");
        String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");
        String osVersion = System.getProperty("os.version");
        int coreCount = Runtime.getRuntime().availableProcessors();
        JSONObject data = new JSONObject();
        data.put((Object)"serverUUID", (Object)serverUUID);
        data.put((Object)"playerAmount", (Object)playerAmount);
        data.put((Object)"onlineMode", (Object)onlineMode);
        data.put((Object)"bukkitVersion", (Object)bukkitVersion);
        data.put((Object)"javaVersion", (Object)javaVersion);
        data.put((Object)"osName", (Object)osName);
        data.put((Object)"osArch", (Object)osArch);
        data.put((Object)"osVersion", (Object)osVersion);
        data.put((Object)"coreCount", (Object)coreCount);
        return data;
    }

    private void submitData() {
        final JSONObject data = this.getServerData();
        JSONArray pluginData = new JSONArray();
        Iterator var4 = Bukkit.getServicesManager().getKnownServices().iterator();

        while(var4.hasNext()) {
            Class<?> service = (Class)var4.next();

            try {
                service.getField("B_STATS_VERSION");
                Iterator var6 = Bukkit.getServicesManager().getRegistrations(service).iterator();

                while(var6.hasNext()) {
                    RegisteredServiceProvider<?> provider = (RegisteredServiceProvider)var6.next();

                    try {
                        pluginData.add(provider.getService().getMethod("getPluginData").invoke(provider.getProvider()));
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NullPointerException var8) {
                    }
                }
            } catch (NoSuchFieldException var9) {
            }
        }

        data.put("plugins", pluginData);
        (new Thread(new Runnable() {
            public void run() {
                try {
                    BStatsMetrics.sendData(data);
                } catch (Exception var2) {
                    if (BStatsMetrics.logFailedRequests) {
                        BStatsMetrics.this.plugin.getLogger().log(Level.WARNING, "Could not submit plugin stats of " + BStatsMetrics.this.plugin.getName(), var2);
                    }
                }

            }
        })).start();
    }

    private static void sendData(JSONObject data) throws Exception {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null!");
        }
        if (Bukkit.isPrimaryThread()) {
            throw new IllegalAccessException("This method must not be called from the main thread!");
        }
        HttpsURLConnection connection = (HttpsURLConnection)new URL(URL).openConnection();
        byte[] compressedData = BStatsMetrics.compress(data.toString());
        connection.setRequestMethod("POST");
        connection.addRequestProperty("Accept", "application/json");
        connection.addRequestProperty("Connection", "close");
        connection.addRequestProperty("Content-Encoding", "gzip");
        connection.addRequestProperty("Content-Length", String.valueOf(compressedData.length));
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", "MC-Server/1");
        connection.setDoOutput(true);
        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        outputStream.write(compressedData);
        outputStream.flush();
        outputStream.close();
        connection.getInputStream().close();
    }

    private static byte[] compress(String str) throws IOException {
        if (str == null) {
            return null;
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(outputStream);
        gzip.write(str.getBytes("UTF-8"));
        gzip.close();
        return outputStream.toByteArray();
    }

    public static class AdvancedBarChart
    extends CustomChart {
        private final Callable<Map<String, int[]>> callable;

        public AdvancedBarChart(String chartId, Callable<Map<String, int[]>> callable) {
            super(chartId);
            this.callable = callable;
        }

        @Override
        protected JSONObject getChartData() throws Exception {
            JSONObject data = new JSONObject();
            JSONObject values = new JSONObject();
            Map<String, int[]> map = this.callable.call();
            if (map == null || map.isEmpty()) {
                return null;
            }
            boolean allSkipped = true;
            for (Map.Entry<String, int[]> entry : map.entrySet()) {
                if (entry.getValue().length == 0) continue;
                allSkipped = false;
                JSONArray categoryValues = new JSONArray();
                for (int categoryValue : entry.getValue()) {
                    categoryValues.add((Object)categoryValue);
                }
                values.put((Object)entry.getKey(), (Object)categoryValues);
            }
            if (allSkipped) {
                return null;
            }
            data.put((Object)"values", (Object)values);
            return data;
        }
    }

    public static class AdvancedPie
    extends CustomChart {
        private final Callable<Map<String, Integer>> callable;

        public AdvancedPie(String chartId, Callable<Map<String, Integer>> callable) {
            super(chartId);
            this.callable = callable;
        }

        @Override
        protected JSONObject getChartData() throws Exception {
            JSONObject data = new JSONObject();
            JSONObject values = new JSONObject();
            Map<String, Integer> map = this.callable.call();
            if (map == null || map.isEmpty()) {
                return null;
            }
            boolean allSkipped = true;
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                if (entry.getValue() == 0) continue;
                allSkipped = false;
                values.put((Object)entry.getKey(), (Object)entry.getValue());
            }
            if (allSkipped) {
                return null;
            }
            data.put((Object)"values", (Object)values);
            return data;
        }
    }

    public static abstract class CustomChart {
        final String chartId;

        CustomChart(String chartId) {
            if (chartId == null || chartId.isEmpty()) {
                throw new IllegalArgumentException("ChartId cannot be null or empty!");
            }
            this.chartId = chartId;
        }

        private JSONObject getRequestJsonObject() {
            JSONObject data;
            JSONObject chart;
            block4: {
                chart = new JSONObject();
                chart.put((Object)"chartId", (Object)this.chartId);
                try {
                    data = this.getChartData();
                    if (data != null) break block4;
                    return null;
                }
                catch (Throwable t) {
                    if (logFailedRequests) {
                        Bukkit.getLogger().log(Level.WARNING, "Failed to get data for custom chart with id " + this.chartId, t);
                    }
                    return null;
                }
            }
            chart.put((Object)"data", (Object)data);
            return chart;
        }

        protected abstract JSONObject getChartData() throws Exception;
    }

    public static class DrilldownPie
    extends CustomChart {
        private final Callable<Map<String, Map<String, Integer>>> callable;

        public DrilldownPie(String chartId, Callable<Map<String, Map<String, Integer>>> callable) {
            super(chartId);
            this.callable = callable;
        }

        @Override
        public JSONObject getChartData() throws Exception {
            JSONObject data = new JSONObject();
            JSONObject values = new JSONObject();
            Map<String, Map<String, Integer>> map = this.callable.call();
            if (map == null || map.isEmpty()) {
                return null;
            }
            boolean reallyAllSkipped = true;
            for (Map.Entry<String, Map<String, Integer>> entryValues : map.entrySet()) {
                JSONObject value = new JSONObject();
                boolean allSkipped = true;
                for (Map.Entry<String, Integer> valueEntry : map.get(entryValues.getKey()).entrySet()) {
                    value.put((Object)valueEntry.getKey(), (Object)valueEntry.getValue());
                    allSkipped = false;
                }
                if (allSkipped) continue;
                reallyAllSkipped = false;
                values.put((Object)entryValues.getKey(), (Object)value);
            }
            if (reallyAllSkipped) {
                return null;
            }
            data.put((Object)"values", (Object)values);
            return data;
        }
    }

    public static class MultiLineChart
    extends CustomChart {
        private final Callable<Map<String, Integer>> callable;

        public MultiLineChart(String chartId, Callable<Map<String, Integer>> callable) {
            super(chartId);
            this.callable = callable;
        }

        @Override
        protected JSONObject getChartData() throws Exception {
            JSONObject data = new JSONObject();
            JSONObject values = new JSONObject();
            Map<String, Integer> map = this.callable.call();
            if (map == null || map.isEmpty()) {
                return null;
            }
            boolean allSkipped = true;
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                if (entry.getValue() == 0) continue;
                allSkipped = false;
                values.put((Object)entry.getKey(), (Object)entry.getValue());
            }
            if (allSkipped) {
                return null;
            }
            data.put((Object)"values", (Object)values);
            return data;
        }
    }

    public static class SimpleBarChart
    extends CustomChart {
        private final Callable<Map<String, Integer>> callable;

        public SimpleBarChart(String chartId, Callable<Map<String, Integer>> callable) {
            super(chartId);
            this.callable = callable;
        }

        @Override
        protected JSONObject getChartData() throws Exception {
            JSONObject data = new JSONObject();
            JSONObject values = new JSONObject();
            Map<String, Integer> map = this.callable.call();
            if (map == null || map.isEmpty()) {
                return null;
            }
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                JSONArray categoryValues = new JSONArray();
                categoryValues.add((Object)entry.getValue());
                values.put((Object)entry.getKey(), (Object)categoryValues);
            }
            data.put((Object)"values", (Object)values);
            return data;
        }
    }

    public static class SimplePie
    extends CustomChart {
        private final Callable<String> callable;

        public SimplePie(String chartId, Callable<String> callable) {
            super(chartId);
            this.callable = callable;
        }

        @Override
        protected JSONObject getChartData() throws Exception {
            JSONObject data = new JSONObject();
            String value = this.callable.call();
            if (value == null || value.isEmpty()) {
                return null;
            }
            data.put((Object)"value", (Object)value);
            return data;
        }
    }

    public static class SingleLineChart
    extends CustomChart {
        private final Callable<Integer> callable;

        public SingleLineChart(String chartId, Callable<Integer> callable) {
            super(chartId);
            this.callable = callable;
        }

        @Override
        protected JSONObject getChartData() throws Exception {
            JSONObject data = new JSONObject();
            int value = this.callable.call();
            if (value == 0) {
                return null;
            }
            data.put((Object)"value", (Object)value);
            return data;
        }
    }
}

