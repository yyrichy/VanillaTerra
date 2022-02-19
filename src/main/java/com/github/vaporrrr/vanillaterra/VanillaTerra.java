package com.github.vaporrrr.vanillaterra;

import com.github.vaporrrr.vanillaterra.commands.Distortion;
import com.github.vaporrrr.vanillaterra.commands.Reload;
import com.github.vaporrrr.vanillaterra.commands.Tpll;
import com.github.vaporrrr.vanillaterra.commands.Where;
import org.bukkit.plugin.java.JavaPlugin;

public class VanillaTerra extends JavaPlugin {
    @Override
    @SuppressWarnings("ConstantConditions")
    public void onEnable() {
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
        } catch (ClassNotFoundException e) {
            getLogger().info("This plugin only supports paper and its downstream forks. Disabling...");
            getPluginLoader().disablePlugin(this);
            return;
        }
        saveDefaultConfig();
        getLogger().info("VanillaTerra enabled!");
        getCommand("tpll").setExecutor(new Tpll());
        getCommand("where").setExecutor(new Where());
        getCommand("distortion").setExecutor(new Distortion());
        getCommand("vt-reload").setExecutor(new Reload());
        getConfig().options().copyDefaults(true);
        saveConfig();
    }
}
