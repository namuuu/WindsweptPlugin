package fr.namu.windswept.event;

import fr.namu.windswept.Main;
import fr.namu.windswept.instance.FarkleInstance;
import fr.namu.windswept.manager.FarkleManager;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class DiceEvent implements Listener {

    @EventHandler
    public void onDiceInteract(PlayerInteractEntityEvent event) {
        Entity clicked = event.getRightClicked();

        if(!(clicked instanceof Interaction interaction))
            return;

        NamespacedKey key = new NamespacedKey(Main.getInstance(), "farkle_game_id");
        NamespacedKey diceKey = new NamespacedKey(Main.getInstance(), "farkle_dice_id");

        if (!interaction.getPersistentDataContainer().has(key, PersistentDataType.STRING))
            return;
        if (!interaction.getPersistentDataContainer().has(diceKey, PersistentDataType.INTEGER))
                return;

        PersistentDataContainer container = interaction.getPersistentDataContainer();

        String gameId = container.get(key, PersistentDataType.STRING);
        Integer diceId = container.get(diceKey, PersistentDataType.INTEGER);

        if(gameId == null || diceId == null)
            return;

        UUID uuid = UUID.fromString(gameId);

        FarkleInstance instance = FarkleManager.getFarkleInstanceByUUID(uuid);

        if(instance == null)
            return;

        Player player = event.getPlayer();

        instance.processInteraction(player, diceId);
    }
}
