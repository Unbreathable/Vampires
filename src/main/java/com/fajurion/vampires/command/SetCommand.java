package com.fajurion.vampires.command;

import com.fajurion.vampires.Vampires;
import com.fajurion.vampires.util.LocationAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {

        if(cs instanceof Player player && player.hasPermission("set")) {

            if(args.length == 0) {
                player.sendMessage("§c/set <name> §8-> §7Setzt eine Position.");
            } else {

                LocationAPI.setLocation(args[0], player.getLocation());
                player.sendMessage(Vampires.PREFIX + "§cPosition §7gesetzt.");
            }

        } else {
            cs.sendMessage("§cKeine Rechte!");
        }

        return false;
    }

}
