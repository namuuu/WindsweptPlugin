package fr.namu.windswept.event;

import fr.namu.windswept.Main;
import fr.namu.windswept.instance.PlayerInstance;
import fr.namu.windswept.manager.PlayerManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinLeaveEvent implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerInstance instance = PlayerManager.load(player);

        // Replace join message
        event.joinMessage(Component.text("§a+ §7» §e" + player.getName()));
        Main.getInstance().getLogger().info(instance.toString());
    }

    @EventHandler
    public void onPlayerLeave(org.bukkit.event.player.PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerInstance instance = PlayerManager.get(player.getUniqueId());
        if (instance != null) {
            instance.save();
            PlayerManager.remove(player.getUniqueId());
        }

        // Replace quit message
        event.quitMessage(Component.text("§c- §7» §e" + player.getName()));
    }
}
