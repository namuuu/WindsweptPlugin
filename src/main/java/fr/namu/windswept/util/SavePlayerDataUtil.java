package fr.namu.windswept.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.namu.windswept.instance.PlayerInstance;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class SavePlayerDataUtil {

    private static File pluginFolder;
    private static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
    private static final String PLAYER_DATA_FOLDER = "playerdata";

    public static void checkFolder(File pluginFolder) {
        SavePlayerDataUtil.pluginFolder = pluginFolder;
        File folder = new File(pluginFolder, PLAYER_DATA_FOLDER);
        if (!folder.exists()) {
            System.out.println("Player data folder does not exist. Creating folder at: " + folder.getAbsolutePath());
            boolean result = folder.mkdirs();
            if(result)
                System.out.println("Player data folder created successfully.");
            else
                System.err.println("Failed to create player data folder.");
        }
    }

    public static void save(PlayerInstance playerInstance) throws IOException {
        File file = new File(pluginFolder, PLAYER_DATA_FOLDER + "/" + playerInstance.getUuid() + ".json");

        try(FileWriter writer = new FileWriter(file)) {
            gson.toJson(playerInstance, writer);
        } catch (IOException e) {
            throw new IOException("Failed to save player data for " + playerInstance.getUuid(), e);
        }
    }

    public static PlayerInstance load(UUID puid) throws IOException {
        File file = new File(pluginFolder, PLAYER_DATA_FOLDER + "/" + puid + ".json");
        System.out.println("Attempting to load player data for " + puid + " from " + file.getAbsolutePath());

        if (!file.exists()) {
            System.out.println("Player data file not found for " + puid + ". A new instance will be created.");
            return null; // No data found for this player, return null to indicate a new instance should be created
        }

        System.out.println("Player data file found for " + puid + ". Loading data...");

        try(FileReader reader = new FileReader(file)) {
            return gson.fromJson(reader, PlayerInstance.class);
        } catch (IOException e) {
            throw new IOException("Failed to load player data for " + puid, e);
        }
    }
}
