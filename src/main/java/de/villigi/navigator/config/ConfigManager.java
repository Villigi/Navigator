package de.villigi.navigator.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import de.villigi.navigator.Navigator;
import de.villigi.navigator.handler.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.HashMap;
import java.util.UUID;

public class ConfigManager {

    private final JavaPlugin plugin;
    private final File configFile;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public int itemAmount = 0;

    public final HashMap<Integer, ItemStack> cachedItems = new HashMap<>();
    public final HashMap<UUID, Inventory> playerMenues = new HashMap<>();

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(Navigator.getInstance().getDataFolder(), "Config.json");
    }

    public void loadItems() {
        for (int i = 0; i < 52; i++) {
            if(!Navigator.getInstance().getConfigManager().getItemString("Item_" + i).isEmpty()){
                String itemNumber = "Item_" + i;
                JsonObject itemObject = JsonParser.parseString(Navigator.getInstance().getConfigManager().getItemString(itemNumber)).getAsJsonObject();
                ItemStack item = new ItemBuilder(Material.getMaterial(itemObject.get("type").getAsString()))
                        .setDisplayname(ChatColor.translateAlternateColorCodes('&', itemObject.get("title").getAsString()))
                        .setCustomModelData(itemObject.get("modeldata").getAsInt())
                        .build();
                cachedItems.put(itemObject.get("slot").getAsInt(), item);
                itemAmount = i+1;
            }else{
                break;
            }
        }
    }

    public Inventory getOrCreateMenu(Player p) {
        return playerMenues.computeIfAbsent(p.getUniqueId(), id -> {
            Inventory inv = Bukkit.createInventory(null, 6 * 9, ChatColor.translateAlternateColorCodes('&', Navigator.getInstance().getConfigManager().getString("Inventory_Title")));
            cachedItems.forEach(inv::setItem);
            return inv;
        });
    }

    public void createConfigFile() {
        if (!configFile.exists()) {
            try {
                configFile.getParentFile().mkdirs();
                JsonObject defaultConfig = new JsonObject();
                defaultConfig.addProperty("Model_Data", 0);
                defaultConfig.addProperty("Item_Navigator_Title", "&6Navigator");
                defaultConfig.addProperty("Inventory_Title", "&6Navigator");

                JsonObject itemsObject = new JsonObject();
                for (int i = 0; i < 13; i++) {
                    JsonObject itemObject = new JsonObject();
                    itemObject.addProperty("slot", i);
                    itemObject.addProperty("type", String.valueOf(Material.PAPER));
                    itemObject.addProperty("title", "Titel");
                    itemObject.addProperty("command", "/help");
                    itemObject.addProperty("modeldata", 0);
                    itemsObject.add("Item_" + i, itemObject);
                }
                defaultConfig.add("items", itemsObject);

                try (FileWriter writer = new FileWriter(configFile)) {
                    gson.toJson(defaultConfig, writer);
                }

            } catch (IOException e) {
            }
        } else {
        }
    }

    public int getCustomModelData() {
        return getInt("Model_Data");
    }

    public String getString(String key) {
        if (!configFile.exists()) {

            return "";
        }

        try (FileReader reader = new FileReader(configFile);
             BufferedReader bufferedReader = new BufferedReader(reader)) {

            JsonObject jsonObject = JsonParser.parseReader(bufferedReader).getAsJsonObject();

            if (jsonObject.has(key) && !jsonObject.get(key).isJsonNull()) {
                return jsonObject.get(key).getAsString();
            } else {

                return "";
            }

        } catch (IOException | JsonSyntaxException e) {
            return "";
        }
    }

    public String getItemString(String itemNumber) {
        if (!configFile.exists()) {
            return "";
        }

        try (FileReader reader = new FileReader(configFile);
             BufferedReader bufferedReader = new BufferedReader(reader)) {

            JsonObject jsonObject = JsonParser.parseReader(bufferedReader).getAsJsonObject();
            JsonObject itemsObject = jsonObject.getAsJsonObject("items");

            if (itemsObject != null && itemsObject.has(itemNumber)) {
                JsonObject itemObject = itemsObject.getAsJsonObject(itemNumber);
                return itemObject.toString();
            }
            return "";

        } catch (IOException | JsonSyntaxException e) {
            return "";
        }
    }

    public int getInt(String itemNumber) {
        if (!configFile.exists()) {
            return 0;
        }

        try (FileReader reader = new FileReader(configFile);
             BufferedReader bufferedReader = new BufferedReader(reader)) {

            JsonObject jsonObject = JsonParser.parseReader(bufferedReader).getAsJsonObject();
            JsonObject itemsObject = jsonObject.getAsJsonObject("items");

            if (itemsObject != null && itemsObject.has(itemNumber)) {
                JsonObject itemObject = itemsObject.getAsJsonObject(itemNumber);
                return itemObject.get("slot").getAsInt();
            }
            return 0;

        } catch (IOException | JsonSyntaxException e) {
            return 0;
        }
    }
}