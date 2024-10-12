package com.liphium.vampires.listener.machines.impl;

import com.liphium.core.util.ItemStackBuilder;
import com.liphium.vampires.listener.machines.Machine;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

public class RocketDropper extends Machine {

    private final ArmorStand stand;

    public RocketDropper(Location location) {
        super(location, false);

        stand = location.getWorld().spawn(location.clone().add(0, -1.5, 0), ArmorStand.class);

        stand.setCustomNameVisible(true);
        stand.customName(Component.text("Hier werden ", NamedTextColor.GRAY)
                .append(Component.text("Raketen ", NamedTextColor.RED))
                .append(Component.text("hergestellt.", NamedTextColor.GRAY)));
        stand.setGravity(false);
        stand.setInvisible(true);
        stand.setInvulnerable(true);
        stand.setRemoveWhenFarAway(false);
    }

    int count = 20, tickCount = 0;

    @Override
    public void tick() {
        if (broken) return;

        if (tickCount++ >= 20) {
            tickCount = 0;

            count--;
            stand.customName(Component.text("Rakete ", NamedTextColor.RED)
                    .append(Component.text("in ", NamedTextColor.GRAY))
                    .append(Component.text(count, NamedTextColor.RED, TextDecoration.BOLD))
                    .append(Component.text("..", NamedTextColor.GRAY))
            );

            if (count == 0) {
                count = 20;

                ItemStack rocket = new ItemStackBuilder(Material.FIREWORK_ROCKET).buildStack();
                FireworkMeta meta = (FireworkMeta) rocket.getItemMeta();
                meta.setPower(3);
                meta.addEffect(FireworkEffect.builder().withColor(Color.RED).build());
                rocket.setItemMeta(meta);

                location.getWorld().dropItem(location, rocket);
            }
        }
    }

    @Override
    public void onBreak() {
        stand.customName(Component.text("Zerstört", NamedTextColor.RED, TextDecoration.ITALIC));
    }

    @Override
    public void destroy() {
        stand.remove();
    }
}
