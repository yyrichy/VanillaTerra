package com.github.vaporrrr.vanillaterra;

import com.github.vaporrrr.vanillaterra.commands.Distortion;
import com.github.vaporrrr.vanillaterra.commands.Tpll;
import com.github.vaporrrr.vanillaterra.commands.Where;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

@SuppressWarnings("unused")
public class VanillaTerra extends JavaPlugin {
    @Override
    public void onEnable() {
        this.getLogger().info("VanillaTerra Ready!");
        Objects.requireNonNull(getCommand("tpll")).setExecutor(new Tpll());
        Objects.requireNonNull(getCommand("where")).setExecutor(new Where());
        Objects.requireNonNull(getCommand("distortion")).setExecutor(new Distortion());
    }
}
