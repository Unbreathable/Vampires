package com.liphium.vampires.listener.machines.impl;

import com.liphium.core.util.ItemStackBuilder;
import com.liphium.vampires.listener.machines.Machine;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;

public class TorchDropper extends Machine {

    private final ArmorStand stand;

    public TorchDropper(Location location) {
        super(location, false);

        stand = location.getWorld().spawn(location.clone().add(0, -1.5, 0), ArmorStand.class);

        stand.setCustomNameVisible(true);
        stand.customName(Component.text("§7Hier werden §cFackeln §7hergestellt."));
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
            stand.customName(Component.text("§cFackel §7in §c§l" + count + "§7.."));

            if (count == 0) {
                count = 11;
                location.getWorld().dropItem(location, new ItemStackBuilder(Material.TORCH).buildStack());
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
