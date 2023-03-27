package com.fajurion.vampires.game.state;

import com.fajurion.vampires.Vampires;
import com.fajurion.vampires.game.GameState;
import com.fajurion.vampires.util.LocationAPI;
import com.fajurion.vampires.util.Messages;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class EndState extends GameState {

    public EndState() {
        super("Ende", 20);
    }

    @Override
    public void start() {

        for(Player player : Bukkit.getOnlinePlayers()) {
            player.setGameMode(GameMode.SURVIVAL);
            player.teleport(LocationAPI.getLocation("Camp"));

            player.getInventory().clear();
            player.getInventory().setHelmet(null);
            player.getInventory().setChestplate(null);
            player.getInventory().setLeggings(null);
            player.getInventory().setBoots(null);
        }

        Vampires.getInstance().getTaskManager().inject(new Runnable() {
            int tickCount = 0;
            @Override
            public void run() {

                if(tickCount++ >= 20) {
                    tickCount = 0;
                    if(!paused) count--;

                    Messages.broadcast(ChatMessageType.ACTION_BAR, "§cStoppen §7in §c§l" + count + "§7..");

                    if(count == 0) {
                        Bukkit.shutdown();
                    }
                }

            }
        });
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onBreak(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onPlace(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }
}
