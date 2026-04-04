package fr.namu.windswept.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import fr.namu.windswept.instance.FarkleInstance;
import fr.namu.windswept.manager.FarkleManager;
import fr.namu.windswept.util.farkle.FarkleTextUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.entity.Player;

public class FarkleCommand {

    public LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("farkle")
                .then(Commands.argument("player", ArgumentTypes.player())
                    .executes(ctx -> {
                       Player player1 = ctx.getSource().getSender() instanceof Player ? (Player) ctx.getSource().getSender() : null;

                       if(player1 == null) {
                           ctx.getSource().getSender().sendMessage("You must be a player to use this command.");
                           return 0;
                       }

                       final PlayerSelectorArgumentResolver playerSelector = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
                       final Player player2 = playerSelector.resolve(ctx.getSource()).getFirst();

                      if(player2 == null) {
                        FarkleTextUtil.sendInfo(player1, "Aucun joueur trouvé avec ce nom.");
                        return 0;
                      }

                      if(player1.equals(player2)) {
                        FarkleTextUtil.sendInfo(player1, "Si tu souhaites jouer contre toi-même, tu peux faire /farkle solo.");
                        return 0;
                      }

                      FarkleManager.addPlayRequest(player1, player2);

                      FarkleTextUtil.sendInfo(player1, "Demande de jeu envoyée à " + player2.getName() + ".");
                      FarkleTextUtil.sendInfo(player2, "Tu as reçu une demande de jeu de " + player1.getName() + ". Tape /farkle accept " + player1.getName() + " pour accepter la demande.");

                       return 1;
                    }))
                .then(Commands.literal("solo")
                    .executes(ctx -> {
                        Player player = ctx.getSource().getSender() instanceof Player ? (Player) ctx.getSource().getSender() : null;

                        if(player == null) {
                            ctx.getSource().getSender().sendMessage("You must be a player to use this command.");
                            return 0;
                        }

                        new FarkleInstance(player);
                        return 1;
                    }))
                .then(Commands.literal("accept")
                        .then(Commands.argument("sender", ArgumentTypes.player())
                                .executes(ctx -> {
                                    Player receiver = ctx.getSource().getSender() instanceof Player ? (Player) ctx.getSource().getSender() : null;

                                    if(receiver == null) {
                                        ctx.getSource().getSender().sendMessage("You must be a player to use this command.");
                                        return 0;
                                    }

                                    final PlayerSelectorArgumentResolver playerSelector = ctx.getArgument("sender", PlayerSelectorArgumentResolver.class);
                                    final Player sender = playerSelector.resolve(ctx.getSource()).getFirst();

                                    if(!FarkleManager.hasPlayRequest(sender, receiver)) {
                                        FarkleTextUtil.sendInfo(receiver, "Tu n'as pas reçu de demande de jeu de ce joueur.");
                                        return 0;
                                    }

                                    if(sender.getLocation().distance(receiver.getLocation()) > 10) {
                                        FarkleTextUtil.sendWarning(receiver, "Le joueur est trop loin pour accepter la demande. (max: 10 blocs)");
                                        return 0;
                                    }

                                    new FarkleInstance(sender, receiver);

                                    // Remove the previous play request
                                    FarkleManager.removePlayRequest(sender, receiver);

                                    return 1;
                                }
                            )
                        )
                )
                .then(Commands.literal("forfeit")
                        .executes(ctx -> {
                            Player player = ctx.getSource().getSender() instanceof Player ? (Player) ctx.getSource().getSender() : null;

                            if(player == null) {
                                ctx.getSource().getSender().sendMessage("You must be a player to use this command.");
                                return 0;
                            }

                            FarkleInstance instance = FarkleManager.getFarkleInstanceByPlayer(player);

                            if(instance == null) {
                                FarkleTextUtil.sendInfo(player, "Tu n'es pas dans une partie.");
                                return 0;
                            }

                            instance.remove();
                            return 1;
                        })).build();
    }
}
