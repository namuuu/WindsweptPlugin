package fr.namu.windswept.util;

import fr.namu.windswept.enums.Job;
import fr.namu.windswept.enums.Quest;
import fr.namu.windswept.enums.QuestAdvancementType;
import fr.namu.windswept.instance.PlayerInstance;
import fr.namu.windswept.manager.PlayerManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuestUtil {
    public static List<Quest> getAvailableQuests(PlayerInstance playerInstance) {
        List<Quest> availableQuests = new ArrayList<>();
        Arrays.stream(Quest.values()).toList().forEach(quest -> {
            if(quest.getRequiredLevel() < playerInstance.getJobLevel(quest.getJob()))
                return;

            availableQuests.add(quest);
        });

        return availableQuests;
    }

    public static List<Quest> getAvailableQuestsForType(PlayerInstance playerInstance, QuestAdvancementType advancementType) {
        List<Quest> availableQuests = new ArrayList<>();
        Arrays.stream(Quest.values()).toList().forEach(quest -> {
            if(quest.getRequiredLevel() < playerInstance.getJobLevel(quest.getJob()))
                return;

            if(quest.getAdvancementType() == advancementType)
                availableQuests.add(quest);
        });

        return availableQuests;
    }

    public static void reward(Player player, Quest quest) {
        PlayerInstance playerInstance = PlayerManager.get(player.getUniqueId());
        switch (quest.getRewardType()) {
            case JOB_MASTERY_POINT:
                playerInstance.setMasteryPoints(playerInstance.getJobMasteryPoints() + 1);
                player.sendMessage("You have received 1 mastery point for completing the quest: " + quest.getQuestName());
                break;
        }
    }
}
