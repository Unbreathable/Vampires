package com.liphium.vampires.game;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public abstract class GameState {

    private final String name;
    public int count;
    public boolean paused = false;

    public GameState(String name, int count) {
        this.name = name;
        this.count = count;
    }

    public abstract void start();

    public String getName() {
        return name;
    }

    public void onInteract(PlayerInteractEvent event) {
    }

    public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
    }

    public void onMove(PlayerMoveEvent event) {
    }

    public void onDeath(PlayerDeathEvent event) {
    }

    public void onDamage(EntityDamageEvent event) {
    }

    public void onDamageByEntity(EntityDamageByEntityEvent event) {
    }

    public void onPlace(BlockPlaceEvent event) {
    }

    public void onDrop(PlayerDropItemEvent event) {
    }

    public void onBreak(BlockBreakEvent event) {
    }

    public void onFirework(FireworkExplodeEvent event) {
    }

    public void join(Player player) {
    }

    public void quit(Player player) {
    }

    public void handleDeath(Player player) {
    }

}
