package fr.namu.windswept;

import fr.namu.windswept.event.DiceEvent;
import fr.namu.windswept.event.FarkleInteractionEvent;
import fr.namu.windswept.instance.FarkleInstance;
import fr.namu.windswept.manager.CommandManager;
import fr.namu.windswept.manager.FarkleManager;
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
    }

    @Override
    public void onDisable() {
        for(FarkleInstance instance : FarkleManager.getallFarkleInstances()) {
            instance.remove();
        }
    }

    // Register events such as DiceEvent
    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new DiceEvent(), this);
        getServer().getPluginManager().registerEvents(new FarkleInteractionEvent(), this);
    }
}
