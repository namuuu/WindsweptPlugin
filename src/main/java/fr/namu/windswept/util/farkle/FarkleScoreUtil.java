package fr.namu.windswept.util.farkle;

import fr.namu.windswept.instance.DiceInstance;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class FarkleScoreUtil {

    public static int get(ArrayList<DiceInstance> roundDiceList, Player player) {
        int score = FarkleScoreUtil.calculateScore(roundDiceList);
        boolean isValid = FarkleScoreUtil.validSelectedDice(roundDiceList);

        if(score == 0) {
            FarkleTextUtil.sendWarning(player, "Aucun de tes dés ne rapportent de points!");
            return 0;
        }
        if(!isValid) {
            FarkleTextUtil.sendWarning(player, "Certains de tes dés sélectionnés ne rapportent pas de points...");
            return 0;
        }

        return score;
    }

    public static int calculateScore(ArrayList<DiceInstance> diceValues) {
        int score = 0;
        int[] counts = new int[7]; // Index 0 unused, dice values are 1-6

        for (DiceInstance dice : diceValues) {
            counts[dice.getValue()]++;
        }

        // Check for six of a kind, five of a kind, four of a kind, and three of a kind
        for (int i = 1; i <= 6; i++) {
            if(counts[i] == 6) {
                score += 3000; // Six of a kind
                continue;
            }
            if (counts[i] == 5) {
                score += 2000; // Five of a kind
                continue;
            }
            if (counts[i] == 4) {
                score += 1000; // Four of a kind
                continue;
            }
            if (counts[i] >= 3) {
                if (i == 1) {
                    score += 1000; // Three ones
                } else {
                    score += i * 100; // Three of a kind for other numbers
                }
            }
        }

        // If 1 or 5s scored points from three of a kind, remove those from the counts to avoid double counting
        if(counts[1] >= 3) {
            counts[1] = 0; // Remove the three ones that were scored
        }

        if(counts[5] >= 3) {
            counts[5] = 0; // Remove the three fives that were scored
        }

        // Check for straight (1-6)
        if (isStraight(counts)) {
            return 1500; // Straight
        }

        // Check for partial straight (1-5)
        if (isPartialStraight(counts)) {
            score += 750; // Partial straight
            if(counts[1] != 0)
                counts[1] -= 1;
            if(counts[5] != 0)
                counts[5] -= 1;
        }

        // Add points for remaining ones and fives
        score += counts[1] * 100; // Each remaining one is worth 100 points
        score += counts[5] * 50;  // Each remaining five is worth 50 points

        return score;
    }

    public static boolean validSelectedDice(ArrayList<DiceInstance> selectedDice) {
        if(calculateScore(selectedDice) == 0)
            return false;

        int[] counts = new int[7]; // Index 0 unused, dice values are 1-6

        for (DiceInstance dice : selectedDice) {
            counts[dice.getValue()]++;
        }

        // Check for straight (1-6)
        if (isStraight(counts))
            return true;

        // Check for partial straight (1-5)
        if (isPartialStraight(counts))
            return true;

        // If the player put 2s in the selection but they do not score any points, invalid
        if(isInvalidCount(counts[2]))
            return false;

        // If the player put 3s in the selection but they do not score any points, invalid
        if(isInvalidCount(counts[3]))
            return false;

        // If the player put 4s in the selection but they do not score any points, invalid
        if(isInvalidCount(counts[4]))
            return false;

        // If the player put 6s in the selection but they do not score any points, invalid
        return !isInvalidCount(counts[6]);
    }

    private static boolean isInvalidCount(int count) {
        return count < 3 && count != 0;
    }

    private static boolean isStraight(int[] counts) {
        return counts[1] == 1 && counts[2] == 1 && counts[3] == 1 && counts[4] == 1 && counts[5] == 1 && counts[6] == 1;
    }

    private static boolean isPartialStraight(int[] counts) {
        return (counts[1] == 1 && counts[2] == 1 && counts[3] == 1 && counts[4] == 1 && counts[5] == 1) ||
                (counts[2] == 1 && counts[3] == 1 && counts[4] == 1 && counts[5] == 1 && counts[6] == 1);
    }
}
