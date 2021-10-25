package me.vapor.vanillaterra.commands;

import net.buildtheearth.terraminusminus.generator.EarthGeneratorSettings;
import net.buildtheearth.terraminusminus.projection.GeographicProjection;
import net.buildtheearth.terraminusminus.projection.OutOfProjectionBoundsException;
import net.buildtheearth.terraminusminus.util.geo.CoordinateParseUtils;
import net.buildtheearth.terraminusminus.util.geo.LatLng;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Tpll implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!commandSender.hasPermission("vt.tpll") && !commandSender.isOp()) {
            return false;
        }
        if(!(commandSender instanceof Player)){
            commandSender.sendMessage("Only players can use this command");
            return true;
        }
        try {
            Player player = (Player) commandSender;
            Location l = player.getLocation();
            LatLng defaultCoords = CoordinateParseUtils.parseVerbatimCoordinates(this.getRawArguments(args).trim());

            if (defaultCoords == null) {
                LatLng possiblePlayerCoords = CoordinateParseUtils.parseVerbatimCoordinates(this.getRawArguments(this.selectArray(args, 1)));
                if (possiblePlayerCoords != null) {
                    defaultCoords = possiblePlayerCoords;
                }
            }

            LatLng possibleHeightCoords = CoordinateParseUtils.parseVerbatimCoordinates(this.getRawArguments(this.inverseSelectArray(args, args.length - 1)));
            if (possibleHeightCoords != null) {
                defaultCoords = possibleHeightCoords;
            }

            LatLng possibleHeightNameCoords = CoordinateParseUtils.parseVerbatimCoordinates(this.getRawArguments(this.inverseSelectArray(this.selectArray(args, 1), this.selectArray(args, 1).length - 1)));
            if (possibleHeightNameCoords != null) {
                defaultCoords = possibleHeightNameCoords;
            }

            if (defaultCoords == null) {
                player.sendMessage(ChatColor.DARK_RED + "Invalid coordinates. Ex: /tpll 38.897633, -77.0366201");
                return true;
            }

            EarthGeneratorSettings bteSettings = EarthGeneratorSettings.parse(EarthGeneratorSettings.BTE_DEFAULT_SETTINGS);
            GeographicProjection projection = bteSettings.projection();

            double[] c = {defaultCoords.getLng(), defaultCoords.getLat()};
            try {
                c = projection.fromGeo(c[0], c[1]);
            } catch (OutOfProjectionBoundsException e) {
                e.printStackTrace();
            }
            int y;
            if(args.length >= 3 && isInteger(args[2])){
                y = Integer.parseInt(args[2]);
            }
            else {
                int highest = player.getWorld().getHighestBlockAt((int) c[0], (int) c[1]).getY();
                if(highest < 1){
                    commandSender.sendMessage(ChatColor.DARK_RED + "Stay in the already generated areas of Hartford, Portland, Annapolis, and Middletown.");
                    return true;
                }
                y = highest + 1;
            }
            commandSender.sendMessage(ChatColor.GREEN + "Teleporting to " + defaultCoords.getLat() + " " + defaultCoords.getLng());
            player.teleportAsync(new Location(player.getWorld(), c[0], y, c[1], l.getYaw(), l.getPitch()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Gets a space seperated string from an array
     *
     * @param args A string array
     * @return The space seperated String
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
     * @param args  Initial array
     * @param index Starting index
     * @return Selected array
     */
    private String[] selectArray(String[] args, int index) {
        List<String> array = new ArrayList<>();
        for (int i = index; i < args.length; i++) {
            array.add(args[i]);
        }

        return array.toArray(array.toArray(new String[array.size()]));
    }

    private String[] inverseSelectArray(String[] args, int index) {
        List<String> array = new ArrayList<>();
        for (int i = 0; i < index; i++) {
            array.add(args[i]);
        }

        return array.toArray(array.toArray(new String[array.size()]));

    }

    public static boolean isInteger(String s) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),10) < 0) return false;
        }
        return true;
    }
}
