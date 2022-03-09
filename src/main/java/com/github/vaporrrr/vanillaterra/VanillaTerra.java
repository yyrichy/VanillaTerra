package com.github.vaporrrr.vanillaterra;

import com.github.vaporrrr.vanillaterra.commands.Distortion;
import com.github.vaporrrr.vanillaterra.commands.Reload;
import com.github.vaporrrr.vanillaterra.commands.Tpll;
import com.github.vaporrrr.vanillaterra.commands.Where;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class VanillaTerra extends JavaPlugin {
    private final FileConfiguration config;
    private final BukkitAudiences adventure;

    public VanillaTerra() {
        this.config = getConfig();
        this.adventure = BukkitAudiences.create(this);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("VanillaTerra enabled!");
        getCommand("tpll").setExecutor(new Tpll());
        getCommand("where").setExecutor(new Where());
        getCommand("distortion").setExecutor(new Distortion());
        getCommand("vt-reload").setExecutor(new Reload());
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public static void sendComponent(CommandSender commandSender, TextComponent textComponent) {
        getPlugin().adventure.sender(commandSender).sendMessage(textComponent);
    }

    public static VanillaTerra getPlugin() {
        return getPlugin(VanillaTerra.class);
    }

    public static FileConfiguration config() {
        return getPlugin().config;
    }
}
