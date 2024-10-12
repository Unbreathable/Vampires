package com.liphium.vampires.listener.machines.impl;

import com.liphium.core.util.ItemStackBuilder;
import com.liphium.vampires.Vampires;
import com.liphium.vampires.listener.machines.Machine;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;

public class BeetrootDropper extends Machine {

    private final ArmorStand stand;

    public BeetrootDropper(Location location) {
        super(location, false);

        stand = location.getWorld().spawn(location.clone().add(0, -1.5, 0), ArmorStand.class);

        stand.setCustomNameVisible(true);
        stand.customName(Component.text("§7Hier wird §cBlut Knoblauch §7hergestellt."));
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
            stand.customName(Vampires.PREFIX.append(Component.text("§cBlut Knoblauch", NamedTextColor.RED))
                    .append(Component.text("in ", NamedTextColor.GRAY))
                    .append(Component.text("" + count, NamedTextColor.RED, TextDecoration.BOLD))
                    .append(Component.text("..", NamedTextColor.GRAY)));

            if (count == 0) {
                count = 30;

                location.getWorld().dropItem(location, new ItemStackBuilder(Material.BEETROOT).buildStack());
            }
        }
    }

    @Override
    public void onBreak() {
        stand.customName(Component.text("§c§oZerstört"));
    }

    @Override
    public void destroy() {
        stand.remove();
    }
}
