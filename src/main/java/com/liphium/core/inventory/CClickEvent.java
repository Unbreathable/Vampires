package com.liphium.core.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CClickEvent {

    private final ItemStack stack;
    private final Player player;

    public CClickEvent(Player player, ItemStack stack) {
        this.player = player;
        this.stack = stack;
    }

    public ItemStack getStack() {
        return stack;
    }

    public Player getPlayer() {
        return player;
    }
}
