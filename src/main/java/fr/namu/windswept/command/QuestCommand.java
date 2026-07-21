package fr.namu.windswept.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import fr.namu.windswept.instance.PlayerInstance;
import fr.namu.windswept.manager.PlayerManager;
import fr.namu.windswept.util.QuestUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public class QuestCommand {
    public LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("quest")
                .executes(ctx -> {
                    ctx.getSource().getSender().sendMessage("Quest command executed!");
                    return 1;
                })
                .then(Commands.literal("list")
                        .executes(ctx -> {
                            Player player = (Player) ctx.getSource().getSender();
                            PlayerInstance playerInstance = PlayerManager.get(player.getUniqueId());
                            QuestUtil.getAvailableQuests(playerInstance).forEach(quest -> {
                                player.sendMessage(quest.getQuestName() + " Avancement: " + playerInstance.getQuestProgress(quest) + "/" + quest.getCompletion());
                            });
                            return 1;
                        }))
                .build();
    }
}
