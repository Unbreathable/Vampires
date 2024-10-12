package com.liphium.vampires.game.state;

import com.liphium.core.Core;
import com.liphium.core.util.ItemStackBuilder;
import com.liphium.vampires.Vampires;
import com.liphium.vampires.game.GameState;
import com.liphium.vampires.util.LocationAPI;
import com.liphium.vampires.util.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
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

import java.time.Duration;

public class LobbyState extends GameState {

    // Constants
    private final int NEEDED_PlAYERS = 1;

    public LobbyState() {
        super("Waiting for players", 200);
    }

    @Override
    public void start() {

        Location location = LocationAPI.getLocation("Camp");
        if (location != null && location.getWorld() != null) {
            location.getWorld().setTime(18000);
            location.getWorld().setThundering(false);
            location.getWorld().setStorm(false);
            location.getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            location.getWorld().setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            location.getWorld().setDifficulty(Difficulty.PEACEFUL);

            // World cleanup
            for (Entity entity : location.getWorld().getEntities()) {
                if (!(entity instanceof Player)) entity.remove();
            }

        } else {
            Bukkit.broadcast(Component.text("Please set up the server first.", NamedTextColor.RED));
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setHealth(20);
            player.setFoodLevel(20);
            player.getActivePotionEffects().clear();
        }

        Vampires.getInstance().getTaskManager().inject(new Runnable() {
            int tickCount = 0;

            @Override
            public void run() {

                if (tickCount++ >= 20) {
                    tickCount = 0;

                    // World cleanup
                    for (Entity entity : location.getWorld().getEntities()) {
                        if (entity.getType().equals(EntityType.ITEM)) entity.remove();
                    }

                    if (!Bukkit.getOnlinePlayers().isEmpty()) {
                        if (!paused) count--;

                        if (count <= 5) {

                            if (count == 0) {

                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    player.showTitle(Title.title(
                                            Component.text("Vampires", NamedTextColor.RED, TextDecoration.BOLD),
                                            Component.text("Halloween Special", NamedTextColor.GRAY),
                                            Title.Times.times(Duration.ofSeconds(0), Duration.ofSeconds(3), Duration.ofSeconds(1))
                                    ));
                                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                                }

                                Vampires.getInstance().getTaskManager().uninject(this);
                                Vampires.getInstance().getGameManager().setCurrentState(new IngameState());
                                return;
                            }

                            for (Player player : Bukkit.getOnlinePlayers()) {
                                player.showTitle(Title.title(
                                        Component.text(count, NamedTextColor.RED, TextDecoration.BOLD)
                                                .append(Component.text("..", NamedTextColor.GRAY)),
                                        Component.text("Vampires", NamedTextColor.GRAY),
                                        Title.Times.times(Duration.ofSeconds(0), Duration.ofSeconds(3), Duration.ofSeconds(1))
                                ));
                                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1f);
                            }

                        } else if (count % 10 == 0 && count <= 100) {
                            Bukkit.broadcast(Vampires.PREFIX
                                    .append(Component.text("Das ", NamedTextColor.GRAY))
                                    .append(Component.text("Spiel ", NamedTextColor.RED))
                                    .append(Component.text("startet in ", NamedTextColor.GRAY))
                                    .append(Component.text(count + " Sekunden", NamedTextColor.RED))
                                    .append(Component.text(".", NamedTextColor.GRAY))
                            );
                        }

                        if (paused) {
                            Messages.actionBar(Component.text("Countdown ", NamedTextColor.GRAY)
                                    .append(Component.text("pausiert", NamedTextColor.RED)));
                        } else {
                            Messages.actionBar(Component.text(count, NamedTextColor.RED, TextDecoration.BOLD)
                                    .append(Component.text("..", NamedTextColor.GRAY)));
                        }
                    } else {

                        Messages.actionBar(Component.text("Warten auf ", NamedTextColor.GRAY)
                                .append(Component.text("Spieler", NamedTextColor.RED))
                                .append(Component.text(".. (", NamedTextColor.GRAY))
                                .append(Component.text(Bukkit.getOnlinePlayers().size(), NamedTextColor.RED))
                                .append(Component.text("/", NamedTextColor.GRAY))
                                .append(Component.text(NEEDED_PlAYERS, NamedTextColor.RED))
                                .append(Component.text(")", NamedTextColor.GRAY)));
                        count = 199;
                    }

                }

            }
        });

    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getItem().getType().equals(Material.SADDLE)) {
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

        player.getInventory().setItem(4, new ItemStackBuilder(Material.SADDLE).withName("§c§lTeams §7(Right-click)")
                .withLore("§7§oBetrete ein Team.").buildStack());

        player.teleport(LocationAPI.getLocation("Camp"));
    }

    @Override
    public void onDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;
        event.setCancelled(true);
    }

    @Override
    public void onDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;
        event.setCancelled(true);
    }
}
