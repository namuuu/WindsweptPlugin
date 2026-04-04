package fr.namu.windswept.util.farkle;

import fr.namu.windswept.Main;
import fr.namu.windswept.enums.Direction;
import fr.namu.windswept.instance.DiceInstance;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class FarklePlacementUtil {

    private static final float FEATHER_SIZE = 0.15f;

    /**
     * Gets the location where the NPC should be placed for the given player.
     * It is 5 block in front of the player (not impacted if the player looks up or down) and be placed on the ground
     * It should check if the space is clear, and if not return null.
     * @param player Location of player 1
     * @return The location where the NPC should be placed, or null if the space is not clear.
     */
    public static Location getNPCLocation(Player player) {
        Location playerLocation = player.getLocation();
        Location npcLocation = playerLocation.clone().add(playerLocation.getDirection().setY(0).normalize().multiply(5));

        // Check if the space is clear
        if (npcLocation.getBlock().isEmpty() && npcLocation.clone().getBlock().isEmpty()) {
            return npcLocation;
        } else {
            return null;
        }
    }

    /**
     * Gets the location where the table should be placed for the given two locations.
     * @param loc1 the first location (e.g. the NPC location)
     * @param loc2 the second location (e.g. the player location)
     */
    public static Location getTableLocation(Location loc1, Location loc2) {
        // Get middle between two locations
        Location tableLocation = loc1.clone().add(loc2).multiply(0.5);

        tableLocation.setX(Math.round(tableLocation.getX()));
        tableLocation.setY(Math.round(tableLocation.getY()));
        tableLocation.setZ(Math.round(tableLocation.getZ()));
        tableLocation.setYaw(0);
        tableLocation.setPitch(0);

        // Check if the space is clear
        if (tableLocation.getBlock().isEmpty()) {
            return tableLocation;
        } else {
            return null;
        }
    }

    public static BlockDisplay spawnTable(Location location) {
        location.getBlock().setType(Material.BARRIER); // Place a barrier block to simulate collision
        location.setY(location.getY() - 1); // Account for the raising animation
        BlockDisplay display = location.getWorld().spawn(location, BlockDisplay.class);
        display.setBlock(Material.SPRUCE_LOG.createBlockData());
        display.setBrightness(new Display.Brightness(15, 15));

        // Animate the table rising up from the ground
        BukkitTask tableTask = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> {
            if (display.isValid()) {
                display.teleport(display.getLocation().add(0, 0.05, 0));
            }
        }, 0, 1);

        // Stop the table rising animation after 1 second
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), tableTask::cancel, 20); // Remove after 1 second

        return display;
    }

    // If the player 1's dice goes North and South if selected, the Feather spawns west
    public static ItemDisplay spawnFeather(Location tableLocation) {
        Location featherLocation = tableLocation.clone().add(0.05, 2.01, 0.5);
        ItemDisplay display = tableLocation.getWorld().spawn(featherLocation, ItemDisplay.class);
        display.setItemStack(new ItemStack(Material.FEATHER));
        display.setBrightness(new Display.Brightness(15, 15));
        display.setRotation(0, 90);

        Transformation transformation = display.getTransformation();
        transformation.getScale().set(FEATHER_SIZE, FEATHER_SIZE, FEATHER_SIZE);

        display.setTransformation(transformation);

        return display;
    }

    public static Interaction spawnFeatherInteraction(UUID uuid, Location tableLocation) {
        Location featherLocation = tableLocation.clone().add(0.05, 2, 0.5);
        Interaction interaction = tableLocation.getWorld().spawn(featherLocation, Interaction.class);
        interaction.setInteractionWidth(FEATHER_SIZE);
        interaction.setInteractionHeight(FEATHER_SIZE);

        NamespacedKey key = new NamespacedKey(Main.getInstance(), "farkle_feather_game_id");
        interaction.getPersistentDataContainer().set(
                key,
                PersistentDataType.STRING,
                uuid.toString()
        );

        return interaction;
    }

    public static ItemDisplay spawnSack(Location tableLocation) {
        Location sackLocation = tableLocation.clone().add(0.95, 2.01, 0.5);
        ItemDisplay display = tableLocation.getWorld().spawn(sackLocation, ItemDisplay.class);
        display.setItemStack(new ItemStack(Material.BUNDLE));
        display.setBrightness(new Display.Brightness(15, 15));
        display.setRotation(0, 90);

        Transformation transformation = display.getTransformation();
        transformation.getScale().set(FEATHER_SIZE, FEATHER_SIZE, FEATHER_SIZE);

        display.setTransformation(transformation);

        return display;
    }

    public static Interaction spawnSackInteraction(UUID uuid, Location tableLocation) {
        Location sackLocation = tableLocation.clone().add(0.95, 2, 0.5);
        Interaction interaction = tableLocation.getWorld().spawn(sackLocation, Interaction.class);
        interaction.setInteractionWidth(FEATHER_SIZE);
        interaction.setInteractionHeight(FEATHER_SIZE);

        NamespacedKey key = new NamespacedKey(Main.getInstance(), "farkle_sack_game_id");
        interaction.getPersistentDataContainer().set(
                key,
                PersistentDataType.STRING,
                uuid.toString()
        );

        return interaction;
    }

    public static HashMap<Integer, DiceInstance> spawnDice(UUID uuid, Location tableLocation) {
        HashMap<Integer, DiceInstance> diceMap = new HashMap<>();
        List<Location> diceLocations = getDiceLocations(tableLocation, 6);

        for(int i = 0; i < 6; i++) {
            DiceInstance die = new DiceInstance(uuid, i, diceLocations.get(i));
            diceMap.put(i, die);
        }

        return diceMap;
    }

    // If the location is a corner of the table at (0,0), then the valid dice locations are random between (0.2, 0.2) and (0.8, 0.8) on the X and Z axis, and are placed on the table (Y = 0.5).
    public static List<Location> getDiceLocations(Location tableLocation, int diceCount) {
        List<Location> locationList = new ArrayList<>();
        int diceCounter = 0;

        // Loop to make sure the dice don't spawn on top of each other, and that they are all in the valid area
        while(diceCounter < diceCount) {
            double x = (Math.random() * 0.6 + 0.2);
            double z = (Math.random() * 0.6 + 0.2);

            // Y is 2 blocs + half a dice (0.08) above the table location, so the dice are on the table
            Location diceLocation = tableLocation.clone().add(x, 2.04, z);

            boolean isValid = true;

            for(Location loc : locationList) {
                if(loc.distance(diceLocation) < 0.15) { // If the new dice is too close to an existing one, it's not valid
                    isValid = false;
                    break;
                }
            }

            if(isValid) {
                locationList.add(diceLocation);
                diceCounter++;
            }
        }

        return locationList;
    }

    // When a player select a dice, it goes to its designed selectedDiceLocation based on its id
    public static Location getSelectedDiceLocation(Location tableLocation, int diceId, Direction direction) {
        // The selected dice should be placed on a side of the table, in order of the diceId.
        Location diceLocation = tableLocation.clone();

        if(direction == Direction.NORTH) {
            diceLocation.add(0.1 + diceId * 0.15, 2.04, 0.1);
        } else if(direction == Direction.SOUTH) {
            diceLocation.add(0.9 - diceId * 0.15, 2.04, 0.9);
        } else if(direction == Direction.WEST) {
            diceLocation.add(0.1, 2.04, 0.1 + diceId * 0.15);
        } else if(direction == Direction.EAST) {
            diceLocation.add(0.9, 2.04, 0.9 - diceId * 0.15);
        }

        return diceLocation;
    }
}
