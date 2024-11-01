package com.liphium.vampires.game.team.impl;

import com.liphium.core.util.ItemStackBuilder;
import com.liphium.vampires.Vampires;
import com.liphium.vampires.game.team.Team;
import com.liphium.vampires.util.LocationAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class VampireTeam extends Team {

    public final HashMap<Player, Integer> bloodLevel = new HashMap<>();

    public VampireTeam() {
        super("Vampires", "§c§l", Material.REDSTONE);
    }

    @Override
    public void giveKit(Player player) {
        player.getInventory().setBoots(new ItemStackBuilder(Material.LEATHER_BOOTS).makeUnbreakable()
                .withLeatherColor(Color.RED).buildStack());
        player.getInventory().setHelmet(new ItemStackBuilder(Material.LEATHER_HELMET).makeUnbreakable()
                .withLeatherColor(Color.RED).buildStack());
        player.getInventory().setLeggings(new ItemStackBuilder(Material.NETHERITE_LEGGINGS).makeUnbreakable().buildStack());

        player.getInventory().addItem(new ItemStackBuilder(Material.STICK).makeUnbreakable()
                .withName(Component.text("§c§lCatcher")).withLore(
                        Component.text("§7§oWhen you hit a human with"),
                        Component.text("§7§othis stick they will be infected,"),
                        Component.text("§7§owhen you hit them again, they will"),
                        Component.text("§7§obe sent straight to the cell!"))
                .addEnchantment(Enchantment.KNOCKBACK, 2).buildStack());

        player.getInventory().addItem(new ItemStackBuilder(Material.CROSSBOW).withName(Component.text("§c§lCrossbow"))
                .withLore(Component.text("§7§oWhen you shoot at torches"),
                        Component.text("§7§othey'll be destroyed."))
                .makeUnbreakable().buildStack());

        ItemStack rocket = new ItemStackBuilder(Material.FIREWORK_ROCKET).withName(Component.text("§c§lRockets"))
                .withLore(Component.text("§7§oAmmo for your crossbow.")).buildStack();
        FireworkMeta meta = (FireworkMeta) rocket.getItemMeta();
        meta.setPower(3);
        meta.addEffect(FireworkEffect.builder().withColor(Color.RED).build());
        rocket.setItemMeta(meta);

        player.getInventory().setItemInOffHand(rocket);

        player.teleport(Objects.requireNonNull(LocationAPI.getLocation("Cave")));
    }

    @Override
    public void sendStartMessage() {
        for (Player player : getPlayers()) {

            player.sendMessage(" ");
            player.sendMessage("    §7You are a §c§lvampire§7!");
            player.sendMessage(" ");
            player.sendMessage("§7Get all §chumans §7into §7the §ccell");
            player.sendMessage("§7before you are §celiminated§7.");
            player.sendMessage(" ");

            bloodLevel.put(player, ThreadLocalRandom.current().nextInt(50, 60));
        }
    }

    @Override
    public void handleWin() {

        Bukkit.broadcast(Component.text(" "));
        Bukkit.broadcast(Component.text("   §cThe §c§lvampires §7won the §cgame§7!"));
        Bukkit.broadcast(Component.text(" "));
        Bukkit.broadcast(Component.text("§7All §chumans §7were imprisoned and the §cvampires"));
        Bukkit.broadcast(Component.text("§7took over the §cworld§7!"));
        Bukkit.broadcast(Component.text(" "));

        for (Player player : getPlayers()) {
            player.sendTitle("§a§lVictory Royale", null, 10, 60, 10);
            player.playSound(player.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_1, 1f, 1f);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!getPlayers().contains(player)) {
                player.sendTitle("§c§lGame Over", null, 10, 60, 10);
                player.playSound(player.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_1, 1f, 1f);
            }
        }

    }
}
