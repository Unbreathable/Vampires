package com.fajurion.vampires.listener.machines.impl;

import com.fajurion.vampires.listener.machines.Machine;
import de.nightempire.servercore.Core;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Brewer extends Machine {

    private final ArmorStand stand;

    public Brewer(Location location) {
        super(location, false);

        stand = location.getWorld().spawn(location.clone().add(0, -1, 0), ArmorStand.class);

        stand.setCustomNameVisible(true);
        stand.setCustomName("§7Klicke auf den §cBraustand§7.");
        stand.setGravity(false);
        stand.setInvisible(true);
        stand.setVisible(false);
        stand.setInvulnerable(true);
        stand.setRemoveWhenFarAway(false);

        location.getBlock().setType(Material.BREWING_STAND);
    }

    int tickCount = 0, count = 60;
    public ItemStack currentPotion;

    @Override
    public void tick() {

        if(currentPotion != null) {
            if(tickCount++ >= 20) {
                count--;
                tickCount = 0;

                stand.setCustomName("§c" + currentPotion.getItemMeta().getDisplayName() + " §7in §c§l" + count + "§7..");

                if(count == 0) {
                    stand.setCustomName("§7Klicke auf den §cBraustand§7.");
                    count = 60;
                    location.getWorld().dropItem(location.clone().add(0, 1.5, 0), currentPotion);
                    currentPotion = null;
                }
            }
        }

    }

    @Override
    public void destroy() {
        stand.remove();
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        if(event.getClickedBlock() != null && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if(!event.getClickedBlock().equals(location.getBlock())) return;

            Core.getInstance().getScreens().open(event.getPlayer(), 2);
            event.setCancelled(true);
        }
    }

    @Override
    public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if(event.getRightClicked().equals(stand)) {
            Core.getInstance().getScreens().open(event.getPlayer(), 2);
            event.setCancelled(true);
        }
    }
}
