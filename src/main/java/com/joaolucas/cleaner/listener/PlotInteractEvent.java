package com.joaolucas.cleaner.listener;

import com.intellectualcrafters.plot.object.Location;
import com.intellectualcrafters.plot.object.Plot;
import com.joaolucas.cleaner.inventory.ConfirmInventory;
import com.joaolucas.cleaner.settings.Messages;
import com.joaolucas.cleaner.settings.Settings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class PlotInteractEvent implements Listener {

    @EventHandler
    public void onBreak(BlockBreakEvent event) {

        final Player player = event.getPlayer();
        if (player.getWorld().getName().equalsIgnoreCase(Settings.CLEAR_WORLD_NAME)) {
            if (isCleaner(player.getItemInHand())) {

                Location location = parseLocation(event.getBlock().getLocation());

                Optional.ofNullable(Plot.getPlot(location)).ifPresent(plot -> {
                    if (plot.hasOwner()) {
                        if (plot.getOwners().contains(player.getUniqueId())) {
                            new ConfirmInventory(plot).open(player);
                        } else {
                            player.sendMessage(Messages.NOT_OWN_THIS_PLOT);
                        }
                    }
                });

                event.setCancelled(true);
            }
        }
    }

    private Location parseLocation(org.bukkit.Location location) {
        return new Location(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    private boolean isCleaner(ItemStack itemStack) {
        return itemStack.isSimilar(Settings.ITEM_CLEANER.complete());
    }
}
