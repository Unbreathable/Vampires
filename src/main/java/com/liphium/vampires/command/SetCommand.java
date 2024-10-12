package com.liphium.vampires.command;

import com.liphium.core.Core;
import com.liphium.vampires.Vampires;
import com.liphium.vampires.util.LocationAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {

        if (cs instanceof Player player && player.hasPermission("set")) {

            if (args.length == 0) {
                player.sendMessage(Component.text("§c/set <name> §8-> §7Setzt eine Position."));
            } else {

                if (args[0].equals("item")) {
                    Core.getInstance().getScreens().open(player, 3);
                    return true;
                }

                LocationAPI.setLocation(args[0], player.getLocation());
                player.sendMessage(Vampires.PREFIX.append(Component.text("§cPosition §7gesetzt.")));
            }

        } else {
            cs.sendMessage(Component.text("§cKeine Rechte!"));
        }

        return false;
    }

}
