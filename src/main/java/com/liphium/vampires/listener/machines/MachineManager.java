package com.liphium.vampires.listener.machines;

import com.liphium.vampires.listener.machines.impl.*;
import com.liphium.vampires.util.LocationAPI;
import org.bukkit.Location;
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
            case "PumpkinDropper" -> new PumpkinDropper(location);
            case "RespawnBeacon" -> new RocketDropper(location);
            default -> null;
        };
    }

    public Machine newMachineByItemName(String name, Location location) {
        return switch (name) {
            case "RocketDropper" -> new RocketDropper(location);
            case "TorchDropper" -> new TorchDropper(location);
            case "BeetrootDropper" -> new BeetrootDropper(location);
            case "Brewer" -> new Brewer(location);
            default -> null;
        };
    }

    public void breakLocation(Location location) {
        for (Machine machine : machines) {
            if (machine.isBreakable() && machine.getLocation().distance(location) <= 1.5) {
                machine.setBroken(true);
                machine.onBreak();
            }
        }
    }

    public void tick() {
        for (Machine machine : machines) {
            machine.tick();
        }
    }

}
