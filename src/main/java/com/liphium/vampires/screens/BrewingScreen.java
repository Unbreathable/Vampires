package com.liphium.vampires.screens;

import com.liphium.core.inventory.CItem;
import com.liphium.core.inventory.CScreen;
import com.liphium.core.util.ItemStackBuilder;
import com.liphium.vampires.Vampires;
import com.liphium.vampires.listener.machines.impl.Brewer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

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
                    .withName("§c§l" + data.name() + " §7Potion").buildStack();
            PotionMeta meta = (PotionMeta) itemStack.getItemMeta();
            meta.setBasePotionType(data);
            itemStack.setItemMeta(meta);

            setItem(12 + count, new CItem(itemStack).onClick(event -> {
                Brewer brewer = (Brewer) Vampires.getInstance().getMachineManager().getMachine("Brewer");

                if (brewer.currentPotion != null) {
                    event.getPlayer().closeInventory();
                    event.getPlayer().sendMessage(Vampires.PREFIX + "§7Es wird bereits ein §cTrank §7hergestellt.");
                    return;
                }

                brewer.currentPotion = itemStack;
                event.getPlayer().closeInventory();
            }));
            count++;
        }
    }

}
