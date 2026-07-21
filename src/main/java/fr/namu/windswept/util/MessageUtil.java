package fr.namu.windswept.util;

import org.bukkit.entity.Player;

import java.util.Map;

public class MessageUtil {
    private final static String PREFIX = "§6[§cWindswept§6] §r";

    public static void sendMessage(Player player, String key) {
        player.sendMessage(PREFIX + "§a" + key);
    }

    public static void sendError(Player player, String key) {
        player.sendMessage(PREFIX + "§c" + key);
    }
}
