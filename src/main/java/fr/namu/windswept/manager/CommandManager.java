package fr.namu.windswept.manager;

import fr.namu.windswept.command.FarkleCommand;
import fr.namu.windswept.command.JobCommand;
import fr.namu.windswept.command.QuestCommand;
import fr.namu.windswept.command.TestCommand;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandManager {

    private final JavaPlugin plugin;

    public CommandManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerCommands() {
        plugin.getLifecycleManager().registerEventHandler(
                LifecycleEvents.COMMANDS,
                commands -> {
                    commands.registrar().register(new FarkleCommand().build());
                    commands.registrar().register((new TestCommand().build()));
                    commands.registrar().register((new JobCommand().build()));
                    commands.registrar().register((new QuestCommand().build()));
                }
        );
    }
}
