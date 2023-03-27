package com.fajurion.vampires.screens;

import com.fajurion.vampires.Vampires;
import com.fajurion.vampires.game.team.Team;
import de.nightempire.servercore.Core;
import de.nightempire.servercore.inventory.CClickEvent;
import de.nightempire.servercore.inventory.CItem;
import de.nightempire.servercore.inventory.CScreen;
import de.nightempire.servercore.util.ItemStackBuilder;

public class TeamSelectionScreen extends CScreen {

    public TeamSelectionScreen() {
        super(1, "§c§lTeams", 3, true);

        background();
        rebuild();
    }

    public void rebuild() {
        int maxSize = Vampires.getInstance().getGameManager().getMaxTeamSize();

        // 9 10 11 12 13 14 15 16 17
        for(Team team : Vampires.getInstance().getGameManager().getTeamManager().getTeams()) {
            int slot = team.getName().equals("Vampire") ? 10 : 16;

            setItem(slot, new CItem(new ItemStackBuilder(team.getMaterial()).withLore(team.playerLore(maxSize))
                    .withName(team.getCc() + team.getName()).buildStack())
                    .onClick(event -> click(slot, team, maxSize, event)));
        }
    }

    public void click(int slot, Team team, int maxSize, CClickEvent event) {

        if(team.getPlayers().contains(event.getPlayer())) {
            team.getPlayers().remove(event.getPlayer());
        } else if(team.getPlayers().size() >= maxSize) {

            event.getPlayer().closeInventory();
            event.getPlayer().sendMessage(Vampires.PREFIX + "§7Dieses §cTeam §7ist §cvoll§7!");
            return;
        } else {

            for(Team t : Vampires.getInstance().getGameManager().getTeamManager().getTeams()) {
                t.getPlayers().remove(event.getPlayer());
            }
            team.addPlayer(event.getPlayer());
        }

        rebuild();
    }

}
