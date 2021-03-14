package com.joaolucas.cleaner.settings;

import com.joaolucas.cleaner.Main;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class Messages {

    private static final FileConfiguration config = Main.getInstance().getConfig();

    public static final String START_CLEARING = colorized(config.getString("messages.start-clearing"));
    public static final String FINISHED_CLEANING = colorized(config.getString("messages.finished-cleaning"));
    public static final String NOT_OWN_THIS_PLOT = colorized(config.getString("messages.not-own-this-plot"));

    public static String colorized(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
