package fr.namu.windswept.manager;

import fr.namu.windswept.instance.FarkleInstance;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class FarkleManager {
    private static FarkleManager instance;
    private final HashMap<UUID, FarkleInstance> activeFarkleInstances = new HashMap<>();
    private final HashMap<Player, Player> playRequests = new HashMap<>();
    private final HashMap<Player, Long> lastRequestTime = new HashMap<>();

    private FarkleManager() {
        // Private constructor to prevent instantiation
    }

    public static FarkleManager getInstance() {
        if (instance == null) {
            instance = new FarkleManager();
        }
        return instance;
    }

    public static void addFarkleInstance(UUID uuid, FarkleInstance instance) {
        getInstance().activeFarkleInstances.put(uuid, instance);
    }

    public static FarkleInstance getFarkleInstanceByUUID(UUID uuid) {
        return getInstance().activeFarkleInstances.get(uuid);
    }

    public static Collection<FarkleInstance> getallFarkleInstances() {
        return getInstance().activeFarkleInstances.values();
    }

    public static FarkleInstance getFarkleInstanceByPlayer(Player player) {
        for (FarkleInstance instance : getInstance().activeFarkleInstances.values()) {
            if (instance.isPlayerInInstance(player) != 0) {
                return instance;
            }
        }
        return null; // Not found
    }

    public static void addPlayRequest(Player sender, Player receiver) {
        getInstance().playRequests.put(sender, receiver);
        getInstance().lastRequestTime.put(sender, System.currentTimeMillis());
    }

    public static boolean hasPlayRequest(Player sender, Player receiver) {
        return getInstance().playRequests.getOrDefault(sender, null) == receiver && (System.currentTimeMillis() - getInstance().lastRequestTime.getOrDefault(sender, 0L) < 60000 * 5); // 5 minutes expiration
    }

    public static void removePlayRequest(Player sender, Player receiver) {
        getInstance().playRequests.remove(sender, receiver);
        getInstance().lastRequestTime.remove(sender);
    }
}
