package com.github.vaporrrr.vanillaterra.commands;

import net.buildtheearth.terraminusminus.generator.EarthGeneratorSettings;
import net.buildtheearth.terraminusminus.projection.GeographicProjection;
import net.buildtheearth.terraminusminus.projection.OutOfProjectionBoundsException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
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
            TextComponent textComponent = Component.text("You do not have permission to use that command.")
                    .color(NamedTextColor.DARK_RED);
            commandSender.sendMessage(textComponent);
            return true;
        }
        if (!(commandSender instanceof Player)) {
            TextComponent textComponent = Component.text("Only players can use this command.")
                    .color(NamedTextColor.DARK_RED);
            commandSender.sendMessage(textComponent);
            return true;
        }
        try{
            Location l = ((Player) commandSender).getLocation();
            EarthGeneratorSettings bteSettings = EarthGeneratorSettings.parse(EarthGeneratorSettings.BTE_DEFAULT_SETTINGS);
            GeographicProjection projection = bteSettings.projection();

            double[]c = new double[0];
            try {
                c = projection.toGeo(l.getX(), l.getZ());
                c = projection.tissot(c[0], c[1]);
            } catch (OutOfProjectionBoundsException e) {
                e.printStackTrace();
            }
            if (c == null || Double.isNaN(c[0])) {
                commandSender.sendMessage(ChatColor.DARK_RED + "You are not in the \"earth\". You must be in the projection/map. OutOfProjectionBoundsException");
                return true;
            }
            TextComponent textComponent = Component.text("Distortion: ")
                    .color(NamedTextColor.GRAY)
                    .append(Component.text("~", NamedTextColor.DARK_GREEN))
                    .append(Component.text(Math.sqrt(Math.abs(c[0])), NamedTextColor.RED).decoration(TextDecoration.BOLD, true))
                    .append(Component.text(" +/-", NamedTextColor.DARK_GREEN))
                    .append(Component.text(c[1] * 180.0 / Math.PI + "°", NamedTextColor.RED).decoration(TextDecoration.BOLD, true));
            commandSender.sendMessage(textComponent);
        }catch(Exception e){
            commandSender.sendMessage("A unknown error occurred. Please contact the server's developers.");
            e.printStackTrace();
        }
        return true;
    }
}
