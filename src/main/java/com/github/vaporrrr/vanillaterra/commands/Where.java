package com.github.vaporrrr.vanillaterra.commands;

import com.github.vaporrrr.vanillaterra.VanillaTerra;
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

import java.util.List;

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
            double lat = c[1];
            double lng = c[0];
            TextComponent textComponent = Component.text("You are at ")
                    .color(NamedTextColor.GRAY)
                    .append(((Component.text(lat, NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true))
                    .append(Component.text(", ", NamedTextColor.GRAY).decoration(TextDecoration.BOLD, true))
                    .append(Component.text(lng, NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true))).clickEvent(ClickEvent.copyToClipboard(lat + " " + lng))
                    .append(Component.text("\n")));
            List<String> mapLinks = VanillaTerra.getPlugin(VanillaTerra.class).getConfig().getStringList("MapLinks");
            if (mapLinks.contains("Google")) {
                textComponent = textComponent.append(Component.text("[Google Maps Link] ", NamedTextColor.YELLOW).clickEvent(ClickEvent.openUrl("https://www.google.com/maps/search/?api=1&query=" + lat + "," + lng)).decoration(TextDecoration.BOLD, true));
            }
            if (mapLinks.contains("Yandex")) {
                 textComponent = textComponent.append(Component.text("[Yandex Maps Link] ", NamedTextColor.YELLOW).clickEvent(ClickEvent.openUrl("https://yandex.com/maps/?ll=" + lng + "%2C" + lat + "z=10")).decoration(TextDecoration.BOLD, true));
            }
            if (mapLinks.contains("OSM")) {
                textComponent = textComponent.append(Component.text("[OSM Link] ", NamedTextColor.YELLOW).clickEvent(ClickEvent.openUrl("https://www.openstreetmap.org/#map=19/" + lat + "/" + lng)).decoration(TextDecoration.BOLD, true));
            }
            commandSender.sendMessage(textComponent);
        }catch(Exception e){
            commandSender.sendMessage("A unknown error occurred. Please contact the server's developers.");
            e.printStackTrace();
        }
        return true;
    }
}
