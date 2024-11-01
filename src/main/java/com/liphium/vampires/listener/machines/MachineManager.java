package com.liphium.vampires.listener.machines;

import com.liphium.vampires.listener.machines.impl.*;
import com.liphium.vampires.util.LocationAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;

public class MachineManager {

    private final ArrayList<Machine> machines = new ArrayList<>();

    public MachineManager() {

        ArrayList<String> registered = new ArrayList<>();

        // Add all machines (all the ones that can be spawned by location)
        registered.add("PumpkinDropper");
        registered.add("RespawnBeacon");
        registered.add("ItemShop");

        for (String s : registered) {
            for (int i = 1; i <= 1000; i++) {
                if (LocationAPI.exists(s + i)) {
                    machines.add(newMachineByLocation(s, LocationAPI.getLocation(s + i)));
                } else break;
            }
        }

    }

    public Machine getMachine(String name) {
        for (Machine machine : machines) {
            if (machine.getClass().getSimpleName().equalsIgnoreCase(name)) {
                return machine;
            }
        }

        return null;
    }

    public Machine getMachine(Location location) {
        for (Machine machine : machines) {
            if (machine.getLocation().distance(location) <= 0.1) {
                return machine;
            }
        }

        return null;
    }

    public void onInteract(PlayerInteractEvent event) {
        for (Machine machine : machines) {
            machine.onInteract(event);
        }
    }

    public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
        for (Machine machine : machines) {
            machine.onInteractAtEntity(event);
        }
    }

    public ArrayList<Machine> getMachines() {
        return machines;
    }

    public Machine newMachineByLocation(String name, Location location) {
        return switch (name) {
            case "PumpkinDropper" -> new PumpkinDropper(location, false);
            case "ItemShop" -> new ItemShop(location);
            default -> null;
        };
    }

    public Machine newMachineByMaterial(Material material, Location location) {
        return switch (material) {
            case Material.PUMPKIN -> new PumpkinDropper(location, true);
            case Material.BEACON -> new TorchDropper(location);
            case Material.RED_WOOL -> new BeetrootDropper(location);
            case Material.BREWING_STAND -> new Brewer(location);
            case Material.REDSTONE_LAMP -> new RocketDropper(location);
            case Material.GOLD_BLOCK -> new GoldenAppleDropper(location);
            default -> null;
        };
    }

    public boolean breakLocation(Location location) {
        Machine toRemove = null;
        for (Machine machine : machines) {
            if (machine.isBreakable() && machine.getLocation().getBlock().getLocation().equals(location)) {
                machine.destroy();
                toRemove = machine;
            }
        }

        if (toRemove != null) {
            machines.remove(toRemove);
            return true;
        }

        return false;
    }

    public void addMachine(Machine machine) {
        machines.add(machine);
    }

    public void tick() {
        for (Machine machine : machines) {
            machine.tick();
        }
    }

}
