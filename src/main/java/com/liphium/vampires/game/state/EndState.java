package com.liphium.vampires.game.state;

import com.liphium.vampires.Vampires;
import com.liphium.vampires.game.GameState;
import com.liphium.vampires.util.LocationAPI;
import com.liphium.vampires.util.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
        super("Ending", 20);
    }

    @Override
    public void start() {

        for (Player player : Bukkit.getOnlinePlayers()) {
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

                if (tickCount++ >= 20) {
                    tickCount = 0;
                    if (!paused) count--;

                    Messages.actionBar(Component.text("Stoppen ", NamedTextColor.RED)
                            .append(Component.text("in ", NamedTextColor.GRAY))
                            .append(Component.text(count, NamedTextColor.RED, TextDecoration.BOLD))
                            .append(Component.text("..", NamedTextColor.GRAY))
                    );

                    if (count == 0) {
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
