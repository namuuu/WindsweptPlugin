package fr.namu.windswept.enums;

public enum Quest {
    COAL_MINING("Miner du charbon", 0, Job.MINER, QuestAdvancementType.BREAKING_COAL_ORE, 10, QuestRewardType.JOB_MASTERY_POINT);
    ;

    private String questName;

    private int requiredLevel;

    private Job job;

    private QuestAdvancementType advancementType;

    private int completion;

    private QuestRewardType reward;

    Quest(String questName, int requiredLevel, Job job, QuestAdvancementType advancementType, int completion, QuestRewardType reward) {
        this.questName = questName;
        this.requiredLevel = requiredLevel;
        this.job = job;
        this.advancementType = advancementType;
        this.completion = completion;
        this.reward = reward;
    }

    public String getQuestName() {
        return questName;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public Job getJob() {
        return job;
    }

    public QuestAdvancementType getAdvancementType() {
        return advancementType;
    }

    public int getCompletion() {
        return completion;
    }

    public QuestRewardType getRewardType() {
        return reward;
    }
}
