package com.liphium.core.inventory;

import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class CItem {

    private final ItemStack stack;
    private Consumer<CClickEvent> clickFunction;
    private boolean clickable = true;

    public CItem(ItemStack stack) {
        this.stack = stack;
    }

    public CItem onClick(Consumer<CClickEvent> clickFunction) {
        this.clickFunction = clickFunction;
        return this;
    }

    public CItem notClickable() {
        this.clickable = false;
        return this;
    }

    public boolean click(CClickEvent event) {
        if (!clickable) return true;

        if (clickFunction == null) {
            return false;
        }

        clickFunction.accept(event);
        return true;
    }

    public ItemStack getStack() {
        return stack;
    }
}
