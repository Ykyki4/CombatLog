/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.json.simple.JSONArray
 *  org.json.simple.JSONObject
 *  org.json.simple.JSONValue
 */
package me.iiSnipez.CombatLog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Updater {
    private static final String TITLE_VALUE = "name";
    private static final String LINK_VALUE = "downloadUrl";
    private static final String TYPE_VALUE = "releaseType";
    private static final String VERSION_VALUE = "gameVersion";
    private static final String QUERY = "/servermods/files?projectIds=";
    private static final String HOST = "https://api.curseforge.com";
    private static final String USER_AGENT = "Updater (by Gravity)";
    private static final String DELIMETER = "^v|[\\s_-]v";
    private static final String[] NO_UPDATE_TAG = new String[]{"-DEV", "-PRE", "-SNAPSHOT"};
    private static final int BYTE_SIZE = 1024;
    private static final String API_KEY_CONFIG_KEY = "api-key";
    private static final String DISABLE_CONFIG_KEY = "disable";
    private static final String API_KEY_DEFAULT = "PUT_API_KEY_HERE";
    private static final boolean DISABLE_DEFAULT = false;
    private final Plugin plugin;
    private final UpdateType type;
    private final boolean announce;
    private final File file;
    private final File updateFolder;
    private final UpdateCallback callback;
    private int id = -1;
    private String apiKey = null;
    private String versionName;
    private String versionLink;
    private String versionType;
    private String versionGameVersion;
    private URL url;
    private Thread thread;
    private UpdateResult result = UpdateResult.SUCCESS;

    public Updater(Plugin plugin, int id, File file, UpdateType type, boolean announce) {
        this(plugin, id, file, type, null, announce);
    }

    public Updater(Plugin plugin, int id, File file, UpdateType type, UpdateCallback callback) {
        this(plugin, id, file, type, callback, false);
    }

    public Updater(Plugin plugin, int id, File file, UpdateType type, UpdateCallback callback, boolean announce) {
        this.plugin = plugin;
        this.type = type;
        this.announce = announce;
        this.file = file;
        this.id = id;
        this.updateFolder = this.plugin.getServer().getUpdateFolderFile();
        this.callback = callback;
        File pluginFile = this.plugin.getDataFolder().getParentFile();
        File updaterFile = new File(pluginFile, "Updater");
        File updaterConfigFile = new File(updaterFile, "config.yml");
        YamlConfiguration config = new YamlConfiguration();
        config.options().header("This configuration file affects all plugins using the Updater system (version 2+ - http://forums.bukkit.org/threads/96681/ )\nIf you wish to use your API key, read http://wiki.bukkit.org/ServerMods_API and place it below.\nSome updating systems will not adhere to the disabled value, but these may be turned off in their plugin's configuration.");
        config.addDefault(API_KEY_CONFIG_KEY, (Object)API_KEY_DEFAULT);
        config.addDefault(DISABLE_CONFIG_KEY, (Object)false);
        if (!updaterFile.exists()) {
            this.fileIOOrError(updaterFile, updaterFile.mkdir(), true);
        }
        boolean createFile = !updaterConfigFile.exists();
        try {
            if (createFile) {
                this.fileIOOrError(updaterConfigFile, updaterConfigFile.createNewFile(), true);
                config.options().copyDefaults(true);
                config.save(updaterConfigFile);
            } else {
                config.load(updaterConfigFile);
            }
        }
        catch (Exception e) {
            String message = createFile ? "The updater could not create configuration at " + updaterFile.getAbsolutePath() : "The updater could not load configuration at " + updaterFile.getAbsolutePath();
            this.plugin.getLogger().log(Level.SEVERE, message, e);
        }
        if (config.getBoolean(DISABLE_CONFIG_KEY)) {
            this.result = UpdateResult.DISABLED;
            return;
        }
        String key = config.getString(API_KEY_CONFIG_KEY);
        if (API_KEY_DEFAULT.equalsIgnoreCase(key) || "".equals(key)) {
            key = null;
        }
        this.apiKey = key;
        try {
            this.url = new URL("https://api.curseforge.com/servermods/files?projectIds=" + this.id);
        }
        catch (MalformedURLException e) {
            this.plugin.getLogger().log(Level.SEVERE, "The project ID provided for updating, " + this.id + " is invalid.", e);
            this.result = UpdateResult.FAIL_BADID;
        }
        if (this.result != UpdateResult.FAIL_BADID) {
            this.thread = new Thread(new UpdateRunnable());
            this.thread.start();
        } else {
            this.runUpdater();
        }
    }

    public UpdateResult getResult() {
        this.waitForThread();
        return this.result;
    }

    public ReleaseType getLatestType() {
        this.waitForThread();
        if (this.versionType != null) {
            for (ReleaseType type : ReleaseType.values()) {
                if (!this.versionType.equalsIgnoreCase(type.name())) continue;
                return type;
            }
        }
        return null;
    }

    public String getLatestGameVersion() {
        this.waitForThread();
        return this.versionGameVersion;
    }

    public String getLatestName() {
        this.waitForThread();
        return this.versionName;
    }

    public String getLatestFileLink() {
        this.waitForThread();
        return this.versionLink;
    }

    private void waitForThread() {
        if (this.thread != null && this.thread.isAlive()) {
            try {
                this.thread.join();
            }
            catch (InterruptedException e) {
                this.plugin.getLogger().log(Level.SEVERE, null, e);
            }
        }
    }

    private void saveFile(String file) {
        File folder = this.updateFolder;
        this.deleteOldFiles();
        if (!folder.exists()) {
            this.fileIOOrError(folder, folder.mkdir(), true);
        }
        this.downloadFile();
        File dFile = new File(folder.getAbsolutePath(), file);
        if (dFile.getName().endsWith(".zip")) {
            this.unzip(dFile.getAbsolutePath());
        }
        if (this.announce) {
            this.plugin.getLogger().info("Finished updating.");
        }
    }

    private void downloadFile() {
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try {
            try {
                int count;
                URL fileUrl = new URL(this.versionLink);
                int fileLength = fileUrl.openConnection().getContentLength();
                in = new BufferedInputStream(fileUrl.openStream());
                fout = new FileOutputStream(new File(this.updateFolder, this.file.getName()));
                byte[] data = new byte[1024];
                if (this.announce) {
                    this.plugin.getLogger().info("About to download a new update: " + this.versionName);
                }
                long downloaded = 0L;
                while ((count = in.read(data, 0, 1024)) != -1) {
                    fout.write(data, 0, count);
                    int percent = (int)((downloaded += (long)count) * 100L / (long)fileLength);
                    if (!this.announce || percent % 10 != 0) continue;
                    this.plugin.getLogger().info("Downloading update: " + percent + "% of " + fileLength + " bytes.");
                }
            }
            catch (Exception ex) {
                this.plugin.getLogger().log(Level.WARNING, "The auto-updater tried to download a new update, but was unsuccessful.", ex);
                this.result = UpdateResult.FAIL_DOWNLOAD;
                try {
                    if (in != null) {
                        in.close();
                    }
                }
                catch (IOException ex2) {
                    this.plugin.getLogger().log(Level.SEVERE, null, ex2);
                }
                try {
                    if (fout != null) {
                        fout.close();
                    }
                }
                catch (IOException ex3) {
                    this.plugin.getLogger().log(Level.SEVERE, null, ex3);
                }
            }
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (IOException ex) {
                this.plugin.getLogger().log(Level.SEVERE, null, ex);
            }
            try {
                if (fout != null) {
                    fout.close();
                }
            }
            catch (IOException ex) {
                this.plugin.getLogger().log(Level.SEVERE, null, ex);
            }
        }
    }

    private void deleteOldFiles() {
        File[] list;
        File[] arrfile = list = this.listFilesOrError(this.updateFolder);
        int n = list.length;
        for (int i = 0; i < n; ++i) {
            File xFile = arrfile[i];
            if (!xFile.getName().endsWith(".zip")) continue;
            this.fileIOOrError(xFile, xFile.mkdir(), true);
        }
    }

    private void unzip(String file) {
        File fSourceZip = new File(file);
        try {
            try {
                String zipPath = file.substring(0, file.length() - 4);
                ZipFile zipFile = new ZipFile(fSourceZip);
                Enumeration<? extends ZipEntry> e = zipFile.entries();
                while (e.hasMoreElements()) {
                    int b;
                    ZipEntry entry = e.nextElement();
                    File destinationFilePath = new File(zipPath, entry.getName());
                    this.fileIOOrError(destinationFilePath.getParentFile(), destinationFilePath.getParentFile().mkdirs(), true);
                    if (entry.isDirectory()) continue;
                    BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
                    byte[] buffer = new byte[1024];
                    FileOutputStream fos = new FileOutputStream(destinationFilePath);
                    BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
                    while ((b = bis.read(buffer, 0, 1024)) != -1) {
                        bos.write(buffer, 0, b);
                    }
                    bos.flush();
                    bos.close();
                    bis.close();
                    String name = destinationFilePath.getName();
                    if (!name.endsWith(".jar") || !this.pluginExists(name)) continue;
                    File output = new File(this.updateFolder, name);
                    this.fileIOOrError(output, destinationFilePath.renameTo(output), true);
                }
                zipFile.close();
                this.moveNewZipFiles(zipPath);
            }
            catch (IOException e) {
                this.plugin.getLogger().log(Level.SEVERE, "The auto-updater tried to unzip a new update file, but was unsuccessful.", e);
                this.result = UpdateResult.FAIL_DOWNLOAD;
                this.fileIOOrError(fSourceZip, fSourceZip.delete(), false);
            }
        }
        finally {
            this.fileIOOrError(fSourceZip, fSourceZip.delete(), false);
        }
    }

    private void moveNewZipFiles(String zipPath) {
        File[] list;
        File[] arrfile = list = this.listFilesOrError(new File(zipPath));
        int n = list.length;
        for (int i = 0; i < n; ++i) {
            File dFile = arrfile[i];
            if (dFile.isDirectory() && this.pluginExists(dFile.getName())) {
                File oFile = new File(this.plugin.getDataFolder().getParent(), dFile.getName());
                File[] dList = this.listFilesOrError(dFile);
                File[] oList = this.listFilesOrError(oFile);
                File[] arrfile2 = dList;
                int n2 = dList.length;
                for (int j = 0; j < n2; ++j) {
                    File cFile = arrfile2[j];
                    boolean found = false;
                    File[] arrfile3 = oList;
                    int n3 = oList.length;
                    for (int k = 0; k < n3; ++k) {
                        File xFile = arrfile3[k];
                        if (!xFile.getName().equals(cFile.getName())) continue;
                        found = true;
                        break;
                    }
                    if (!found) {
                        File output = new File(oFile, cFile.getName());
                        this.fileIOOrError(output, cFile.renameTo(output), true);
                        continue;
                    }
                    this.fileIOOrError(cFile, cFile.delete(), false);
                }
            }
            this.fileIOOrError(dFile, dFile.delete(), false);
        }
        File zip = new File(zipPath);
        this.fileIOOrError(zip, zip.delete(), false);
    }

    private boolean pluginExists(String name) {
        File[] plugins;
        File[] arrfile = plugins = this.listFilesOrError(new File("plugins"));
        int n = plugins.length;
        for (int i = 0; i < n; ++i) {
            File file = arrfile[i];
            if (!file.getName().equals(name)) continue;
            return true;
        }
        return false;
    }

    private boolean versionCheck() {
        String title = this.versionName;
        if (this.type != UpdateType.NO_VERSION_CHECK) {
            String localVersion = this.plugin.getDescription().getVersion();
            if (title.split(DELIMETER).length >= 2) {
                String remoteVersion = title.split(DELIMETER)[title.split(DELIMETER).length - 1].split(" ")[0];
                if (this.hasTag(localVersion) || !this.shouldUpdate(localVersion, remoteVersion)) {
                    this.result = UpdateResult.NO_UPDATE;
                    return false;
                }
            } else {
                String authorInfo = this.plugin.getDescription().getAuthors().isEmpty() ? "" : " (" + (String)this.plugin.getDescription().getAuthors().get(0) + ")";
                this.plugin.getLogger().warning("The author of this plugin" + authorInfo + " has misconfigured their Auto Update system");
                this.plugin.getLogger().warning("File versions should follow the format 'PluginName vVERSION'");
                this.plugin.getLogger().warning("Please notify the author of this error.");
                this.result = UpdateResult.FAIL_NOVERSION;
                return false;
            }
        }
        return true;
    }

    public boolean shouldUpdate(String localVersion, String remoteVersion) {
        return !localVersion.equalsIgnoreCase(remoteVersion);
    }

    private boolean hasTag(String version) {
        String[] arrstring = NO_UPDATE_TAG;
        int n = NO_UPDATE_TAG.length;
        for (int i = 0; i < n; ++i) {
            String string = arrstring[i];
            if (!version.contains(string)) continue;
            return true;
        }
        return false;
    }

    private boolean read() {
        JSONArray array;
        block6: {
            try {
                URLConnection conn = this.url.openConnection();
                conn.setConnectTimeout(5000);
                if (this.apiKey != null) {
                    conn.addRequestProperty("X-API-Key", this.apiKey);
                }
                conn.addRequestProperty("User-Agent", USER_AGENT);
                conn.setDoOutput(true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String response = reader.readLine();
                array = (JSONArray)JSONValue.parse((String)response);
                if (!array.isEmpty()) break block6;
                this.plugin.getLogger().warning("The updater could not find any files for the project id " + this.id);
                this.result = UpdateResult.FAIL_BADID;
                return false;
            }
            catch (IOException e) {
                if (e.getMessage().contains("HTTP response code: 403")) {
                    this.plugin.getLogger().severe("dev.bukkit.org rejected the API key provided in plugins/Updater/config.yml");
                    this.plugin.getLogger().severe("Please double-check your configuration to ensure it is correct.");
                    this.result = UpdateResult.FAIL_APIKEY;
                } else {
                    this.plugin.getLogger().severe("The updater could not contact dev.bukkit.org for updating.");
                    this.plugin.getLogger().severe("If you have not recently modified your configuration and this is the first time you are seeing this message, the site may be experiencing temporary downtime.");
                    this.result = UpdateResult.FAIL_DBO;
                }
                this.plugin.getLogger().log(Level.SEVERE, null, e);
                return false;
            }
        }
        JSONObject latestUpdate = (JSONObject)array.get(array.size() - 1);
        this.versionName = (String)latestUpdate.get((Object)TITLE_VALUE);
        this.versionLink = (String)latestUpdate.get((Object)LINK_VALUE);
        this.versionType = (String)latestUpdate.get((Object)TYPE_VALUE);
        this.versionGameVersion = (String)latestUpdate.get((Object)VERSION_VALUE);
        return true;
    }

    private void fileIOOrError(File file, boolean result, boolean create) {
        if (!result) {
            this.plugin.getLogger().severe("The updater could not " + (create ? "create" : "delete") + " file at: " + file.getAbsolutePath());
        }
    }

    private File[] listFilesOrError(File folder) {
        File[] contents = folder.listFiles();
        if (contents == null) {
            this.plugin.getLogger().severe("The updater could not access files at: " + this.updateFolder.getAbsolutePath());
            return new File[0];
        }
        return contents;
    }

    private void runUpdater() {
        if (this.url != null && this.read() && this.versionCheck()) {
            if (this.versionLink != null && this.type != UpdateType.NO_DOWNLOAD) {
                String name = this.file.getName();
                if (this.versionLink.endsWith(".zip")) {
                    name = this.versionLink.substring(this.versionLink.lastIndexOf("/") + 1);
                }
                this.saveFile(name);
            } else {
                this.result = UpdateResult.UPDATE_AVAILABLE;
            }
        }
        if (this.callback != null) {
            new BukkitRunnable(){

                public void run() {
                    Updater.this.runCallback();
                }
            }.runTask(this.plugin);
        }
    }

    private void runCallback() {
        this.callback.onFinish(this);
    }

    public static enum ReleaseType {
        ALPHA,
        BETA,
        RELEASE;

    }

    public static interface UpdateCallback {
        public void onFinish(Updater var1);
    }

    public static enum UpdateResult {
        SUCCESS,
        NO_UPDATE,
        DISABLED,
        FAIL_DOWNLOAD,
        FAIL_DBO,
        FAIL_NOVERSION,
        FAIL_BADID,
        FAIL_APIKEY,
        UPDATE_AVAILABLE;

    }

    private class UpdateRunnable
    implements Runnable {
        private UpdateRunnable() {
        }

        @Override
        public void run() {
            Updater.this.runUpdater();
        }
    }

    public static enum UpdateType {
        DEFAULT,
        NO_VERSION_CHECK,
        NO_DOWNLOAD;

    }
}

