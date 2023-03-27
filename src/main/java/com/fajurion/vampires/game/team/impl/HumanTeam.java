package com.fajurion.vampires.game.team.impl;

import com.fajurion.vampires.game.team.Team;
import com.fajurion.vampires.util.LocationAPI;
import de.nightempire.servercore.util.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

public class HumanTeam extends Team {

    public HumanTeam() {
        super("Menschen", "§a§l", Material.GRASS_BLOCK);
    }

    @Override
    public void giveKit(Player player) {
        player.getInventory().setHelmet(new ItemStackBuilder(Material.NETHERITE_HELMET).makeUnbreakable()
                .addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 3).buildStack());

        player.getInventory().setBoots(new ItemStackBuilder(Material.LEATHER_BOOTS).makeUnbreakable()
                .withLeatherColor(Color.GREEN).buildStack());

        player.getInventory().addItem(new ItemStackBuilder(Material.DIAMOND_SWORD).makeUnbreakable().buildStack());

        player.teleport(LocationAPI.getLocation("Camp"));
    }

    @Override
    public void sendStartMessage() {
        for(Player player : getPlayers()) {

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

        Bukkit.broadcastMessage(" ");
        Bukkit.broadcastMessage("   §aDie §a§lMenschen §7haben §agewonnen§7!");
        Bukkit.broadcastMessage(" ");
        Bukkit.broadcastMessage("§7Die §aWelt §7wurde von den §aMenschen §7gesichert");
        Bukkit.broadcastMessage("§7und kann weiter §afriedlich §7existieren!");
        Bukkit.broadcastMessage(" ");

        for(Player player : getPlayers()) {
            player.sendTitle("§a§lSieg", null, 10, 60, 10);
            player.playSound(player.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_1, 1f, 1f);
        }

        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!getPlayers().contains(player)) {
                player.sendTitle("§c§lVerloren", null, 10, 60, 10);
                player.playSound(player.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_1, 1f, 1f);
            }
        }

    }
}
