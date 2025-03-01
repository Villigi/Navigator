package de.villigi.navigator.listener;

import de.villigi.navigator.Navigator;
import de.villigi.navigator.handler.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;

public class Listener implements org.bukkit.event.Listener {


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = (Player) event.getPlayer();
        p.getInventory().setItem(8, new ItemBuilder(Material.COMPASS)
                .setDisplayname(ChatColor.translateAlternateColorCodes('&', Navigator.getInstance().getConfigManager().getString("Item_Navigator_Title")))
                .build());
        System.out.println("Model Data: " + Navigator.getInstance().getConfigManager().getCustomModelData());

    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();





        if (event.getItem() != null && event.getItem().getItemMeta() != null &&
                event.getItem().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', Navigator.getInstance().getConfigManager().getString("Item_Navigator_Title")))) {
            event.setCancelled(true);

            if(event.getAction().isRightClick() || event.getAction().isLeftClick()) {
                Inventory inv = Bukkit.createInventory(null, 6*9, ChatColor.translateAlternateColorCodes('&', Navigator.getInstance().getConfigManager().getString("Inventory_Title")));
                p.openInventory(inv);

                for(int i = 0; i<13; i++) {
                    inv.setItem(Navigator.getInstance().getConfigManager().getInt("Item_" + i + "_slot"), new ItemBuilder(Material.getMaterial(Navigator.getInstance().getConfigManager().getString("Item_" + i + "_type")))
                            .setDisplayname(ChatColor.translateAlternateColorCodes('&', Navigator.getInstance().getConfigManager().getString("Item_" + i + "_title")))
                            .build());
                }

            }
        }

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        if (event.getCurrentItem() != null && event.getCurrentItem().getItemMeta() != null &&
                event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', Navigator.getInstance().getConfigManager().getString("Item_Navigator_Title")))) {
            event.setCancelled(true);
        }

        if(event.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&', Navigator.getInstance().getConfigManager().getString("Inventory_Title"))) && event.getCurrentItem() != null && event.getCurrentItem().getItemMeta() != null) {
            event.setCancelled(true);
            p.performCommand(Navigator.getInstance().getConfigManager().getString("Item_" + event.getSlot() + "_command"));
        }


    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (event.getMessage().equalsIgnoreCase("/clear")) {
            Bukkit.getScheduler().runTaskLater(Navigator.getInstance(), () -> {
                player.getInventory().setItem(8, new ItemBuilder(Material.COMPASS)
                        .setDisplayname(ChatColor.translateAlternateColorCodes('&', Navigator.getInstance().getConfigManager().getString("Item_Navigator_Title")))
                        .build());
            }, 1L); // Delay to ensure the command is processed first
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', Navigator.getInstance().getConfigManager().getString("Item_Navigator_Title")))) {
            event.setCancelled(true);
        }
    }

}
