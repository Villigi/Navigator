package de.villigi.navigator.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import de.villigi.navigator.Navigator;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class ConfigManager {

    private final JavaPlugin plugin;
    private final File configFile;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create(); // Pretty JSON output

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(Navigator.getInstance().getDataFolder(), "Config.json");
    }


    public void createConfigFile() {
        if (!configFile.exists()) {
            try {
                configFile.getParentFile().mkdirs();
                JsonObject defaultConfig = new JsonObject();
                defaultConfig.addProperty("Model_Data", 0);

                defaultConfig.addProperty("Item_Navigator_Title", "&6Navigator");
                defaultConfig.addProperty("Inventory_Title", "&6Navigator");
                for (int i = 0; i<13; i++) {
                    defaultConfig.addProperty("Item_" + i + "_type", String.valueOf(Material.PAPER));
                    defaultConfig.addProperty("Item_" + i + "_title", "Titel");
                    defaultConfig.addProperty("Item_" + i + "_command", "/help");
                    defaultConfig.addProperty("Item_" + i + "_slot", i);
                }
                try (FileWriter writer = new FileWriter(configFile)) {
                    gson.toJson(defaultConfig, writer);
                }

                Navigator.getInstance().getLogger().info("Config file created at " + configFile.getPath());
            } catch (IOException e) {
                Navigator.getInstance().getLogger().severe("Could not create config file: " + e.getMessage());
            }
        } else {
            Navigator.getInstance().getLogger().info("Config file already exists at " + configFile.getPath());
        }
    }

    public int getCustomModelData() {
        if (!configFile.exists()) {
            Navigator.getInstance().getLogger().warning("Config file does not exist. Returning default value.");
            return 0;
        }

        try (FileReader reader = new FileReader(configFile);
             JsonReader jsonReader = new JsonReader(reader)) {
            JsonObject jsonObject = gson.fromJson(jsonReader, JsonObject.class);
            return jsonObject != null && jsonObject.has("Model_Data") ? jsonObject.get("Model_Data").getAsInt() : 0;
        } catch (IOException | JsonSyntaxException e) {
            Navigator.getInstance().getLogger().severe("Could not read config file: " + e.getMessage());
            return 0;
        }
    }

    public String getString(String key) {
        if (!configFile.exists()) {
            Navigator.getInstance().getLogger().warning("Config file does not exist. Returning default value.");
            return "";
        }

        try (FileReader reader = new FileReader(configFile);
             BufferedReader bufferedReader = new BufferedReader(reader)) {

            JsonObject jsonObject = JsonParser.parseReader(bufferedReader).getAsJsonObject();

            if (jsonObject.has(key) && !jsonObject.get(key).isJsonNull()) {
                return jsonObject.get(key).getAsString();
            } else {
                Navigator.getInstance().getLogger().warning("Key '" + key + "' not found in config. Returning default value.");
                return "";
            }

        } catch (IOException | JsonSyntaxException e) {
            Navigator.getInstance().getLogger().severe("Could not read config file: " + e.getMessage());
            return "";
        }
    }
    public int getInt(String key) {
        if (!configFile.exists()) {
            Navigator.getInstance().getLogger().warning("Config file does not exist. Returning default value.");
            return 0;
        }

        try (FileReader reader = new FileReader(configFile);
             BufferedReader bufferedReader = new BufferedReader(reader)) {

            JsonObject jsonObject = JsonParser.parseReader(bufferedReader).getAsJsonObject();

            if (jsonObject.has(key) && !jsonObject.get(key).isJsonNull()) {
                return jsonObject.get(key).getAsInt();
            } else {
                Navigator.getInstance().getLogger().warning("Key '" + key + "' not found in config. Returning default value.");
                return 0;
            }

        } catch (IOException | JsonSyntaxException e) {
            Navigator.getInstance().getLogger().severe("Could not read config file: " + e.getMessage());
            return 0;
        }
    }


}
