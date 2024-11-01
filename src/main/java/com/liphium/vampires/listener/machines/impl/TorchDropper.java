package com.liphium.vampires.listener.machines.impl;

import com.liphium.core.util.ItemStackBuilder;
import com.liphium.vampires.listener.machines.Machine;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class TorchDropper extends Machine {

    private final ArmorStand stand;

    public TorchDropper(Location location) {
        super(location, true);

        stand = location.getWorld().spawn(location.clone().add(0.5, -0.5, 0.5), ArmorStand.class);

        stand.setCustomNameVisible(true);
        stand.customName(Component.text("Torches", NamedTextColor.GOLD).appendSpace()
                .append(Component.text("are produced here.", NamedTextColor.GRAY)));
        stand.setGravity(false);
        stand.setInvisible(true);
        stand.setInvulnerable(true);
        stand.setRemoveWhenFarAway(false);
    }

    int count = 3, tickCount = 0;

    @Override
    public void tick() {
        if (broken) return;

        if (tickCount++ >= 20) {
            tickCount = 0;

            count--;
            stand.customName(Component.text("Torch", NamedTextColor.GOLD).appendSpace()
                    .append(Component.text("in", NamedTextColor.GRAY)).appendSpace()
                    .append(Component.text(count, NamedTextColor.GOLD, TextDecoration.BOLD)).appendSpace()
                    .append(Component.text("..", NamedTextColor.GRAY)));
            if (count == 0) {
                count = 11;
                Item item = location.getWorld().dropItem(location.clone().add(0.5, 1.5, 0.5), new ItemStack(Material.TORCH));
                item.setVelocity(new Vector());
            }
        }
    }

    @Override
    public void onBreak() {
        stand.customName(Component.text("Destroyed", NamedTextColor.RED, TextDecoration.ITALIC));
    }

    @Override
    public void destroy() {
        stand.remove();
    }
}
