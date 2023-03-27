package com.fajurion.vampires;

import com.fajurion.vampires.command.SetCommand;
import com.fajurion.vampires.command.TimerCommand;
import com.fajurion.vampires.game.GameManager;
import com.fajurion.vampires.listener.ChatListener;
import com.fajurion.vampires.listener.GameListener;
import com.fajurion.vampires.listener.JoinQuitListener;
import com.fajurion.vampires.listener.machines.Machine;
import com.fajurion.vampires.listener.machines.MachineManager;
import com.fajurion.vampires.screens.BrewingScreen;
import com.fajurion.vampires.screens.TeamSelectionScreen;
import com.fajurion.vampires.util.TaskManager;
import de.nightempire.servercore.Core;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Vampires extends JavaPlugin {

    public static final String PREFIX = "§8[§cVampires§8]§r ";
    private static Vampires instance;

    private TaskManager taskManager;

    private GameManager gameManager;

    private MachineManager machineManager;

    @Override
    public void onEnable() {
        instance = this;

        taskManager = new TaskManager();
        taskManager.initTask();

        gameManager = new GameManager();

        machineManager = new MachineManager();

        Listener[] listeners = new Listener[] {new GameListener(), new ChatListener(), new JoinQuitListener()};
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, this);
        }

        getCommand("set").setExecutor(new SetCommand());
        getCommand("timer").setExecutor(new TimerCommand());

        Core.getInstance().getScreens().register(new TeamSelectionScreen(), new BrewingScreen());
    }

    @Override
    public void onDisable() {
        for(Machine machine : machineManager.getMachines()) {
            machine.destroy();
        }
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public MachineManager getMachineManager() {
        return machineManager;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public static Vampires getInstance() {
        return instance;
    }
}
