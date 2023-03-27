package com.fajurion.vampires.game;

import com.fajurion.vampires.game.state.LobbyState;
import com.fajurion.vampires.game.team.TeamManager;
import com.fajurion.vampires.screens.TeamSelectionScreen;
import de.nightempire.servercore.Core;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GameManager {

    private GameState currentState;

    private final TeamManager teamManager;

    public GameManager() {
        teamManager = new TeamManager();
        setCurrentState(new LobbyState());
    }

    public void setCurrentState(GameState currentState) {
        this.currentState = currentState;
        this.currentState.start();
    }

    public void join(Player player) {
        this.currentState.join(player);

        TeamSelectionScreen screen = (TeamSelectionScreen) Core.getInstance().getScreens().screen(1);
        screen.rebuild();
    }

    public void quit(Player player) {
        this.currentState.quit(player);
    }

    public int getMaxTeamSize() {
        return 5;
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public GameState getCurrentState() {
        return currentState;
    }
}
