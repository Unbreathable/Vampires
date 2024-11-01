package com.liphium.vampires.listener.machines.impl;

import com.liphium.vampires.Vampires;
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

public class BeetrootDropper extends Machine {

    private final ArmorStand stand;

    public BeetrootDropper(Location location) {
        super(location, false);

        stand = location.getWorld().spawn(location.clone().add(0, -1.5, 0), ArmorStand.class);

        stand.setCustomNameVisible(true);
        stand.customName(Component.text("§cBlood garlic §7is produced here."));
        stand.setGravity(false);
        stand.setInvisible(true);
        stand.setInvulnerable(true);
        stand.setRemoveWhenFarAway(false);
    }

    int count = 30, tickCount = 0;

    @Override
    public void tick() {
        if (broken) return;

        if (tickCount++ >= 20) {
            tickCount = 0;

            count--;
            stand.customName(Component.text("Blood garlic", NamedTextColor.RED).appendSpace()
                    .append(Component.text("in", NamedTextColor.GRAY)).appendSpace()
                    .append(Component.text(count, NamedTextColor.RED, TextDecoration.BOLD))
                    .append(Component.text("..", NamedTextColor.GRAY)));

            if (count == 0) {
                count = 30;

                Item item = location.getWorld().dropItem(location, new ItemStack(Material.BEETROOT));
                item.setVelocity(new Vector());
            }
        }
    }

    @Override
    public void onBreak() {
        stand.customName(Component.text("§c§oDestroyed"));
    }

    @Override
    public void destroy() {
        stand.remove();
    }
}
