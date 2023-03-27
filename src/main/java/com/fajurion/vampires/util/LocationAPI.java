package com.fajurion.vampires.util;

import com.fajurion.vampires.Vampires;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;

public class LocationAPI {

    private static final HashMap<String, Location> locations = new HashMap<>();

    public static void setLocation(String locationName, Location loc) {
        Vampires.getInstance().getConfig().set(locationName + ".X", loc.getX());
        Vampires.getInstance().getConfig().set(locationName + ".Y", loc.getY());
        Vampires.getInstance().getConfig().set(locationName + ".Z", loc.getZ());
        Vampires.getInstance().getConfig().set(locationName + ".Yaw", loc.getYaw());
        Vampires.getInstance().getConfig().set(locationName + ".Pitch", loc.getPitch());
        Vampires.getInstance().getConfig().set(locationName + ".World", loc.getWorld().getName());
        Vampires.getInstance().saveConfig();
        locations.put(locationName, loc);
    }

    public static boolean exists(String locationName) {
        return Vampires.getInstance().getConfig().isSet(locationName + ".X");
    }

    public static Location getLocation(String locationName) {
        if(!exists(locationName)) {
            Bukkit.broadcastMessage("§cLocation" + locationName + " not found!");
            return null;
        }

        if(locations.containsKey(locationName)) {
            return locations.get(locationName);
        }
        double x = Vampires.getInstance().getConfig().getDouble(locationName + ".X");
        double y = Vampires.getInstance().getConfig().getDouble(locationName + ".Y");
        double z = Vampires.getInstance().getConfig().getDouble(locationName + ".Z");
        float yaw = (float) Vampires.getInstance().getConfig().getDouble(locationName + ".Yaw");
        float pitch = (float) Vampires.getInstance().getConfig().getDouble(locationName + ".Pitch");
        String world = Vampires.getInstance().getConfig().getString(locationName + ".World");
        World w = Bukkit.getWorld(world);
        Location loc = new Location(w, x, y, z, yaw, pitch);
        locations.put(locationName, loc);
        return loc;
    }

}
