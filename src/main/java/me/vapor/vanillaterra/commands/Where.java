package me.vapor.vanillaterra.commands;

import net.buildtheearth.terraminusminus.generator.EarthGeneratorSettings;
import net.buildtheearth.terraminusminus.projection.GeographicProjection;
import net.buildtheearth.terraminusminus.projection.OutOfProjectionBoundsException;
import org.bukkit.ChatColor;
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

            double[] c = {l.getX(), l.getZ()};
            try {
                c = projection.toGeo(c[0], c[1]);
            } catch (OutOfProjectionBoundsException e) {
                e.printStackTrace();
                commandSender.sendMessage(ChatColor.DARK_RED + "You are not in the \"earth\". You must be in the projection/map. OutOfProjectionBoundsException");
                return true;
            }
            commandSender.sendMessage(ChatColor.GREEN + "You are at " + c[1] + " " + c[0] + "\nGoogle Maps Link: " + ChatColor.YELLOW + "\nhttps://www.google.com/maps/search/?api=1&query=" + c[1] + "," + c[0]);
        }catch(Exception e){
            commandSender.sendMessage("A unknown error occurred. Please contact the server's developers.");
            e.printStackTrace();
        }
        return true;
    }
}
