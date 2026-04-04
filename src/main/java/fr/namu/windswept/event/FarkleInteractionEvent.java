package fr.namu.windswept.event;

import fr.namu.windswept.instance.FarkleInstance;
import fr.namu.windswept.manager.FarkleManager;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;


public class FarkleInteractionEvent implements Listener {

    @EventHandler
    public void onFeatherEvent(PlayerInteractEntityEvent event) {
        Entity clicked = event.getRightClicked();

        FarkleInstance instance = getInstanceFromEntity(clicked, "farkle_feather_game_id");

        if(instance == null)
            return;

        instance.processFeatherInteraction(event.getPlayer());
    }

    @EventHandler
    public void onSackEvent(PlayerInteractEntityEvent event) {
        Entity clicked = event.getRightClicked();

        FarkleInstance instance = getInstanceFromEntity(clicked, "farkle_sack_game_id");

        if(instance == null)
            return;

        instance.processSackInteraction(event.getPlayer());
    }

    private FarkleInstance getInstanceFromEntity(Entity entity, String stringKey) {
        NamespacedKey key = new NamespacedKey("windswept", stringKey);

        if(!entity.getPersistentDataContainer().has(key, PersistentDataType.STRING))
            return null;

        String gameId = entity.getPersistentDataContainer().get(key, PersistentDataType.STRING);

        if(gameId == null)
            return null;

        UUID uuid = UUID.fromString(gameId);

        return FarkleManager.getFarkleInstanceByUUID(uuid);
    }
}
