package com.fajurion.vampires.screens;

import com.fajurion.vampires.Vampires;
import com.fajurion.vampires.listener.machines.impl.Brewer;
import de.nightempire.servercore.inventory.CItem;
import de.nightempire.servercore.inventory.CScreen;
import de.nightempire.servercore.util.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class BrewingScreen extends CScreen {

    public BrewingScreen() {
        super(2, "§c§lBrauen", 3, true);

        background();

        PotionData[] potions = new PotionData[] {new PotionData(PotionType.SPEED, false, false),
                new PotionData(PotionType.JUMP, false, true),
                new PotionData(PotionType.INVISIBILITY, false, false)};

        // 9 10 11 12 13 14 15 16 17
        int count = 0;
        for(PotionData data : potions) {

            // Build potion
            ItemStack itemStack = new ItemStackBuilder(Material.LINGERING_POTION)
                    .withName("§c§l" + data.getType().name() + " §7Potion").buildStack();
            PotionMeta meta = (PotionMeta) itemStack.getItemMeta();
            meta.setBasePotionData(data);
            itemStack.setItemMeta(meta);

            setItem(12+count, new CItem(itemStack).onClick(event -> {
                Brewer brewer = (Brewer) Vampires.getInstance().getMachineManager().getMachine("Brewer");

                if(brewer.currentPotion != null) {
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
