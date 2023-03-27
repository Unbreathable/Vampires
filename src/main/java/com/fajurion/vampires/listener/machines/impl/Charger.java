package com.fajurion.vampires.listener.machines.impl;

import com.fajurion.vampires.Vampires;
import com.fajurion.vampires.game.state.IngameState;
import com.fajurion.vampires.game.team.impl.VampireTeam;
import com.fajurion.vampires.listener.machines.Machine;
import com.fajurion.vampires.util.LocationAPI;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

public class Charger extends Machine {

    private final ArmorStand stand;

    int bloodLevel = 0;

    public Charger(Location location) {
        super(location, false);

        stand = location.getWorld().spawn(location.clone().add(0, -1.5, 0), ArmorStand.class);

        stand.setCustomNameVisible(true);
        stand.setCustomName("§8[§7" + getLongString(0) + "§8]");
        stand.setGravity(false);
        stand.setInvisible(true);
        stand.setInvulnerable(true);
        stand.setRemoveWhenFarAway(false);
    }

    int tickCount = 0, rechargeTicks = 0, chargeTicks = 0;

    @Override
    public void tick() {
        if(tickCount++ >= 7) {
            tickCount = 0;

            // Recharge charger
            Location origin = LocationAPI.getLocation("Cell");
            IngameState state = (IngameState) Vampires.getInstance().getGameManager().getCurrentState();

            int amount = 0;
            for(Player player : Vampires.getInstance().getGameManager().getTeamManager().getTeam("Menschen").getPlayers()) {
                if(player.getLocation().distance(origin) <= 7) {
                    amount += 1;
                } else state.prison.remove(player);
            }

            if(rechargeTicks++ >= 4) {
                rechargeTicks = 0;
                bloodLevel += amount;
            }

            // Recharge vampires
            if(chargeTicks++ >= 1) {
                chargeTicks = 0;

                VampireTeam team = (VampireTeam) Vampires.getInstance().getGameManager().getTeamManager().getTeam("Vampire");
                for(Player player : team.getPlayers()) {
                    if(player.getLocation().distance(location) <= 1.5 && bloodLevel > 0) {
                        team.bloodLevel.put(player, team.bloodLevel.get(player) + 1);
                        bloodLevel--;
                        break;
                    }
                }
            }

            stand.setCustomName("§8[§7" + getLongString(bloodLevel) + "§8]");
        }
    }

    @Override
    public void destroy() {
        stand.remove();
    }

    public String getLongString(int level) {
        String levelText = "▰▰▰▰▰▰▰▰ " + level + "/100 ▰▰▰▰▰▰▰▰";
        StringBuilder experienceCount = new StringBuilder();
        char[] chars = levelText.toCharArray();

        for(int i = 0; i < levelText.length(); i++) {
            char character = chars[i];
            if((100 / levelText.length())*i < level) {
                experienceCount.append("§c");
            } else experienceCount.append("§7");

            experienceCount.append(character);
        }

        return experienceCount.toString();
    }
}
