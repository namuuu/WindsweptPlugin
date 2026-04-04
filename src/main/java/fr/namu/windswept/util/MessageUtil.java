package fr.namu.windswept.util;

import org.bukkit.entity.Player;

import java.util.Map;

public class MessageUtil {
    private final static String PREFIX = "§6[§cWindswept§6] §r";

    public static void sendError(Player player, String key, Map<String, String> placeholders) {

        for(String entry: placeholders.keySet()) {
            key = key.replace("{" + entry + "}", placeholders.get(entry));
        }

        player.sendMessage(PREFIX + "§c" + key);
    }
}
