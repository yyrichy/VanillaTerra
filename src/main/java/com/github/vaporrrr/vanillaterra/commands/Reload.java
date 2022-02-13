package com.github.vaporrrr.vanillaterra.commands;

import com.github.vaporrrr.vanillaterra.VanillaTerra;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Reload implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!commandSender.hasPermission("vt.reload") && !commandSender.isOp()) {
            TextComponent textComponent = Component.text("You do not have permission to use that command.")
                    .color(NamedTextColor.DARK_RED);
            commandSender.sendMessage(textComponent);
            return true;
        }
        VanillaTerra.getPlugin(VanillaTerra.class).reloadConfig();
        TextComponent textComponent = Component.text("Plugin reloaded.")
                .color(NamedTextColor.GREEN);
        commandSender.sendMessage(textComponent);
        return true;
    }
}
