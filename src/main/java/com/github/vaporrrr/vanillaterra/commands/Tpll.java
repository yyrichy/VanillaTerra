// Adapted from BuildTheEarth/terraplusplus: https://github.com/BuildTheEarth/terraplusplus
package com.github.vaporrrr.vanillaterra.commands;

import com.github.vaporrrr.vanillaterra.VanillaTerra;
import io.papermc.lib.PaperLib;
import net.buildtheearth.terraminusminus.generator.EarthGeneratorSettings;
import net.buildtheearth.terraminusminus.projection.GeographicProjection;
import net.buildtheearth.terraminusminus.projection.OutOfProjectionBoundsException;
import net.buildtheearth.terraminusminus.util.geo.CoordinateParseUtils;
import net.buildtheearth.terraminusminus.util.geo.LatLng;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tpll implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!commandSender.hasPermission("vt.tpll") && !commandSender.isOp()) {
            VanillaTerra.sendComponent(commandSender, Component.text("You do not have permission to use that command.")
                    .color(NamedTextColor.DARK_RED));
            return true;
        }
        if (!(commandSender instanceof Player)) {
            VanillaTerra.sendComponent(commandSender, Component.text("Only players can use this command.")
                    .color(NamedTextColor.DARK_RED));
            return true;
        }
        try {
            Player player = (Player) commandSender;
            Location l = player.getLocation();
            double altitude = Double.NaN;
            LatLng defaultCoords = CoordinateParseUtils.parseVerbatimCoordinates(this.getRawArguments(args).trim());

            if (defaultCoords == null) {
                LatLng possiblePlayerCoords = CoordinateParseUtils.parseVerbatimCoordinates(this.getRawArguments(this.selectArray(args)));
                if (possiblePlayerCoords != null) {
                    defaultCoords = possiblePlayerCoords;
                }
            }

            LatLng possibleHeightCoords = CoordinateParseUtils.parseVerbatimCoordinates(this.getRawArguments(this.inverseSelectArray(args, args.length - 1)));
            if (possibleHeightCoords != null) {
                defaultCoords = possibleHeightCoords;
                try {
                    altitude = Double.parseDouble(args[args.length - 1]);
                } catch (Exception ignored) {
                }
            }

            LatLng possibleHeightNameCoords = CoordinateParseUtils.parseVerbatimCoordinates(this.getRawArguments(this.inverseSelectArray(this.selectArray(args), this.selectArray(args).length - 1)));
            if (possibleHeightNameCoords != null) {
                defaultCoords = possibleHeightNameCoords;
                try {
                    altitude = Double.parseDouble(args[args.length - 1]);
                } catch (Exception e) {
                    altitude = Double.NaN;
                }
            }

            if (defaultCoords == null) {
                VanillaTerra.sendComponent(commandSender, Component.text("Invalid coordinates. </tpll latitude longitude>")
                        .color(NamedTextColor.RED));
                return true;
            }

            EarthGeneratorSettings bteSettings = EarthGeneratorSettings.parse(EarthGeneratorSettings.BTE_DEFAULT_SETTINGS);
            GeographicProjection projection = bteSettings.projection();

            double[] c = {defaultCoords.getLng(), defaultCoords.getLat()};
            try {
                c = projection.fromGeo(c[0], c[1]);
            } catch (OutOfProjectionBoundsException e) {
                VanillaTerra.sendComponent(commandSender, Component.text("You are not in the \"earth\". You must be in the projection/map.")
                        .color(NamedTextColor.RED));
                return true;
            }
            if (Double.isNaN(altitude)) {
                int highest = player.getWorld().getHighestBlockAt((int) Math.floor(c[0]), (int) Math.floor(c[1])).getY();
                if (highest <= player.getWorld().getMinHeight()) {
                    VanillaTerra.sendComponent(commandSender, Component.text("Please stay in generated areas. You are teleporting to restricted areas.")
                            .color(NamedTextColor.RED));
                    return true;
                }
                altitude = highest;
            }
            TextComponent textComponent = Component.text("Teleporting to ")
                    .color(NamedTextColor.GRAY)
                    .append(Component.text(defaultCoords.getLat(), NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true))
                    .append(Component.text(", ", NamedTextColor.GRAY).decoration(TextDecoration.BOLD, true))
                    .append(Component.text(defaultCoords.getLng(), NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true));
            VanillaTerra.sendComponent(commandSender, textComponent);
            PaperLib.teleportAsync(player, new Location(player.getWorld(), c[0], altitude, c[1], l.getYaw(), l.getPitch()));
        } catch (Exception e) {
            VanillaTerra.sendComponent(commandSender, Component.text("An unknown error occurred executing this command.")
                    .color(NamedTextColor.DARK_RED));
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Gets a space separated string from an array
     *
     * @param args A string array
     * @return The space separated String
     */
    private String getRawArguments(String[] args) {
        if (args.length == 0) {
            return "";
        }
        if (args.length == 1) {
            return args[0];
        }

        StringBuilder arguments = new StringBuilder(args[0].replace((char) 176, (char) 32).trim());

        for (int x = 1; x < args.length; x++) {
            arguments.append(" ").append(args[x].replace((char) 176, (char) 32).trim());
        }

        return arguments.toString();
    }

    /**
     * Gets all objects in a string array above a given index
     *
     * @param args Initial array
     * @return Selected array
     */
    private String[] selectArray(String[] args) {
        List<String> array = new ArrayList<>(Arrays.asList(args).subList(1, args.length));
        return array.toArray(array.toArray(new String[0]));
    }

    private String[] inverseSelectArray(String[] args, int index) {
        List<String> array = new ArrayList<>(Arrays.asList(args).subList(0, index));
        return array.toArray(array.toArray(new String[0]));
    }
}
