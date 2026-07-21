package fr.namu.windswept.util;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.entity.Player;

public class CommandUtil {

    public static Player getPlayer(CommandContext<CommandSourceStack> ctx) {
        if(!(ctx.getSource().getSender() instanceof Player)) {
            ctx.getSource().getSender().sendMessage("You must be a player to use this command.");
            return null;
        }
        return (Player) ctx.getSource().getSender();
    }

    public static Player getTarget(CommandContext<CommandSourceStack> ctx, String argumentName) throws CommandSyntaxException {
        final PlayerSelectorArgumentResolver playerSelector = ctx.getArgument(argumentName, PlayerSelectorArgumentResolver.class);
        final Player player =  playerSelector.resolve(ctx.getSource()).getFirst();

        if(player == null) {
            ctx.getSource().getSender().sendMessage("No player found with that name.");
            return null;
        }

        return player;
    }
}
