package fr.namu.windswept.util.farkle;

import fr.namu.windswept.instance.FarkleInstance;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class FarkleTextUtil {

    private final static String PREFIX = "§l§5Farkle §r§6»";
    private final static String FARLKED_TEXT = "§l§cFARLKED!";
    private final static String CLEAR_TEXT = "§l§aCLEAR! §r§a Tous les dés sont à nouveau disponibles.";

    public static void broadcastMessage(FarkleInstance instance, String message) {
        Player player1 = instance.getPlayer(1);
        Player player2 = instance.getPlayer(2);

        if (player1 != null)
            player1.sendMessage(PREFIX + " §e" + message);
        if (player2 != null)
            player2.sendMessage(PREFIX + " §e" + message);
    }

    public static void sendInfo(Player player, String message) {
        player.sendMessage(PREFIX + " §e" + message);
    }

    public static void sendWarning(Player player, String message) {
        player.sendMessage(PREFIX + " §c" + message);
    }

    // Message to send when a player farkles
    public static void sendFarlked(FarkleInstance instance) {
        Player player1 = instance.getPlayer(1);
        Player player2 = instance.getPlayer(2);

        if (player1 != null) {
            player1.sendMessage(FARLKED_TEXT);
            player1.playSound(instance.getTableLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1.0f, 1.0f);
        }
        if (player2 != null) {
            player2.sendMessage(FARLKED_TEXT);
            player2.playSound(instance.getTableLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1.0f, 1.0f);
        }
    }

    public static void sendClear(FarkleInstance instance) {
        Player player1 = instance.getPlayer(1);
        Player player2 = instance.getPlayer(2);

        if (player1 != null) {
            player1.sendMessage(CLEAR_TEXT);
            player1.playSound(instance.getTableLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }

        if (player2 != null) {
            player2.sendMessage(CLEAR_TEXT);
            player2.playSound(instance.getTableLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }
    }

    public static void announceWinner(FarkleInstance instance, int winner, int score) {
        Player player1 = instance.getPlayer(1);
        Player player2 = instance.getPlayer(2);

        instance.getTableLocation().getWorld().playSound(instance.getTableLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);

        String winnerName = winner == 1 ? player1.getName() : player2.getName();
        if(instance.isNPCMatch() && winner == 2)
            winnerName = "NPC";
        String message = PREFIX + " §c" + winnerName + " §r§6remporte la partie avec §5" + score + " points§e!";

        if (player1 != null)
            player1.sendMessage(message);
        if (player2 != null)
            player2.sendMessage(message);
    }

    public static void sendScoreTitle(FarkleInstance instance, int currentDice, int currentRoundScore) {
        Player player1 = instance.getPlayer(1);
        Player player2 = instance.getPlayer(2);

        String title = "Farkle | ";

        if(currentRoundScore > 0) {
            title += "Tour : " + currentDice + " (" + currentRoundScore + ") | " + player1.getName() + " : " + instance.getPlayerScore(1) + " | " + (player2 != null ? player2.getName() : "NPC") + " : " + instance.getPlayerScore(2);
        } else {
            title += "Tour : " + currentDice + " | " + player1.getName() + " : " + instance.getPlayerScore(1) + " | " + (player2 != null ? player2.getName() : "NPC") + " : " + instance.getPlayerScore(2);
        }

        player1.sendActionBar(Component.text(title).color(NamedTextColor.GOLD));
        if(player2 != null)
            player2.sendActionBar(Component.text(title).color(NamedTextColor.GOLD));
    }
}
