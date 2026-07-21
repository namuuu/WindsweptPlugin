package fr.namu.windswept;

import fr.namu.windswept.event.DiceEvent;
import fr.namu.windswept.event.FarkleInteractionEvent;
import fr.namu.windswept.event.JoinLeaveEvent;
import fr.namu.windswept.event.MiningOreEvent;
import fr.namu.windswept.instance.FarkleInstance;
import fr.namu.windswept.manager.CommandManager;
import fr.namu.windswept.manager.FarkleManager;
import fr.namu.windswept.manager.PlayerManager;
import fr.namu.windswept.util.SavePlayerDataUtil;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private CommandManager commandManager;

    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        getLogger().info("Windswept plugin has been initialized");

        instance = this;

        this.commandManager = new CommandManager(this);
        this.commandManager.registerCommands();
        this.registerEvents();

        // Check if the player data folder exists, if not create it
        SavePlayerDataUtil.checkFolder(getDataFolder());
    }

    @Override
    public void onDisable() {
        for(FarkleInstance instance : FarkleManager.getallFarkleInstances()) {
            instance.remove();
        }
//        PlayerManager.saveAll();
    }

    // Register events such as DiceEvent
    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new JoinLeaveEvent(), this);
        getServer().getPluginManager().registerEvents(new DiceEvent(), this);
        getServer().getPluginManager().registerEvents(new FarkleInteractionEvent(), this);
        getServer().getPluginManager().registerEvents(new MiningOreEvent(), this);
    }
}
