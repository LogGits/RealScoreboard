package josegamerpt.realscoreboard;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import josegamerpt.realscoreboard.config.Config;
import josegamerpt.realscoreboard.config.ConfigUpdater;
import josegamerpt.realscoreboard.managers.AnimationManager;
import josegamerpt.realscoreboard.managers.PlayerManager;
import josegamerpt.realscoreboard.nms.*;
import josegamerpt.realscoreboard.utils.TPS;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


public class RealScoreboard extends JavaPlugin {
    public static NMS nms;
    public static Permission perms = null;
    public static Economy Economia = null;
    public static Chat chat = null;
    public static int abcompatible = 0;
    public static int vault = 0;
    public static int placeholderapi = 0;
    public static Logger log = Bukkit.getLogger();
    public static Plugin pl;
    static String prefix = "§bReal§9Scoreboard §7| §r";
    PluginDescriptionFile desc = getDescription();
    PluginManager pm = Bukkit.getPluginManager();
    static String name = "[ RealScoreboard ]";
    String sv;
    String header = "------------------- RealScoreboard -------------------";

    public static String getPrefix() {
        return prefix;
    }

    public static void log(String s) {
        System.out.print(s);
    }

    public static void logPrefix(String s) {
        System.out.print(name + " " + s);
    }

    public static String getServerVersion() {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        return version;
    }

    public void onEnable() {
        pl = this;

        System.out.print(header);

        log("Checking the server version.");
        if (setupNMS()) {
            sv = getServerVersion();

            if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
                String sv = getServerVersion();
                vault = 1;
                setupEconomy();
                setupPermissions();
                setupChat();
            }
            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                placeholderapi = 1;
            }

            log("Setting up the configuration.");
            saveDefaultConfig();
            Config.setup(this);
            log("Your config version is: " + ConfigUpdater.getConfigVersion());
            ConfigUpdater.updateConfig(ConfigUpdater.getConfigVersion());

            log("Registering Events.");
            pm.registerEvents(new PlayerManager(), this);

            log("Registering Commands.");
            getCommand("realscoreboard").setExecutor(new RSBcmd());

            log("Starting up the Scoreboard.");
            AnimationManager.startAnimations();

            Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new TPS(), 100L, 1L);

            for (Player p : Bukkit.getOnlinePlayers()) {
                PlayerManager.loadPlayer(p);
            }

            logList(Arrays.asList("Loaded sucessfully.", "Server version: " + sv,
                    "If you have any questions or need support, feel free to message JoseGamer_PT", "https://www.spigotmc.org/members/josegamer_pt.40267/"));

            System.out.print(header);
        } else {
            logList(Arrays.asList("Failed to load RealScoreboard.",
                    "Your server version (" + sv + ") is not compatible with RealScoreboard.",
                    "If you think this is a bug, please contact JoseGamer_PT.", "https://www.spigotmc.org/members/josegamer_pt.40267/"));
            System.out.print("");
            System.out.print(header);

            HandlerList.unregisterAll();

            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    private boolean setupNMS() {
        String versao;

        try {
            versao = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException whatVersionAreYouUsingException) {
            return false;
        }

        String compatible = "Your server is compatible with RealScoreboard!";
        switch (versao) {
            case "v1_13_R1":
                log(compatible);
                nms = new NMS1_13_R1();
                break;
            case "v1_13_R2":
                log(compatible);
                nms = new NMS1_13_R2();
                break;
            case "v1_14_R1":
                log(compatible);
                nms = new NMS1_14_R1();
                break;
            case "v1_14_R2":
                log(compatible);
                nms = new NMS1_14_R1();
                break;
            case "v1_15_R1":
                log(compatible);
                nms = new NMS1_15_R1();
                break;
            default:
                //not compatible
                break;
        }

        return nms != null;
    }

    protected void logList(List<String> l) {
        for (String s : l) {
            log(s);
        }
    }

    // Vault
    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        Economia = (Economy) rsp.getProvider();
        return Economia != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager()
                .getRegistration(Permission.class);
        if (permissionProvider != null) {
            perms = (Permission) permissionProvider.getProvider();
        }
        return perms != null;
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(Chat.class);
        if (chatProvider != null) {
            chat = (Chat) chatProvider.getProvider();
        }
        return chat != null;
    }
}