package com.liphium.vampires.screens;

import com.liphium.core.inventory.CClickEvent;
import com.liphium.core.inventory.CItem;
import com.liphium.core.inventory.CScreen;
import com.liphium.core.util.ItemStackBuilder;
import com.liphium.vampires.Vampires;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class ItemShopScreen extends CScreen {

    public ItemShopScreen() {
        super(3, Component.text("Item shop", NamedTextColor.GOLD, TextDecoration.BOLD), 3, false);
    }

    @Override
    public void init(Player player, Inventory inventory) {
        background(player);

        // Add all the categories
        for (int i = 0; i < ShopCategory.values().length; i++) {
            int finalI = i;
            setItemNotCached(player, 10 + i, new CItem(ShopCategory.values()[i].getStack())
                    .onClick(event -> openCategory(event, finalI, inventory)));
        }
    }

    public void openCategory(CClickEvent event, int id, Inventory inventory) {
        final ShopCategory category = ShopCategory.values()[id];
        for (int i = 0; i < category.getItems().size(); i++) {
            setItemNotCached(event.getPlayer(), 19 + i, category.getItems().get(i));
            inventory.setItem(19 + i, category.getItems().get(i).getStack());
        }
    }

    enum ShopCategory {
        VAMPIRES(
                new ItemStackBuilder(Material.REDSTONE)
                        .withName(Component.text("Vampires", NamedTextColor.RED, TextDecoration.BOLD))
                        .withLore(Component.text("Stuff to catch those pesky creatures.", NamedTextColor.GRAY))
                        .buildStack(),
                List.of(
                        itemWithPrice(Material.FEATHER, "Dash", NamedTextColor.RED, 5, 1),
                        itemWithPrice(Material.BLAZE_ROD, "Instant catcher", NamedTextColor.RED, 10, 1),
                        spacer(),
                        itemWithPrice(Material.LEATHER, "Leather", NamedTextColor.RED, 1, 1),
                        itemWithPrice(Material.IRON_INGOT, "Iron ingot", NamedTextColor.RED, 3, 1),
                        itemWithPrice(Material.DIAMOND, "Diamond", NamedTextColor.RED, 7, 1),
                        spacer(),
                        itemWithPrice(Material.GOLDEN_APPLE, "Golden apple dropper", NamedTextColor.RED, 20, 1),
                        itemWithPrice(Material.FIREWORK_ROCKET, "Rocket dropper", NamedTextColor.RED, 30, 1)
                )
        ),
        HUMANS(
                new ItemStackBuilder(Material.GRASS_BLOCK)
                        .withName(Component.text("Humans", NamedTextColor.GREEN, TextDecoration.BOLD))
                        .withLore(Component.text("The stuff you need to survive.", NamedTextColor.GRAY))
                        .buildStack(),
                List.of(
                        itemWithPrice(Material.TORCH, "Torch", NamedTextColor.GREEN, 4, 1),
                        spacer(),
                        itemWithPrice(Material.IRON_INGOT, "Iron ingot", NamedTextColor.GREEN, 5, 1),
                        itemWithPrice(Material.DIAMOND, "Diamond", NamedTextColor.GREEN, 10, 1),
                        itemWithPrice(Material.MACE, "Mace", NamedTextColor.WHITE, 25, 1),
                        spacer(),
                        itemWithPrice(Material.BEETROOT, "Blood garlic dropper", NamedTextColor.GREEN, 20, 1),
                        itemWithPrice(Material.TORCH, "Torch dropper", NamedTextColor.GREEN, 30, 1)
                )
        ),
        TOOLS(
                new ItemStackBuilder(Material.IRON_PICKAXE)
                        .withName(Component.text("Tools & weapons", NamedTextColor.WHITE, TextDecoration.BOLD))
                        .withLore(Component.text("To aid you in the fight.", NamedTextColor.GRAY))
                        .buildStack(),
                List.of(
                        itemWithPrice(Material.IRON_SHOVEL, "Iron shovel", NamedTextColor.WHITE, 4, 1),
                        itemWithPriceCustom(Material.GOLDEN_SHOVEL, "Golden shovel", NamedTextColor.WHITE, 10,
                                new ItemStackBuilder(Material.GOLDEN_SHOVEL)
                                        .withName(Component.text("Golden shovel", NamedTextColor.WHITE))
                                        .withEnchantments(Map.of(Enchantment.EFFICIENCY, 2))
                                        .buildStack()
                        ),
                        itemWithPrice(Material.WIND_CHARGE, "Wind charge", NamedTextColor.WHITE, 20, 5)
                )
        ),
        BUILDING(
                new ItemStackBuilder(Material.REDSTONE)
                        .withName(Component.text("Building", NamedTextColor.GOLD, TextDecoration.BOLD))
                        .withLore(Component.text("Upgrade your base.", NamedTextColor.GRAY))
                        .buildStack(),
                List.of(
                        itemWithPrice(Material.CARVED_PUMPKIN, "Pumpkin dropper", NamedTextColor.GOLD, 5, 1),
                        itemWithPrice(Material.BREWING_STAND, "Brewer", NamedTextColor.GOLD, 50, 1),
                        itemWithPrice(Material.ARMOR_STAND, "Pop-up item shop", NamedTextColor.GOLD, 50, 1),
                        spacer(),
                        itemWithPrice(Material.REDSTONE_TORCH, "Alarm trap", NamedTextColor.GOLD, 5, 1),
                        itemWithPrice(Material.GLOWSTONE_DUST, "Glow trap", NamedTextColor.GOLD, 10, 1)
                )
        );

        final ItemStack stack;
        final List<CItem> items;

        ShopCategory(ItemStack stack, List<CItem> items) {
            this.stack = stack;
            this.items = items;
        }

        public ItemStack getStack() {
            return stack;
        }

        public List<CItem> getItems() {
            return items;
        }

        private static ItemStack item = new ItemStackBuilder(Material.BLACK_STAINED_GLASS_PANE).withName(Component.text("§r")).buildStack();

        public static CItem spacer() {
            return new CItem(item);
        }

        public static CItem itemWithPrice(Material material, String name, NamedTextColor color, int price, int amount) {
            final var stackBuilder = new ItemStackBuilder(material).withName(Component.text(name, color));

            return new CItem(stackBuilder
                    .withLore(Component.text("Price: ", NamedTextColor.GRAY).append(Component.text(price, NamedTextColor.GOLD)))
                    .buildStack()
            ).onClick(event -> buyFunction(event, stackBuilder.withAmount(amount).buildStack(), price));
        }

        public static CItem itemWithPriceCustom(Material material, String name, NamedTextColor color, int price, ItemStack sold) {
            return new CItem(new ItemStackBuilder(material).withName(Component.text(name, color))
                    .withLore(Component.text("Price: ", NamedTextColor.GRAY).append(Component.text(price, NamedTextColor.GOLD)))
                    .buildStack()
            ).onClick(event -> buyFunction(event, sold, price));
        }

        public static void buyFunction(CClickEvent event, ItemStack stack, int price) {
            event.getPlayer().closeInventory();

            // Get the amount of pumpkins in the inventory
            int count = 0;
            for (ItemStack item : event.getPlayer().getInventory()) {
                if (item.getType() == Material.CARVED_PUMPKIN) {
                    count += item.getAmount();
                }
            }

            if (count < price) {
                event.getPlayer().sendMessage(Vampires.PREFIX.append(Component.text("You don't have enough pumpkins to purchase this item.", NamedTextColor.RED)));
                return;
            }

            event.getPlayer().getInventory().addItem(stack);
        }
    }
}
