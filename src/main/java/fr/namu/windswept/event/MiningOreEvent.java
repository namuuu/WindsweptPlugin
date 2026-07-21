package fr.namu.windswept.event;

import fr.namu.windswept.enums.QuestAdvancementType;
import fr.namu.windswept.instance.PlayerInstance;
import fr.namu.windswept.manager.PlayerManager;
import fr.namu.windswept.util.QuestUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class MiningOreEvent implements Listener {

    @EventHandler
    public void onOreMining(BlockBreakEvent event) {
        if (!event.getBlock().getType().toString().endsWith("_ORE")) {
            return;
        }

        Player player = event.getPlayer();
        PlayerInstance instance = PlayerManager.get(player.getUniqueId());

        switch (event.getBlock().getType()) {
            case COAL_ORE:
                QuestUtil.getAvailableQuestsForType(instance, QuestAdvancementType.BREAKING_COAL_ORE).forEach(quest -> {
                    instance.progressQuest(quest, 1);
                });
                break;
        }
    }
}
