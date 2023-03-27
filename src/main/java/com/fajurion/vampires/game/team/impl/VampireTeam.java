package com.fajurion.vampires.game.team.impl;

import com.fajurion.vampires.Vampires;
import com.fajurion.vampires.game.team.Team;
import com.fajurion.vampires.util.LocationAPI;
import de.nightempire.servercore.util.ItemStackBuilder;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class VampireTeam extends Team {

    public final HashMap<Player, Integer> bloodLevel = new HashMap<>();

    public VampireTeam() {
        super("Vampire", "§c§l", Material.REDSTONE);
    }

    @Override
    public void giveKit(Player player) {
        player.getInventory().setBoots(new ItemStackBuilder(Material.LEATHER_BOOTS).makeUnbreakable()
                .withLeatherColor(Color.RED).buildStack());
        player.getInventory().setHelmet(new ItemStackBuilder(Material.LEATHER_HELMET).makeUnbreakable()
                .withLeatherColor(Color.RED).buildStack());
        player.getInventory().setLeggings(new ItemStackBuilder(Material.NETHERITE_LEGGINGS).makeUnbreakable().buildStack());

        player.getInventory().addItem(new ItemStackBuilder(Material.BLAZE_ROD).makeUnbreakable()
                .withName("§c§lCatcher").withLore(
                        "§7§oWenn du mit diesem Item",
                        "§7§oeinen Menschen 2-mal schlägst",
                        "§7§owird er in den Kerker teleportiert.",
                        "§7§oBeim in der Hand halten bekommst du",
                        "§7§oSlowness I!")
                .addEnchantment(Enchantment.KNOCKBACK, 2).buildStack());

        player.getInventory().addItem(new ItemStackBuilder(Material.FEATHER).makeUnbreakable()
                .withName("§c§lDash").withLore(
                        "§7§oWenn du mit diesem Item",
                        "§7§orechtsklickst wirst du 5 Blöcke",
                        "§7§onach vorne geschleudert.")
                .buildStack());

        player.getInventory().addItem(new ItemStackBuilder(Material.CROSSBOW).withName("§c§lCrossbow")
                .withLore("§7§oWenn du mit diesem Crossbow",
                        "§7§oeine Fackel oder Maschine der",
                        "§7§oMenschen abschießt, wird sie zerstört!")
                .makeUnbreakable().buildStack());

        ItemStack rocket = new ItemStackBuilder(Material.FIREWORK_ROCKET).withName("§c§lRakete")
                .withLore("§7§oMunition für den Crossbow.").buildStack();
        FireworkMeta meta = (FireworkMeta) rocket.getItemMeta();
        meta.setPower(3);
        meta.addEffect(FireworkEffect.builder().withColor(Color.RED).build());
        rocket.setItemMeta(meta);

        player.getInventory().setItemInOffHand(rocket);

        player.teleport(LocationAPI.getLocation("Cave"));
    }

    @Override
    public void sendStartMessage() {
        for(Player player : getPlayers()) {

            player.sendMessage(" ");
            player.sendMessage("    §7Du bist ein §c§lVampir§7!");
            player.sendMessage(" ");
            player.sendMessage("§7Eliminiere alle §cMenschen §7bevor dir");
            player.sendMessage("§7das §cBlut §7ausgeht.");
            player.sendMessage(" ");

            bloodLevel.put(player, ThreadLocalRandom.current().nextInt(50, 60));
        }
    }

    int tickCount = 0;

    @Override
    public void tick() {
        for(Player player : getPlayers()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§8[§7" + getLongString(bloodLevel.get(player)) + "§8]"));
        }

        if(tickCount++ >= 120) {
            tickCount = 0;

            for(Player player : getPlayers()) {
                bloodLevel.put(player, bloodLevel.get(player) - 1);

                if(bloodLevel.get(player) == 0) {
                    Vampires.getInstance().getGameManager().getCurrentState().handleDeath(player);
                }

                player.getActivePotionEffects().clear();

                int level = bloodLevel.get(player);
                if(level >= 50) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 130, 0));
                }

                if(level >= 33) {
                    int amplifier = level >= 66 ? 1 : 0;
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 130, amplifier));
                }
            }
        }
    }

    public String getLongString(int level) {
        String levelText = "▰▰▰▰▰▰▰▰ " + level + "/100 ▰▰▰▰▰▰▰▰";
        StringBuilder experienceCount = new StringBuilder();
        char[] chars = levelText.toCharArray();

        for(int i = 0; i < levelText.length(); i++) {
            char character = chars[i];
            if((100 / levelText.length())*i < level) {
                experienceCount.append("§c");
            } else experienceCount.append("§7");

            experienceCount.append(character);
        }

        return experienceCount.toString();
    }

    @Override
    public void handleWin() {

        Bukkit.broadcastMessage(" ");
        Bukkit.broadcastMessage("   §cDie §c§lVampire §7haben §cgewonnen§7!");
        Bukkit.broadcastMessage(" ");
        Bukkit.broadcastMessage("§7Die §cWelt §7wurde von den §cVampiren §7eingenommen");
        Bukkit.broadcastMessage("§7und §cunterworfen§7!");
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
