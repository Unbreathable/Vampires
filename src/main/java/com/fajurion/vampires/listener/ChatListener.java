package com.fajurion.vampires.listener;

import com.fajurion.vampires.Vampires;
import com.fajurion.vampires.game.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Team team = Vampires.getInstance().getGameManager().getTeamManager().getTeam(event.getPlayer());
        event.setCancelled(true);
        if(team == null) {
            Bukkit.broadcastMessage("§7§o" + event.getPlayer().getName() + "§8: §7" + event.getMessage());
        } else Bukkit.broadcastMessage(team.getCc() + event.getPlayer().getName() + "§8: §7" + event.getMessage());
    }

}
