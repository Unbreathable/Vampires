package com.fajurion.vampires.util;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Messages {

    public static void broadcast(ChatMessageType type, String bar) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.spigot().sendMessage(type, new TextComponent(bar));
        }
    }

}
