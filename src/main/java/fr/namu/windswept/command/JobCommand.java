package fr.namu.windswept.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import fr.namu.windswept.enums.Job;
import fr.namu.windswept.instance.PlayerInstance;
import fr.namu.windswept.manager.PlayerManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class JobCommand {
    public LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("job")
                .executes(ctx -> {
                    ctx.getSource().getSender().sendMessage("Job command executed!");
                    return 1;
                })
                .then(Commands.literal("list")
                        .executes(ctx -> {
                            Player player = (Player) ctx.getSource().getSender();
                            PlayerInstance playerInstance = PlayerManager.get(player.getUniqueId());
                            Arrays.stream(Job.values()).toList().forEach(job -> player.sendMessage(job.getName() + " Level: " + playerInstance.getJobLevel(job)));
                            return 1;
                        }))
                .build();
    }
}
