package com.joaolucas.cleaner.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    private final ItemStack item;


    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
    }

    public ItemBuilder(ItemStack item) {
        this.item = item;
    }

    public ItemBuilder(Material material, short data) {
        this.item = new ItemStack(material, 1, data);
    }

    public ItemBuilder(Material material, int i, short data) {
        this.item = new ItemStack(material, i, data);
    }

    public static ItemStack of(Material material, String name, String... lore) {
        return new ItemBuilder(material).setName(name).setLore(lore).complete();
    }

    public static ItemStack of(Material material, String name, List<String> lore) {
        return new ItemBuilder(material).setName(name).setLore(lore).complete();
    }

    public static ItemBuilder of(ItemStack item) {
        return new ItemBuilder(item);
    }

    public static ItemBuilder copyOf(ItemBuilder itemStack) {
        return new ItemBuilder(itemStack.complete().clone());
    }


    public String searchLore(String string) {
        return item.getItemMeta().getLore().stream().filter(index -> index.contains(string)).findFirst().orElse(null);
    }

    public int searchLoreIndex(String string) {
        return item.getItemMeta().getLore().indexOf(searchLore(string));
    }

    public String getName() {
        return this.item.getItemMeta().getDisplayName();
    }

    public ItemBuilder setName(String name) {
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(colorized(name));
        item.setItemMeta(meta);
        return this;
    }

    public List<String> getLore() {
        return this.item.getItemMeta().getLore();
    }

    public ItemBuilder setLore(String... lore) {
        final ItemMeta meta = item.getItemMeta();
        final List<String> list = new ArrayList<>();
        for (String str : lore) {
            list.add(colorized(str));
        }
        meta.setLore(list);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        final ItemMeta meta = item.getItemMeta();
        final List<String> list = new ArrayList<>();
        for (String str : lore) {
            list.add(colorized(str));
        }
        meta.setLore(list);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder appendLore(String lore) {
        final ItemMeta meta = item.getItemMeta();
        final List<String> list = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        list.add(colorized(lore));
        meta.setLore(list);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder editLore(int index, String string) {
        final ItemMeta meta = item.getItemMeta();
        final List<String> list = meta.getLore();
        list.set(index, colorized(string));
        meta.setLore(list);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setDurability(short durability) {
        item.setDurability(durability);
        return this;
    }

    public ItemBuilder setOwner(String owner) {
        if (!(item.getItemMeta() instanceof SkullMeta)) return this;
        final SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwner(owner);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        final ItemMeta meta = item.getItemMeta();
        meta.spigot().setUnbreakable(unbreakable);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addFlags(ItemFlag... flags) {
        final ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(flags);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantments, int level) {
        final ItemMeta meta = item.getItemMeta();
        meta.addEnchant(enchantments, level, true);
        item.setItemMeta(meta);
        return this;
    }


    public ItemStack complete() {
        return this.item;
    }


    private String colorized(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
