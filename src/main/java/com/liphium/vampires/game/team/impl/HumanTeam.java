package com.liphium.vampires.game.team.impl;

import com.liphium.core.util.ItemStackBuilder;
import com.liphium.vampires.game.team.Team;
import com.liphium.vampires.util.LocationAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Objects;


public class HumanTeam extends Team {

    public HumanTeam() {
        super("Humans", "§a§l", Material.GRASS_BLOCK);
    }

    @Override
    public void giveKit(Player player) {
        player.getInventory().setHelmet(new ItemStackBuilder(Material.NETHERITE_HELMET).makeUnbreakable()
                .addEnchantment(Enchantment.BLAST_PROTECTION, 3).buildStack());

        player.getInventory().setBoots(new ItemStackBuilder(Material.LEATHER_BOOTS).makeUnbreakable()
                .withLeatherColor(Color.GREEN).buildStack());

        player.getInventory().addItem(new ItemStackBuilder(Material.STONE_SWORD).makeUnbreakable().buildStack());

        player.teleport(Objects.requireNonNull(LocationAPI.getLocation("Camp")));
    }

    @Override
    public void sendStartMessage() {
        for (Player player : getPlayers()) {

            player.sendMessage(" ");
            player.sendMessage("    §7You are a §c§lhuman§7!");
            player.sendMessage(" ");
            player.sendMessage("§7Eliminate all §cvampires §7and free your");
            player.sendMessage("§cimprisoned §7comrads.");
            player.sendMessage(" ");

        }
    }

    @Override
    public void handleWin() {

        Bukkit.broadcast(Component.text(" "));
        Bukkit.broadcast(Component.text("   §aThe §a§lhumans §7won the §agame§7!"));
        Bukkit.broadcast(Component.text(" "));
        Bukkit.broadcast(Component.text("§7The world has been §asaved §7by the §ahumans"));
        Bukkit.broadcast(Component.text("§7and can §aexist §7for another day."));
        Bukkit.broadcast(Component.text(" "));

        for (Player player : getPlayers()) {
            player.showTitle(Title.title(
                    Component.text("Victory Royale", NamedTextColor.GREEN, TextDecoration.BOLD),
                    Component.empty(),
                    Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(1))
            ));
            player.playSound(player.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_1, 1f, 1f);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!getPlayers().contains(player)) {
                player.showTitle(Title.title(
                        Component.text("Game Over", NamedTextColor.RED, TextDecoration.BOLD),
                        Component.empty(),
                        Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(1))
                ));
                player.playSound(player.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_1, 1f, 1f);
            }
        }

    }
}
