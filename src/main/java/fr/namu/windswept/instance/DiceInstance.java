package fr.namu.windswept.instance;

import fr.namu.windswept.Main;
import fr.namu.windswept.enums.DiceValue;
import fr.namu.windswept.enums.Direction;
import fr.namu.windswept.util.farkle.FarklePlacementUtil;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Interaction;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;

import java.util.UUID;

public class DiceInstance {
    private final int id;
    private int value; // The value of the dice (1-6)

    private BlockDisplay blockDisplay;

    private Interaction interaction;

    private Location randomLocation; // The location where the dice will be thrown when rolled
    private final float randomRotation; // The rotation of the dice when rolled
    private final Location location; // The current location of the dice

    private final float SIZE = 0.08f;

    public DiceInstance(UUID uuid, int id, Location location) {
        this.id = id;
        this.value = (int) (Math.random() * 6) + 1;
        this.location = location;
        this.randomLocation = location;
//        this.randomRotation = (float) (Math.random() * 360);
        this.randomRotation = 0;

        spawnDisplay();
        spawnInteraction(uuid);
    }

    public BlockDisplay getBlockDisplay() {
        return blockDisplay;
    }

    public Interaction getInteraction() {
        return interaction;
    }

    /**
     * Spawns the block display for the dice at the specified location.
     */
    private void spawnDisplay() {
//        Location updatedLocation = location.clone().add(-0.5 * SIZE, 0, -0.5 * SIZE);
        BlockDisplay display = location.getWorld().spawn(location, BlockDisplay.class);
        display.setBlock(Bukkit.createBlockData("minecraft:note_block[note=0,instrument=harp,powered=false]"));
        display.setBrightness(new Display.Brightness(15, 15));
        display.setGravity(false);
        display.setGlowColorOverride(Color.LIME);
        Transformation transformation = display.getTransformation();

        transformation.getTranslation().set(DiceValue.applyPositionFix(value, SIZE));

        // The rotation is set to the value of the dice, so the display shows the correct face of the dice
        transformation.getLeftRotation().set(DiceValue.getRotation(value));

        // The scale is set to the size of the dice, so the display is the size of a dice
        transformation.getScale().set(SIZE, SIZE, SIZE);

        display.setTransformation(transformation);

        this.blockDisplay = display;
    }

    /**
     * Spawns the interaction for the dice at the specified location.
     */
    private void spawnInteraction(UUID uuid) {
        Location newLoc = location.clone().add(0, -0.5f * SIZE, 0);
        Interaction interaction = location.getWorld().spawn(newLoc, Interaction.class);
        interaction.setInteractionWidth(SIZE);
        interaction.setInteractionHeight(SIZE);

        NamespacedKey key = new NamespacedKey(Main.getInstance(), "farkle_game_id");
        interaction.getPersistentDataContainer().set(
                key,
                PersistentDataType.STRING,
                uuid.toString()
        );

        NamespacedKey diceKey = new NamespacedKey(Main.getInstance(), "farkle_dice_id");
        interaction.getPersistentDataContainer().set(
                diceKey,
                PersistentDataType.INTEGER,
                id
        );

        this.interaction = interaction;
    }

    public void setLocked() {
        blockDisplay.setGlowing(true);
        blockDisplay.setGlowColorOverride(Color.BLUE);
    }

    public void setFarkleState() {
        blockDisplay.setGlowing(true);
        blockDisplay.setGlowColorOverride(Color.RED);
    }

    public void reroll(Location location) {
        blockDisplay.teleport(location);
        blockDisplay.setGlowing(false);
        blockDisplay.setGlowColorOverride(Color.LIME);
        this.randomLocation = location;
        Location fixedLoc = location.clone().add(0, -0.5f * SIZE, 0);
        interaction.teleport(fixedLoc);
        // Reroll the value between 1 and 6
        this.value = (int) (Math.random() * 6) + 1;

        // Update the block display to show the new value
        Transformation transformation = blockDisplay.getTransformation();
        transformation.getLeftRotation().set(DiceValue.getRotation(value));
        transformation.getTranslation().set(DiceValue.applyPositionFix(value, SIZE));
        blockDisplay.setTransformation(transformation);
    }

    public void moveSelected(Location tableLocation, Direction direction) {
        Location newLocation = FarklePlacementUtil.getSelectedDiceLocation(tableLocation, this.id, direction);
        Location fixedLoc = newLocation.clone().add(0, -0.5f * SIZE, 0);
        blockDisplay.teleport(newLocation);
        blockDisplay.setGlowing(true);
        interaction.teleport(fixedLoc);
    }

    public void moveUnselected() {
        Location fixedLoc = randomLocation.clone().add(0, -0.5f * SIZE, 0);
        blockDisplay.teleport(randomLocation);
        blockDisplay.setRotation(randomRotation, 0f);
        blockDisplay.setGlowing(false);
        interaction.teleport(fixedLoc);
    }

    public void remove() {
        blockDisplay.remove();
        interaction.remove();
    }

    public int getValue() {
        return value;
    }
}
