package com.fajurion.vampires.listener.machines.impl;

import com.fajurion.vampires.listener.machines.Machine;
import de.nightempire.servercore.util.ItemStackBuilder;
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
        stand.setCustomName("§7Hier werden §cRaketen §7hergestellt.");
        stand.setGravity(false);
        stand.setInvisible(true);
        stand.setInvulnerable(true);
        stand.setRemoveWhenFarAway(false);
    }

    int count = 20, tickCount = 0;

    @Override
    public void tick() {
        if(broken) return;

        if(tickCount++ >= 20) {
            tickCount = 0;

            count--;
            stand.setCustomName("§cRakete §7in §c§l" + count + "§7..");

            if(count == 0) {
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
        stand.setCustomName("§c§oZerstört");
    }

    @Override
    public void destroy() {
        stand.remove();
    }
}
