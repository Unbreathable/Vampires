package com.liphium.vampires.game.team;

import com.liphium.vampires.Vampires;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Team {

    private final String name, cc;
    private final Material material;
    private final ArrayList<Player> players = new ArrayList<>();

    public Team(String name, String cc, Material material) {
        this.name = name;
        this.cc = cc;
        this.material = material;
    }

    public String getName() {
        return name;
    }

    public String getCc() {
        return cc;
    }

    public Material getMaterial() {
        return material;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ArrayList<String> playerLore() {
        ArrayList<String> lore = new ArrayList<>();

        if (players.isEmpty()) {
            lore.add("§7Click to join!");
        } else {
            for (Player player : players) {
                lore.add("§7- " + cc.substring(0, 2) + player.getName());
            }
        }

        return lore;
    }

    public boolean isJoinable() {
        return Vampires.getInstance().getGameManager().getMaxTeamSize() > getPlayers().size();
    }

    public void giveKit(Player player) {
    }

    public void tick() {
    }

    public void sendStartMessage() {
    }

    public void join(Player player) {
    }

    public void handleWin() {
    }
}