package com.fajurion.vampires.game.team;

import com.fajurion.vampires.Vampires;
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

    public ArrayList<String> playerLore(int size) {
        ArrayList<String> lore = new ArrayList<>();

        for(int i = 0; i < size; i++) {
            lore.add("§7- " + (players.size() >= i+1 ? cc.substring(0, 2) + players.get(i).getName() : "§7§oEmpty"));
        }

        return lore;
    }

    public boolean isJoinable() {
        return Vampires.getInstance().getGameManager().getMaxTeamSize() > getPlayers().size();
    }

    public void giveKit(Player player) {}
    public void tick() {}
    public void sendStartMessage() {}
    public void join(Player player) {}

    public void handleWin() {}
}
