package com.liphium.core.listener;

import com.liphium.core.Core;
import com.liphium.core.inventory.CClickEvent;
import com.liphium.core.inventory.CItem;
import com.liphium.core.inventory.CScreen;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {

        CScreen screen = Core.getInstance().getScreens().getScreenFromInventory((Player) event.getPlayer(), event.getView().title(), event.getInventory());

        if (screen != null) {
            screen.close((Player) event.getPlayer(), event);
        }

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        CScreen screen = Core.getInstance().getScreens().getScreenFromInventory(player, event.getView().title(), event.getInventory());

        if (screen != null) {
            CItem item = screen.item(player, event.getSlot());

            if (item != null) {
                event.setCancelled(item.click(new CClickEvent(player, event.getCurrentItem())));
            }
        }
    }

}
