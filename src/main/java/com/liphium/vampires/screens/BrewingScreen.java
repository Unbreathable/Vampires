package com.liphium.vampires.screens;

import com.liphium.core.inventory.CItem;
import com.liphium.core.inventory.CScreen;
import com.liphium.core.util.ItemStackBuilder;
import com.liphium.vampires.Vampires;
import com.liphium.vampires.listener.machines.impl.Brewer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import java.util.Objects;

public class BrewingScreen extends CScreen {

    public BrewingScreen() {
        super(2, Component.text("Brauen", NamedTextColor.RED, TextDecoration.BOLD), 3, true);

        background();

        PotionType[] potions = new PotionType[]{
                PotionType.SWIFTNESS,
                PotionType.STRONG_LEAPING,
                PotionType.INVISIBILITY,
        };

        // 9 10 11 12 13 14 15 16 17
        int count = 0;
        for (PotionType data : potions) {

            // Build potion
            ItemStack itemStack = new ItemStackBuilder(Material.LINGERING_POTION)
                    .withName(Component.text(convertToReadable(data.name()), NamedTextColor.GOLD).appendSpace()
                            .append(Component.text("potion", NamedTextColor.GRAY)))
                    .buildStack();
            PotionMeta meta = (PotionMeta) itemStack.getItemMeta();
            meta.setBasePotionType(data);
            itemStack.setItemMeta(meta);

            setItem(12 + count, new CItem(itemStack).onClick(event -> {
                Brewer brewer = Brewer.clickedBrewer.get(event.getPlayer());
                // Build potion
                ItemStack potion = new ItemStack(Material.LINGERING_POTION);
                PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
                potionMeta.setBasePotionType(data);
                potionMeta.displayName(Component.text(convertToReadable(data.name()), NamedTextColor.GOLD).appendSpace()
                        .append(Component.text("potion", NamedTextColor.GRAY)));
                potion.setItemMeta(potionMeta);

                if (brewer.currentPotion != null) {
                    event.getPlayer().closeInventory();
                    event.getPlayer().sendMessage(Component.text("A potion is already being produced.", NamedTextColor.RED));
                    return;
                }

                brewer.currentPotion = potion;
                event.getPlayer().closeInventory();
            }));
            count++;
        }
    }

    public static String convertToReadable(String input) {
        input = input.toLowerCase().replace("_", " "); // Convert to lowercase and replace underscores with spaces
        String[] words = input.split(" ");

        // Capitalize only the first word
        words[0] = words[0].substring(0, 1).toUpperCase() + words[0].substring(1);

        // Join the words back together into a sentence
        return String.join(" ", words);
    }

}
