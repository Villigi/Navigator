package de.villigi.navigator.listener;

import de.villigi.navigator.Navigator;
import de.villigi.navigator.handler.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class Listener implements org.bukkit.event.Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        p.getInventory().setItem(8, new ItemBuilder(Material.COMPASS)
                .setDisplayname(ChatColor.translateAlternateColorCodes('&', Navigator.getInstance().getConfigManager().getString("Item_Navigator_Title")))
                .setCustomModelData(Navigator.getInstance().getConfigManager().getCustomModelData())
                .build());
   }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();

        if (event.getItem() != null && event.getItem().getItemMeta() != null &&
                event.getItem().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', Navigator.getInstance().getConfigManager().getString("Item_Navigator_Title")))) {
            event.setCancelled(true);

            if (event.getAction().isRightClick() || event.getAction().isLeftClick()) {
                p.openInventory(Navigator.getInstance().getConfigManager().getOrCreateMenu(p));
            }
        }
    }

    @EventHandler
    public void onSwapHandItems(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        ItemStack offHandItem = event.getOffHandItem();
        ItemStack mainHandItem = event.getMainHandItem();

        if ((offHandItem != null && offHandItem.getType() == Material.COMPASS &&
                offHandItem.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', Navigator.getInstance().getConfigManager().getString("Item_Navigator_Title")))) ||
                (mainHandItem != null && mainHandItem.getType() == Material.COMPASS &&
                        mainHandItem.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', Navigator.getInstance().getConfigManager().getString("Item_Navigator_Title"))))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player p = event.getEntity();
        p.getInventory().setItem(8, new ItemBuilder(Material.COMPASS)
                .setDisplayname(ChatColor.translateAlternateColorCodes('&', Navigator.getInstance().getConfigManager().getString("Item_Navigator_Title")))
                .setCustomModelData(Navigator.getInstance().getConfigManager().getCustomModelData())
                .build());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        if (event.getCurrentItem() != null && event.getCurrentItem().getItemMeta() != null &&
                event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', Navigator.getInstance().getConfigManager().getString("Item_Navigator_Title")))) {
            event.setCancelled(true);
        }

        if (event.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&', Navigator.getInstance().getConfigManager().getString("Inventory_Title"))) && event.getCurrentItem() != null && event.getCurrentItem().getItemMeta() != null) {
            event.setCancelled(true);
            int slot = event.getSlot();
            for (int i = 0; i < Navigator.getInstance().getConfigManager().itemAmount; i++) {
                String itemNumber = "Item_" + i;
                JsonObject itemObject = JsonParser.parseString(Navigator.getInstance().getConfigManager().getItemString(itemNumber)).getAsJsonObject();
                if (itemObject.get("slot").getAsInt() == slot) {
                    p.performCommand(itemObject.get("command").getAsString());
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (event.getMessage().equalsIgnoreCase("/clear")) {
            Bukkit.getScheduler().runTaskLater(Navigator.getInstance(), () -> {
                player.getInventory().setItem(8, new ItemBuilder(Material.COMPASS)
                        .setDisplayname(ChatColor.translateAlternateColorCodes('&', Navigator.getInstance().getConfigManager().getString("Item_Navigator_Title")))
                        .setCustomModelData(Navigator.getInstance().getConfigManager().getCustomModelData())
                        .build());
            }, 1L);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', Navigator.getInstance().getConfigManager().getString("Item_Navigator_Title")))) {
            event.setCancelled(true);
        }
    }
}