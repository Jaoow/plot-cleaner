package com.joaolucas.cleaner.inventory;

import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.joaolucas.cleaner.Main;
import com.joaolucas.cleaner.cleaner.Cleaner;
import com.joaolucas.cleaner.settings.Messages;
import com.joaolucas.cleaner.settings.Settings;
import com.joaolucas.cleaner.utils.InventoryManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.CompletableFuture;

public class ConfirmInventory implements InventoryManager.InventoryProvider {

    private final Plot plot;
    private final InventoryManager.InventoryBuilder builder;

    public ConfirmInventory(Plot mine) {
        this.plot = mine;
        builder = new InventoryManager.InventoryBuilder()
                .name(Settings.CONFIRM_INVENTORY_TITLE)
                .size(Settings.CONFIRM_INVENTORY_SIZE * 9)
                .manager(Main.getInventoryManager()).provider(this);
    }

    public void open(Player player) {
        builder.open(player);
    }


    @Override
    public boolean initialize(Player player, InventoryManager.InventoryBuilder builder, InventoryManager manager) {

        builder.appendItem(Settings.CONFIRM_INVENTORY_ITEM_DECLINE_SLOT,
                InventoryManager.ClickableItem.of(Settings.CONFIRM_INVENTORY_ITEM_DECLINE, clickEvent -> builder.close(player))
        );

        builder.appendItem(Settings.CONFIRM_INVENTORY_ITEM_ACCEPT_SLOT,
                InventoryManager.ClickableItem.of(Settings.CONFIRM_INVENTORY_ITEM_ACCEPT, clickEvent -> {

                    builder.close(player);

                    if (player.getItemInHand().getAmount() > 1) {
                        player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
                    } else {
                        player.setItemInHand(new ItemStack(Material.AIR));
                    }

                    player.sendMessage(Messages.START_CLEARING);

                    Cleaner cleaner = new Cleaner(plot, player.getLocation());
                    CompletableFuture<?> future = cleaner.executeClear().whenComplete((editSession, throwable) -> {
                        for (PlotPlayer plotPlayer : plot.getPlayersInPlot()) {
                            plot.teleportPlayer(plotPlayer);
                        }

                        player.sendMessage(Messages.FINISHED_CLEANING.replace("%blocks%", String.valueOf(editSession.getBlockChangeCount())));
                    });

                    CompletableFuture.runAsync(() -> {
                        while (!future.isDone()) {
                            System.out.println("limpando");
                        }
                    });
                })
        );
        return true;
    }
}
