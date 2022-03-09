package com.github.vaporrrr.vanillaterra.commands;

import com.github.vaporrrr.vanillaterra.VanillaTerra;
import net.buildtheearth.terraminusminus.generator.EarthGeneratorSettings;
import net.buildtheearth.terraminusminus.projection.GeographicProjection;
import net.buildtheearth.terraminusminus.projection.OutOfProjectionBoundsException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Distortion implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!commandSender.hasPermission("vt.terra.distortion") && !commandSender.isOp()) {
            VanillaTerra.sendComponent(commandSender, Component.text("You do not have permission to use that command.")
                    .color(NamedTextColor.DARK_RED));
            return true;
        }
        if (!(commandSender instanceof Player)) {
            VanillaTerra.sendComponent(commandSender, Component.text("Only players can use this command.")
                    .color(NamedTextColor.DARK_RED));
            return true;
        }
        Location l = ((Player) commandSender).getLocation();
        EarthGeneratorSettings bteSettings = EarthGeneratorSettings.parse(EarthGeneratorSettings.BTE_DEFAULT_SETTINGS);
        GeographicProjection projection = bteSettings.projection();

        double[] c;
        try {
            c = projection.toGeo(l.getX(), l.getZ());
            c = projection.tissot(c[0], c[1]);
        } catch (OutOfProjectionBoundsException e) {
            VanillaTerra.sendComponent(commandSender, Component.text("You are not in the \"earth\". You must be in the projection/map.")
                    .color(NamedTextColor.DARK_RED));
            return true;
        }

        TextComponent textComponent = Component.text("Distortion: ")
                .color(NamedTextColor.GRAY)
                .append(Component.text("~", NamedTextColor.DARK_GREEN))
                .append(Component.text(Math.sqrt(Math.abs(c[0])), NamedTextColor.RED).decoration(TextDecoration.BOLD, true))
                .append(Component.text(" +/-", NamedTextColor.DARK_GREEN))
                .append(Component.text(c[1] * 180.0 / Math.PI + "°", NamedTextColor.RED).decoration(TextDecoration.BOLD, true));
        VanillaTerra.sendComponent(commandSender, textComponent);
        return true;
    }
}
