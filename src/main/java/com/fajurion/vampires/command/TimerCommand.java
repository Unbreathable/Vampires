package com.fajurion.vampires.command;

import com.fajurion.vampires.Vampires;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TimerCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {

        if(cs instanceof Player player && player.hasPermission("timer")) {

            if(args.length == 0) {

                sendHelp(player);
            } else if(args[0].equalsIgnoreCase("pause") || args[0].equalsIgnoreCase("resume")) {

                boolean paused = Vampires.getInstance().getGameManager().getCurrentState().paused;
                Vampires.getInstance().getGameManager().getCurrentState().paused = !paused;

                if(paused) {
                    player.sendMessage(Vampires.PREFIX + "§7Der §cTimer §7wurde §cfortgesetzt§7!");
                } else {
                    player.sendMessage(Vampires.PREFIX + "§7Der §cTimer §7wurde §cpausiert§7!");
                }
            } else if(args[0].equalsIgnoreCase("skip")) {

                if(Vampires.getInstance().getGameManager().getCurrentState().count <= 5) {
                    player.sendMessage(Vampires.PREFIX + "§7Der §cTimer §7wurde bereits §cverschnellert§7!");
                    return false;
                }

                player.sendMessage(Vampires.PREFIX + "§7Du hast den §cTimer §7verschnellert§7!");
                Vampires.getInstance().getGameManager().getCurrentState().count = 10;
            } else {
                sendHelp(player);
            }

        }

        return false;
    }

    public void sendHelp(Player player) {
        player.sendMessage(" ");
        player.sendMessage("    §cCounter§8: §c§l" + Vampires.getInstance().getGameManager().getCurrentState().count);

        String status = Vampires.getInstance().getGameManager().getCurrentState().paused
                ? "§c§lPausiert §8(§c/timer resume§8)"
                : "§c§lLaufend §8(§c/timer pause§8)";
        player.sendMessage("    §cStatus§8: " + status);
        player.sendMessage("§c/timer skip §8-> §7Setzt den Timer auf 5 Sekunden.");
        player.sendMessage(" ");
    }
}
