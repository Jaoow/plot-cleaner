package com.joaolucas.cleaner.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.function.Consumer;

public class InventoryManager implements Listener {

    private final WeakHashMap<UUID, InventoryBuilder> inventories;

    public InventoryManager(JavaPlugin plugin) {
        this.inventories = new WeakHashMap<>();
        plugin.getServer().getPluginManager().registerEvents(new InventoryListener(), plugin);
    }


    public void open(Player player, InventoryBuilder builder) {
        inventories.remove(player.getUniqueId());

        inventories.put(player.getUniqueId(), builder);
        player.openInventory(builder.build());
    }

    public void close(Player p) {
        if (!inventories.containsKey(p.getUniqueId())) return;

        inventories.remove(p.getUniqueId());
        p.closeInventory();
    }


    public interface InventoryProvider {

        boolean initialize(Player player, InventoryBuilder builder, InventoryManager manager);

    }

    public static class InventoryBuilder {

        private final HashMap<Integer, ClickableItem> items;
        private String name;
        private int size;
        private InventoryManager manager;
        private InventoryProvider provider;

        private Inventory inventory;


        public InventoryBuilder() {
            items = new HashMap<>();
        }

        public InventoryBuilder name(String name) {
            this.name = name;
            return this;
        }

        public InventoryBuilder size(int size) {
            this.size = size;
            return this;
        }

        public InventoryBuilder provider(InventoryProvider provider) {
            this.provider = provider;
            return this;
        }

        public InventoryBuilder manager(InventoryManager manager) {
            this.manager = manager;
            return this;
        }

        public void appendItem(int slot, ClickableItem item) {
            items.put(slot, item);
        }

        public ClickableItem get(int slot) {
            return items.getOrDefault(slot, null);
        }

        public String getName() {
            return name;
        }

        public int getSize() {
            return size;
        }

        public InventoryManager getManager() {
            return manager;
        }

        public Inventory build() {
            if (inventory == null) {
                inventory = Bukkit.createInventory(null, size, name);
            }
            for (Map.Entry<Integer, ClickableItem> items : items.entrySet()) {
                inventory.setItem(items.getKey(), items.getValue().item);
            }
            return inventory;
        }

        public void refresh(Player player) {
            provider.initialize(player, this, manager);
            build();
        }

        public void close(Player player) {
            this.manager.close(player);
        }

        public void open(Player player) {
            close(player);
            if (provider.initialize(player, this, manager)) {
                manager.open(player, this);
            }
        }
    }

    public static class ClickableItem {

        private final ItemStack item;
        private final Consumer<InventoryClickEvent> consumer;

        public ClickableItem(ItemStack item, Consumer<InventoryClickEvent> consumer) {
            this.item = item;
            this.consumer = consumer;
        }

        public ClickableItem(ItemBuilder builder, Consumer<InventoryClickEvent> consumer) {
            this.item = builder.complete();
            this.consumer = consumer;
        }

        public static ClickableItem of(ItemBuilder builder, Consumer<InventoryClickEvent> consumer) {
            return new ClickableItem(builder, consumer);
        }

        public static ClickableItem of(ItemBuilder builder) {
            return new ClickableItem(builder, e -> {
            });
        }

        public static ClickableItem of(ItemStack item, Consumer<InventoryClickEvent> consumer) {
            return new ClickableItem(item, consumer);
        }

        public static ClickableItem of(ItemStack item) {
            return new ClickableItem(item, e -> {
            });
        }

        public void run(InventoryClickEvent e) {
            this.consumer.accept(e);
        }

        public ItemStack getItem() {
            return item;
        }
    }

    public class InventoryListener implements Listener {

        @EventHandler
        public void onClick(InventoryClickEvent e) {
            final Player p = (Player) e.getWhoClicked();
            if (!inventories.containsKey(p.getUniqueId())) return;

            e.setCancelled(true);
            final InventoryBuilder builder = inventories.get(p.getUniqueId());
            if (e.getRawSlot() > builder.size) return;
            final ClickableItem item = builder.get(e.getRawSlot());
            if (item != null) item.run(e);
        }

        @EventHandler
        public void onClose(InventoryCloseEvent e) {
            final Player p = (Player) e.getPlayer();
            if (!inventories.containsKey(p.getUniqueId())) return;

            inventories.remove(p.getUniqueId());
        }
    }

}
