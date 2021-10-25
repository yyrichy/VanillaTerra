package me.vapor.vanillaterra.commands;

import net.buildtheearth.terraminusminus.generator.EarthGeneratorSettings;
import net.buildtheearth.terraminusminus.projection.GeographicProjection;
import net.buildtheearth.terraminusminus.projection.OutOfProjectionBoundsException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
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
            return false;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can use this command");
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
            final TextComponent textComponent = Component.text("Distortion: ")
                    .color(TextColor.color(0x8c8c8c))
                    .append(Component.text("~", NamedTextColor.DARK_GREEN)
                    .append(Component.text(Math.sqrt(Math.abs(c[0])), NamedTextColor.RED)).decoration(TextDecoration.BOLD, true)
                    .append(Component.text(" +/-", NamedTextColor.DARK_GREEN))
                    .append(Component.text(c[1] * 180.0 / Math.PI + "°", NamedTextColor.RED)).decoration(TextDecoration.BOLD, true));
            commandSender.sendMessage(textComponent);
        }catch(Exception e){
            commandSender.sendMessage("A unknown error occurred. Please contact the server's developers.");
            e.printStackTrace();
        }
        return true;
    }
}
