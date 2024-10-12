package com.liphium.vampires.listener;

import com.liphium.vampires.Vampires;
import com.liphium.vampires.game.state.LobbyState;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class GameListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Vampires.getInstance().getGameManager().getCurrentState().onInteract(event);
    }

    @EventHandler
    public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Vampires.getInstance().getGameManager().getCurrentState().onInteractAtEntity(event);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Vampires.getInstance().getGameManager().getCurrentState().onBreak(event);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Vampires.getInstance().getGameManager().getCurrentState().onPlace(event);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Vampires.getInstance().getGameManager().getCurrentState().onMove(event);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Vampires.getInstance().getGameManager().getCurrentState().onDamage(event);
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        Vampires.getInstance().getGameManager().getCurrentState().onDamageByEntity(event);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Vampires.getInstance().getGameManager().getCurrentState().onDeath(event);
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        Vampires.getInstance().getGameManager().getCurrentState().onDrop(event);
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntityType().equals(EntityType.ITEM)
                || event.getEntityType().equals(EntityType.FIREWORK_ROCKET)
                || event.getEntityType().equals(EntityType.ARMOR_STAND)
                || event.getEntityType().equals(EntityType.POTION)
                || event.getEntityType().equals(EntityType.AREA_EFFECT_CLOUD)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onRocket(FireworkExplodeEvent event) {
        Vampires.getInstance().getGameManager().getCurrentState().onFirework(event);
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent event) {
        if (Vampires.getInstance().getGameManager().getCurrentState() instanceof LobbyState) {
            return;
        }

        event.setCancelled(true);
    }

}
