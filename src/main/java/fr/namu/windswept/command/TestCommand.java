package fr.namu.windswept.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import fr.namu.windswept.enums.Job;
import fr.namu.windswept.instance.PlayerInstance;
import fr.namu.windswept.manager.PlayerManager;
import fr.namu.windswept.util.SavePlayerDataUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public class TestCommand {

    public LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("test")
                .executes(ctx -> {
                    ctx.getSource().getSender().sendMessage("Test command executed!");
                    Player player = (Player) ctx.getSource().getSender();

                    PlayerInstance playerInstance = PlayerManager.load(player);
                    playerInstance.setJobLevel(Job.FARMER, 1);
                    playerInstance.save();

                    return 1;
                })
                .build();
    }
}
