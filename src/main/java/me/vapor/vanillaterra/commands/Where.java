package me.vapor.vanillaterra.commands;

import net.buildtheearth.terraminusminus.generator.EarthGeneratorSettings;
import net.buildtheearth.terraminusminus.projection.GeographicProjection;
import net.buildtheearth.terraminusminus.projection.OutOfProjectionBoundsException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Where implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!commandSender.hasPermission("vt.terra.where") && !commandSender.isOp()) {
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

            double[] c = {l.getX(), l.getZ()};
            try {
                c = projection.toGeo(c[0], c[1]);
            } catch (OutOfProjectionBoundsException e) {
                TextComponent textComponent = Component.text("You outside of the \"earth\". You must be in the projection/map.")
                        .color(NamedTextColor.DARK_RED);
                commandSender.sendMessage(textComponent);
                return true;
            }
            TextComponent textComponent = Component.text("You are at ")
                    .color(NamedTextColor.GRAY)
                    .append(((Component.text(c[1], NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true))
                    .append(Component.text(", ", NamedTextColor.GRAY).decoration(TextDecoration.BOLD, true))
                    .append(Component.text(c[0], NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true))).clickEvent(ClickEvent.copyToClipboard(c[1] + " " + c[0]))
                    .append(Component.text("\n")))
                    .append(Component.text("[Google Maps Link]", NamedTextColor.YELLOW).clickEvent(ClickEvent.openUrl("https://www.google.com/maps/search/?api=1&query=" + c[1] + "," + c[0])).decoration(TextDecoration.BOLD, true));
            commandSender.sendMessage(textComponent);
        }catch(Exception e){
            commandSender.sendMessage("A unknown error occurred. Please contact the server's developers.");
            e.printStackTrace();
        }
        return true;
    }
}
