package com.joaolucas.cleaner.settings;

import com.joaolucas.cleaner.Main;
import com.joaolucas.cleaner.utils.ItemBuilder;
import com.sk89q.worldedit.blocks.BaseBlock;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Settings {

    private static final FileConfiguration configuration = Main.getInstance().getConfig();

    /*
     * The world where plot executeClear can be used
     */
    public static final String CLEAR_WORLD_NAME = configuration.getString("world-to-clear");

    /*
     * Boolean if will executeClear all merged plots
     */
    public static final Boolean CLEAR_ALL_MERGED = configuration.getBoolean("clear-all-merged");

    /*
     * The list of materials to be deleted
     */
    public static final Set<BaseBlock> CLEAR_MATERIALS = new HashSet<>();

    /*
     * The item of cleaner
     */
    public static final ItemBuilder ITEM_CLEANER = getItemFromConfig("cleaner-item");



    public static final String CONFIRM_INVENTORY_TITLE = Messages.colorized(configuration.getString("confirm-inventory.title"));
    public static final Integer CONFIRM_INVENTORY_SIZE = configuration.getInt("confirm-inventory.size");

    public static final ItemBuilder CONFIRM_INVENTORY_ITEM_DECLINE = getItemFromConfig("confirm-inventory.itens.decline");
    public static final Integer CONFIRM_INVENTORY_ITEM_DECLINE_SLOT = configuration.getInt("confirm-inventory.itens.decline.slot");

    public static final ItemBuilder CONFIRM_INVENTORY_ITEM_ACCEPT = getItemFromConfig("confirm-inventory.itens.accept");
    public static final Integer CONFIRM_INVENTORY_ITEM_ACCEPT_SLOT = configuration.getInt("confirm-inventory.itens.accept.slot");


    public static ItemBuilder getItemFromConfig(String path) {

        Material itemMaterial = Material.matchMaterial(configuration.getString(path + ".material"));
        short itemData = (short) configuration.getInt(path + ".data");

        String itemName = configuration.getString(path + ".name");
        List<String> itemLore = configuration.getStringList(path + ".lore");

        ItemBuilder builder = new ItemBuilder(itemMaterial, 1, itemData);
        builder.setName(itemName);
        builder.setLore(itemLore);

        return builder;
    }

    public static void loadReplaceBlocks() {

        List<String> materials = configuration.getStringList("blocks-to-replace");

        for (String material : materials) {
            if (material.contains(":")) {
                String[] parts = material.split(":");

                int id = Integer.parseInt(parts[0]);
                int data = Integer.parseInt(parts[1]);

                CLEAR_MATERIALS.add(new BaseBlock(id, data));
            } else {
                int id = Integer.parseInt(material);
                CLEAR_MATERIALS.add(new BaseBlock(id));
            }
        }
    }
}
