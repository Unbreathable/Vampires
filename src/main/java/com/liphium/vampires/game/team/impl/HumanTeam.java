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

        player.getInventory().addItem(new ItemStackBuilder(Material.DIAMOND_SWORD).makeUnbreakable().buildStack());

        player.teleport(LocationAPI.getLocation("Camp"));
    }

    @Override
    public void sendStartMessage() {
        for (Player player : getPlayers()) {

            player.sendMessage(" ");
            player.sendMessage("    §7Du bist ein §c§lMensch§7!");
            player.sendMessage(" ");
            player.sendMessage("§7Verteidige das §cCamp §7vor den §c§lVampiren");
            player.sendMessage("§7oder §celiminiere §7alle §c§lVampire§7.");
            player.sendMessage(" ");

        }
    }

    @Override
    public void handleWin() {

        Bukkit.broadcast(Component.text(" "));
        Bukkit.broadcast(Component.text("   Die ", NamedTextColor.GREEN)
                .append(Component.text("Menschen", NamedTextColor.GREEN, TextDecoration.BOLD))
                .append(Component.text(" haben ", NamedTextColor.GRAY))
                .append(Component.text("gewonnen", NamedTextColor.GREEN))
                .append(Component.text("!", NamedTextColor.GRAY)));
        Bukkit.broadcast(Component.text(" "));
        Bukkit.broadcast(Component.text("Die ", NamedTextColor.GRAY)
                .append(Component.text("Welt", NamedTextColor.GREEN))
                .append(Component.text(" wurde von den ", NamedTextColor.GRAY))
                .append(Component.text("Menschen", NamedTextColor.GREEN))
                .append(Component.text(" gesichert", NamedTextColor.GRAY)));
        Bukkit.broadcast(Component.text("und kann weiter ", NamedTextColor.GRAY)
                .append(Component.text("friedlich", NamedTextColor.GREEN))
                .append(Component.text(" existieren!", NamedTextColor.GRAY)));
        Bukkit.broadcast(Component.text(" "));

        for (Player player : getPlayers()) {
            player.showTitle(Title.title(
                    Component.text("Sieg", NamedTextColor.GREEN, TextDecoration.BOLD),
                    Component.empty(),
                    Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(1))
            ));
            player.playSound(player.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_1, 1f, 1f);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!getPlayers().contains(player)) {
                player.showTitle(Title.title(
                        Component.text("Verloren", NamedTextColor.RED, TextDecoration.BOLD),
                        Component.empty(),
                        Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(1))
                ));
                player.playSound(player.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_1, 1f, 1f);
            }
        }

    }
}
