package com.liphium.core.inventory;

import com.liphium.core.util.ItemStackBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public abstract class CScreen {

    private final Component title;
    private final int rows;
    private final boolean cache;
    private Inventory cached;
    private final int id;

    public CScreen(int id, Component title, int rows, boolean cache) {
        this.id = id;
        this.title = title;
        this.rows = rows;
        this.cache = cache;
    }

    private final HashMap<Integer, CItem> items = new HashMap<>();
    private final HashMap<Player, HashMap<Integer, CItem>> customItems = new HashMap<>();

    public void setItem(int slot, CItem item) {
        items.put(slot, item);
        if (cache && cached != null) {
            cached.setItem(slot, item.getStack());
        }
    }

    public void setItemNotCached(Player player, int slot, CItem item) {
        HashMap<Integer, CItem> items = customItems.getOrDefault(player, new HashMap<>());
        items.put(slot, item);
        customItems.put(player, items);
    }

    public void setItemNotCached(Player player, int slot, CItem item, Inventory inventory) {
        setItemNotCached(player, slot, item);
        inventory.setItem(slot, item.getStack());
    }

    public void init(Player player) {
    }

    public void init(Player player, Inventory inventory) {

    }

    public Component title(Player player) {
        return title;
    }

    public void close(Player player, InventoryCloseEvent event) {
    }

    public void background() {
        ItemStack stack = new ItemStackBuilder(Material.BLACK_STAINED_GLASS_PANE).withName(Component.text("§r")).buildStack();
        for (int i = 0; i < 9; i++) setItem(i, new CItem(stack).notClickable());
        for (int i = rows * 9 - 9; i < rows * 9; i++) setItem(i, new CItem(stack).notClickable());
    }

    public void background(Player player) {
        ItemStack stack = new ItemStackBuilder(Material.BLACK_STAINED_GLASS_PANE).withName(Component.text("§r")).buildStack();
        for (int i = 0; i < 9; i++) setItemNotCached(player, i, new CItem(stack).notClickable());
        for (int i = rows * 9 - 9; i < rows * 9; i++) setItemNotCached(player, i, new CItem(stack).notClickable());
    }

    public Inventory buildInventory(Player player) {
        if (cache && cached != null) {
            return cached;
        }

        Inventory inventory = Bukkit.createInventory(null, rows * 9, title(player));

        if (!cache) {
            // Clear all the custom items when the thing is not cached
            customItems.remove(player);
        }
        init(player);
        init(player, inventory);
        for (Map.Entry<Integer, CItem> entry : customItems.getOrDefault(player, new HashMap<>()).entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().getStack());
        }

        if (cache) {

            for (Map.Entry<Integer, CItem> entry : items.entrySet()) {
                inventory.setItem(entry.getKey(), entry.getValue().getStack());
            }

            cached = inventory;
        }

        return inventory;
    }

    public CItem item(Player player, int slot) {
        if (!cache) {
            return customItems.getOrDefault(player, new HashMap<>()).getOrDefault(slot, null);
        }
        return items.getOrDefault(slot, null);
    }

    public boolean isCached() {
        return cache;
    }

    public Component getTitle() {
        return title;
    }

    public Inventory getCached() {
        return cached;
    }

    public int getId() {
        return id;
    }
}
