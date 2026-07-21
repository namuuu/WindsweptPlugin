package fr.namu.windswept.manager;

import fr.namu.windswept.instance.PlayerInstance;
import fr.namu.windswept.util.SavePlayerDataUtil;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerManager {
    private static PlayerManager instance;
    private final HashMap<UUID, PlayerInstance> playerDataMap = new HashMap<>();

    public static PlayerManager getInstance() {
        if (instance == null) {
            instance = new PlayerManager();
        }
        return instance;
    }

    public static void add(UUID playerUUID, PlayerInstance playerInstance) {
        getInstance().playerDataMap.put(playerUUID, playerInstance);
    }

    public static PlayerInstance get(UUID playerUUID) {
        return getInstance().playerDataMap.get(playerUUID);
    }

    public static void remove(UUID playerUUID) {
        getInstance().playerDataMap.remove(playerUUID);
    }

    public static void saveAll() {
        for (PlayerInstance playerInstance : getInstance().playerDataMap.values()) {
            playerInstance.save();
        }
    }

    /* Function that is ran when a player joins to load everything up
     */
    public static PlayerInstance load(Player player) {
        try {
            PlayerInstance newPlayerInstance = SavePlayerDataUtil.load(player.getUniqueId());
            System.out.println("Loaded player instance for " + player.getName() + ": " + newPlayerInstance);
            if(newPlayerInstance == null) {
                newPlayerInstance = new PlayerInstance(player);
            } else {
                newPlayerInstance = new PlayerInstance(newPlayerInstance); // Create a new instance to avoid modifying the loaded one directly
            }
            remove(player.getUniqueId());
            add(player.getUniqueId(), newPlayerInstance);
            return newPlayerInstance;
        } catch (Exception e) {
            System.out.println("Failed to load player instance for " + player.getName());
            PlayerInstance newPlayerInstance = new PlayerInstance(player);
            remove(player.getUniqueId());
            add(player.getUniqueId(), newPlayerInstance);
            return newPlayerInstance;
        }
    }
}
