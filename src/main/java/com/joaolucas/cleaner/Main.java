package com.joaolucas.cleaner;

import com.joaolucas.cleaner.command.GiveCleaner;
import com.joaolucas.cleaner.listener.PlotInteractEvent;
import com.joaolucas.cleaner.settings.Settings;
import com.joaolucas.cleaner.utils.InventoryManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Getter
    private static Main instance;

    @Getter
    private static InventoryManager inventoryManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        instance = this;
        inventoryManager = new InventoryManager(this);

        Settings.loadReplaceBlocks();

        getServer().getPluginCommand("givelimpador").setExecutor(new GiveCleaner());
        getServer().getPluginManager().registerEvents(new PlotInteractEvent(), this);

    }
}
