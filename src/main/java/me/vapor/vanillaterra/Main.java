package me.vapor.vanillaterra;

import me.vapor.vanillaterra.commands.Distortion;
import me.vapor.vanillaterra.commands.Tpll;
import me.vapor.vanillaterra.commands.Where;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        this.getLogger().info("VanillaTerra Ready!");
        Objects.requireNonNull(getCommand("tpll")).setExecutor(new Tpll());
        Objects.requireNonNull(getCommand("where")).setExecutor(new Where());
        Objects.requireNonNull(getCommand("distortion")).setExecutor(new Distortion());
    }
}
