package fr.namu.windswept.instance;

import fr.namu.windswept.Main;
import fr.namu.windswept.enums.Direction;
import fr.namu.windswept.manager.FarkleManager;
import fr.namu.windswept.util.farkle.FarklePlacementUtil;
import fr.namu.windswept.util.farkle.FarkleScoreUtil;
import fr.namu.windswept.util.farkle.FarkleTextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class FarkleInstance {
    private final UUID uuid;
    private final boolean isNPCMatch;

    // Information about the table and the dices in the world
    private Location tableLocation;
    private BlockDisplay tableEntity;
    public HashMap<Integer, DiceInstance> dices = new HashMap<>();

    private ItemDisplay featherEntity;
    private Interaction featherInteraction;

    private ItemDisplay sackEntity;
    private Interaction sackInteraction;

    private BukkitTask actionBarTask;

    // Player 1 is always the one who initiated the match, and player 2 is the opponent (or the NPC if it's an NPC match)
    private Player player1;
    private int player1Score = 0;
    private Player player2;
    private int player2Score = 0;

    // Turn information
    private int currentPlayerTurn = 1; // 1 for player 1, 2 for player 2
    private int currentScore = 0; // The score that the current player has accumulated during their turn
    private boolean lockAnimation = false;
    private final ArrayList<DiceInstance> selectedDiceList = new ArrayList<>(); // The dice that the current player has selected to keep for the current turn
    private final ArrayList<DiceInstance> scoredDiceList = new ArrayList<>(); // The dice that the current player has scored with during their turn

    public FarkleInstance(Player player) {
        this.uuid = UUID.randomUUID();
        this.isNPCMatch = true;
        Location loc2 = FarklePlacementUtil.getNPCLocation(player);

        if(loc2 == null) {
            FarkleTextUtil.sendWarning(player, "Il n'y a pas assez de place pour placer le NPC.");
            return;
        }

        this.tableLocation = FarklePlacementUtil.getTableLocation(player.getLocation(), loc2);
        if(this.tableLocation == null) {
            FarkleTextUtil.sendWarning(player, "Il n'y a pas assez de place pour placer la table.");
            return;
        }

        spawnEntities();

        this.player1 = player;

        FarkleManager.addFarkleInstance(this.uuid, this);

        this.actionBarTask = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> FarkleTextUtil.sendScoreTitle(this, FarkleScoreUtil.calculateScore(getRoundDice()), currentScore), 0, 20);
    }

    public FarkleInstance(Player player1, Player player2) {
        this.uuid = UUID.randomUUID();
        this.isNPCMatch = false;

        this.tableLocation = FarklePlacementUtil.getTableLocation(player1.getLocation(), player2.getLocation());
        if(this.tableLocation == null) {
            FarkleTextUtil.sendWarning(player1, "Il n'y a pas assez de place pour placer la table.");
            FarkleTextUtil.sendWarning(player2, "Il n'y a pas assez de place pour placer la table.");
            return;
        }

        spawnEntities();

        this.player1 = player1;
        this.player2 = player2;

        FarkleManager.addFarkleInstance(this.uuid, this);

        this.actionBarTask = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> FarkleTextUtil.sendScoreTitle(this, FarkleScoreUtil.calculateScore(getRoundDice()), currentScore), 0, 20);
    }

    /**
     * Spawns the entities for the instance. It should be called after the instance is created and the players are set.
     */
    private void spawnEntities() {
        this.tableEntity = FarklePlacementUtil.spawnTable(this.tableLocation);
        this.dices = FarklePlacementUtil.spawnDice(this.uuid, this.tableLocation);
        this.featherEntity = FarklePlacementUtil.spawnFeather(tableLocation);
        this.featherInteraction = FarklePlacementUtil.spawnFeatherInteraction(uuid, tableLocation);
        this.sackEntity = FarklePlacementUtil.spawnSack(tableLocation);
        this.sackInteraction = FarklePlacementUtil.spawnSackInteraction(uuid, tableLocation);
    }

    public int isPlayerInInstance(Player player) {
        if(player.equals(player1)) return 1;
        if(player.equals(player2)) return 2;
        return 0;
    }

    public Player getPlayer(int playerNumber) {
        return playerNumber == 1 ? player1 : player2;
    }

    public int getPlayerScore(int playerNumber) {
        return playerNumber == 1 ? player1Score : player2Score;
    }

    public boolean isNPCMatch() {
        return this.isNPCMatch;
    }

    public void processInteraction(Player player, int diceId) {
        if(lockAnimation) return;

        int playerNumber = isPlayerInInstance(player);
        //TODO: If the player is not in the game, display the score
        if(playerNumber == 0) return; // Player is not in the instance

        if(currentPlayerTurn != playerNumber) {
            FarkleTextUtil.sendWarning(player, "Ce n'est pas ton tour!");
            return;
        }

        DiceInstance dice = dices.get(diceId);


        if(scoredDiceList.contains(dice))
            return;

        player.playSound(tableLocation, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);

        if(selectedDiceList.contains(dice)) {
            selectedDiceList.remove(dice);
            dice.moveUnselected();
        } else {
            selectedDiceList.add(dice);
            if(playerNumber == 1)
                dice.moveSelected(this.tableLocation, Direction.NORTH);
            else
                dice.moveSelected(this.tableLocation, Direction.SOUTH);
        }
    }

    public void processFeatherInteraction(Player player) {
        if(lockAnimation) return;

        int playerNumber = isPlayerInInstance(player);

        if(playerNumber == 0) return; // Player is not in the instance

        if(currentPlayerTurn != playerNumber) {
            FarkleTextUtil.sendWarning(player, "Ce n'est pas ton tour!");
            return;
        }

        ArrayList<DiceInstance> roundDiceList = getRoundDice();

        int score = FarkleScoreUtil.get(roundDiceList, player);

        // If the score is 0, the player has been warned, return without doing anything
        if(score == 0)
            return;

        currentScore += score;
        addScore(currentPlayerTurn, currentScore);

        FarkleTextUtil.broadcastMessage(this,  player.getName() + ": " + getScore(playerNumber) + " points (+" + currentScore + ") ");
        player.playSound(tableLocation, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

        lockAnimation = true;

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), this::goNextTurn, 20);
    }

    public void processSackInteraction(Player player) {
        if(lockAnimation) return;

        int playerNumber = isPlayerInInstance(player);

        if(playerNumber == 0) return; // Player is not in the instance

        if(currentPlayerTurn != playerNumber) {
            FarkleTextUtil.sendWarning(player, "Ce n'est pas ton tour!");
            return;
        }

        ArrayList<DiceInstance> roundDiceList = getRoundDice();

        int score = FarkleScoreUtil.get(roundDiceList, player);

        // If the score is 0, the player has been warned, return without doing anything
        if(score == 0)
            return;

        // Make every selectedDice not scorable again
        scoredDiceList.addAll(roundDiceList);

        // Get the locations for the number of unselected dice. If all the dice are selected, the player rerolls all the dice, so we consider that there are 6 unselected dice in this case.
        List<Location> diceLocation = FarklePlacementUtil.getDiceLocations(tableLocation, selectedDiceList.size() == 6 ? 6 : 6 - selectedDiceList.size());

        // When the player full clears, reroll every dice
        if(selectedDiceList.size() == 6) {
            player.sendMessage("You have selected all the dice, rerolling all dice");
            scoredDiceList.clear();
            selectedDiceList.clear();
        }

        for(DiceInstance dice: dices.values()) {
            if(!selectedDiceList.contains(dice)) {
                dice.reroll(diceLocation.removeFirst());
            } else {
                dice.setLocked();
            }
        }

        currentScore += score;

        FarkleTextUtil.broadcastMessage(this, "§7Tour de " + player.getName() + ": " + currentScore + " points (+" + score + ") ");

        checkFarkle();
    }

    private void addScore(int playerNumber, int scoreToAdd) {
        if(playerNumber == 1) {
            player1Score += scoreToAdd;
        } else {
            player2Score += scoreToAdd;
        }
    }

    private int getScore(int playerNumber) {
        return playerNumber == 1 ? player1Score : player2Score;
    }

    public Location getTableLocation() {
        return this.tableLocation;
    }

    private ArrayList<DiceInstance> getUnselectedDice() {
        ArrayList<DiceInstance> unselectedDice = new ArrayList<>();
        for(DiceInstance dice: dices.values()) {
            if(!selectedDiceList.contains(dice))
                unselectedDice.add(dice);
        }
        return unselectedDice;
    }

    private ArrayList<DiceInstance> getRoundDice() {
        ArrayList<DiceInstance> roundDice = new ArrayList<>();
        for(DiceInstance dice: selectedDiceList) {
            if(!scoredDiceList.contains(dice))
                roundDice.add(dice);
        }
        return roundDice;
    }

    private boolean canScore() {
        ArrayList<DiceInstance> unselectedDice = getUnselectedDice();
        return FarkleScoreUtil.calculateScore(unselectedDice) != 0;
    }

    // If there's no scoring possible, skip the player's turn after making a small animation
    private void checkFarkle() {
        if(canScore())
            return;

        // Make all dice glow red
        for(DiceInstance instance: dices.values())
            instance.setFarkleState();

        FarkleTextUtil.sendFarlked(this);
        lockAnimation = true;

        // Schedule the next turn
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), this::goNextTurn, 20);
    }

    private void goNextTurn() {
        int WINNING_SCORE = 3000;
        if(getScore(currentPlayerTurn) >= WINNING_SCORE) {
            win();
            return;
        }
        if(!isNPCMatch)
            currentPlayerTurn = currentPlayerTurn == 1 ? 2 : 1;
        currentScore = 0;
        selectedDiceList.clear();
        scoredDiceList.clear();
        lockAnimation = false;

        Player currentPlayer = getPlayer(currentPlayerTurn);
        currentPlayer.playSound(currentPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
        FarkleTextUtil.broadcastMessage(this, "§5Tour de " + currentPlayer.getName() + ": " + getScore(currentPlayerTurn) + " points");

        List<Location> diceLocations = FarklePlacementUtil.getDiceLocations(tableLocation, 6);

        for(int i = 0; i < 6; i++) {
            dices.get(i).reroll(diceLocations.get(i));
        }

        FarkleTextUtil.sendScoreTitle(this, FarkleScoreUtil.calculateScore(getRoundDice()), currentScore);

        checkFarkle();
    }

    private void win() {
        FarkleTextUtil.announceWinner(this, currentPlayerTurn, getScore(currentPlayerTurn));

        this.remove();
    }

    public void remove() {
        tableLocation.add(0, 1, 0).getBlock().setType(Material.AIR);
        tableEntity.remove();
        for(DiceInstance dice: dices.values()) {
            dice.remove();
        }
        featherEntity.remove();
        featherInteraction.remove();
        sackEntity.remove();
        sackInteraction.remove();
        this.actionBarTask.cancel();
    }
}
