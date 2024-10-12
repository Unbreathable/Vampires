package com.liphium.vampires.util;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Messages {

    public static void actionBar(Component bar) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendActionBar(bar);
        }
    }

}
