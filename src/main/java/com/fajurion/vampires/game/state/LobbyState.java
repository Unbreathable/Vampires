package com.fajurion.vampires.game.state;

import com.fajurion.vampires.Vampires;
import com.fajurion.vampires.game.GameState;
import com.fajurion.vampires.util.LocationAPI;
import com.fajurion.vampires.util.Messages;
import de.nightempire.servercore.Core;
import de.nightempire.servercore.util.ItemStackBuilder;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class LobbyState extends GameState {

    // Constants
    private final int NEEDED_PlAYERS = 1;

    public LobbyState() {
        super("Wartelobby", 200);
    }

    @Override
    public void start() {

        Location location = LocationAPI.getLocation("Camp");
        if(location != null && location.getWorld() != null) {
            location.getWorld().setTime(18000);
            location.getWorld().setThundering(false);
            location.getWorld().setStorm(false);
            location.getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            location.getWorld().setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            location.getWorld().setDifficulty(Difficulty.PEACEFUL);

            // World cleanup
            for(Entity entity : location.getWorld().getEntities()) {
                if(!(entity instanceof Player)) entity.remove();
            }

        } else {
            Bukkit.broadcastMessage("§cBitte setze den Server zuerst auf.");
            return;
        }

        for(Player player : Bukkit.getOnlinePlayers()) {
            player.setHealth(20);
            player.setFoodLevel(20);
            player.getActivePotionEffects().clear();
        }

        Vampires.getInstance().getTaskManager().inject(new Runnable() {
            int tickCount = 0;
            @Override
            public void run() {

                if(tickCount++ >= 20) {
                    tickCount = 0;

                    // World cleanup
                    for(Entity entity : location.getWorld().getEntities()) {
                        if(entity.getType().equals(EntityType.DROPPED_ITEM)) entity.remove();
                    }

                    if(Bukkit.getOnlinePlayers().size() >= NEEDED_PlAYERS) {
                        if(!paused) count--;

                        if(count <= 5) {

                            if(count == 0) {

                                for(Player player : Bukkit.getOnlinePlayers()) {
                                    player.sendTitle("§c§lVampires", "§7Halloween Special", 0, 50, 20);
                                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                                }

                                Vampires.getInstance().getTaskManager().uninject(this);
                                Vampires.getInstance().getGameManager().setCurrentState(new IngameState());
                                return;
                            }

                            for(Player player : Bukkit.getOnlinePlayers()) {
                                player.sendTitle("§c§l" + count + "§7..", "§7Vampires", 0, 50, 20);
                                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1f);
                            }

                        } else if(count % 10 == 0 && count <= 100) {
                            Bukkit.broadcastMessage(Vampires.PREFIX + "§7Das §cSpiel §7startet in §c" + count + " Sekunden§7.");
                        }

                        if(paused) Messages.broadcast(ChatMessageType.ACTION_BAR, "§7Countdown §cpausiert");
                        else Messages.broadcast(ChatMessageType.ACTION_BAR, "§c§l" + count + "§7..");
                    } else {

                        Messages.broadcast(ChatMessageType.ACTION_BAR, "§7Warten auf §cSpieler§7.. (§c" + Bukkit.getOnlinePlayers().size() + "§7/§c" + NEEDED_PlAYERS + "§7)");
                        count = 199;
                    }

                }

            }
        });

    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        if(event.getItem() != null && event.getItem().getType().equals(Material.SADDLE)) {
            Core.getInstance().getScreens().open(event.getPlayer(), 1);
        }
    }

    @Override
    public void join(Player player) {
        player.setGameMode(GameMode.SURVIVAL);

        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);

        player.getInventory().setItem(4, new ItemStackBuilder(Material.SADDLE).withName("§c§lTeams §7(Rechtsklick)")
                .withLore("§7§oBetrete ein Team.").buildStack());

        player.teleport(LocationAPI.getLocation("Camp"));
    }

    @Override
    public void onDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onBreak(BlockBreakEvent event) {
        if(event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;
        event.setCancelled(true);
    }

    @Override
    public void onDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onPlace(BlockPlaceEvent event) {
        if(event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;
        event.setCancelled(true);
    }
}
