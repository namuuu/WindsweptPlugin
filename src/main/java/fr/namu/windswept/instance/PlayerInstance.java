package fr.namu.windswept.instance;

import com.google.gson.annotations.Expose;
import fr.namu.windswept.enums.CombatClass;
import fr.namu.windswept.enums.Job;
import fr.namu.windswept.enums.Quest;
import fr.namu.windswept.util.QuestUtil;
import fr.namu.windswept.util.SavePlayerDataUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class PlayerInstance {
    private final Player player;

    @Expose
    private final UUID uuid;

    @Expose
    private CombatClass combatClass;

    @Expose
    private HashMap<Job, Integer> jobLevels = new HashMap<>();

    @Expose
    private HashMap<Quest, Integer> questProgress = new HashMap<>();

    @Expose
    private int jobMasteryPoints = 0;

    public PlayerInstance(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();
    }

    public PlayerInstance(PlayerInstance playerInstance) {
        this.player = playerInstance.player;
        this.combatClass = playerInstance.combatClass;
        this.jobLevels = new HashMap<>(playerInstance.jobLevels);
        this.questProgress = new HashMap<>(playerInstance.questProgress);
        this.jobMasteryPoints = playerInstance.jobMasteryPoints;
        this.uuid = playerInstance.uuid;
    }

    public Player getPlayer() {
        if(player == null && uuid != null) {
            // Attempt to retrieve the player from the server using the UUID
            Player onlinePlayer = Bukkit.getPlayer(uuid);
            if (onlinePlayer != null) {
                return onlinePlayer;
            }
        }
        return player;
    }

    public UUID getUuid() {
        return uuid;
    }

    public CombatClass getCombatClass() {
        return combatClass;
    }

    public HashMap<Job, Integer> getJobs() {
        return jobLevels;
    }

    public int getJobLevel(Job job) {
        return this.jobLevels.getOrDefault(job, 0);
    }

    public void setJobLevel(Job job, int level) {
        this.jobLevels.put(job, level);
    }

    public HashMap<Quest, Integer> getQuestProgressMap() {
        return questProgress;
    }

    public int getQuestProgress(Quest quest) {
        return this.questProgress.getOrDefault(quest, 0);
    }

    public void setQuestProgress(Quest quest, int progress) {
        this.questProgress.put(quest, progress);
    }

    public void progressQuest(Quest quest, int amount) {
        int currentProgress = getQuestProgress(quest);

        if(currentProgress >= quest.getCompletion()) {
            return;
        }

        setQuestProgress(quest, currentProgress + amount);


        getPlayer().sendMessage(currentProgress + amount + "/" + quest.getCompletion());
        if(currentProgress + amount == quest.getCompletion()) {
            QuestUtil.reward(getPlayer(), quest);
        }

        this.save();
    }

    public int getJobMasteryPoints() {
        return this.jobMasteryPoints;
    }

    public void setMasteryPoints(int masteryPoints) {
        this.jobMasteryPoints = masteryPoints;
    }

    public void save() {
        try {
            SavePlayerDataUtil.save(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "PlayerInstance{" +
                "player=" + getPlayer().getName() +
                ", combatClass=" + combatClass +
                ", jobLevels=" + jobLevels +
                ", questProgress=" + questProgress +
                ", jobMasteryPoints=" + jobMasteryPoints +
                '}';
    }
}
