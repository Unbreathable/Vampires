package com.liphium.vampires.game;

import com.liphium.core.Core;
import com.liphium.vampires.game.state.LobbyState;
import com.liphium.vampires.game.team.TeamManager;
import com.liphium.vampires.screens.TeamSelectionScreen;
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
        return 20;
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public GameState getCurrentState() {
        return currentState;
    }
}
