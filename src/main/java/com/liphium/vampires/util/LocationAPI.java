package com.liphium.vampires.util;

import com.liphium.vampires.Vampires;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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

    /**
     * Get a location by name in the vampires world
     *
     * @param locationName Name of the location in the config file
     * @return The location in the specified world
     */
    public static Location getLocation(String locationName) {

        // Send a warning when the location doesn't exist yet
        if (!exists(locationName)) {
            Bukkit.broadcast(Component.text("Location " + locationName + " not found!").color(NamedTextColor.RED));
            return null;
        }

        // Check if the location has already been cached
        if (locations.containsKey(locationName)) {
            return locations.get(locationName);
        }

        // Get all the values of the location from the config
        double x = Vampires.getInstance().getConfig().getDouble(locationName + ".X");
        double y = Vampires.getInstance().getConfig().getDouble(locationName + ".Y");
        double z = Vampires.getInstance().getConfig().getDouble(locationName + ".Z");
        float yaw = (float) Vampires.getInstance().getConfig().getDouble(locationName + ".Yaw");
        float pitch = (float) Vampires.getInstance().getConfig().getDouble(locationName + ".Pitch");

        // Make the location
        Location loc = new Location(Bukkit.getWorld("vampires"), x, y, z, yaw, pitch);

        // Cache the location for use in the future
        locations.put(locationName, loc);
        return loc;
    }

}
