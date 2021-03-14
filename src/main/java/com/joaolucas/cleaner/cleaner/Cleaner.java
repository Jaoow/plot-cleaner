package com.joaolucas.cleaner.cleaner;

import com.boydti.fawe.util.EditSessionBuilder;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.RegionWrapper;
import com.joaolucas.cleaner.settings.Settings;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.regions.Region;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class Cleaner extends CompletableFuture<EditSession> {

    private final Plot plot;
    private final Location start;

    public CompletableFuture<EditSession> executeClear() {
        return CompletableFuture.supplyAsync(() -> {

            World world = Bukkit.getWorld(plot.getWorldName());
            EditSession session = new EditSessionBuilder(world.getName()).fastmode(true).build();

            getPlotRegion(start, blockVectors -> {
                try {
                    session.replaceBlocks(blockVectors, Settings.CLEAR_MATERIALS, new BaseBlock(0));
                } catch (MaxChangedBlocksException e) {
                    e.printStackTrace();
                }
            });

            session.flushQueue();
            return session;
        });
    }

    @SneakyThrows
    private void getPlotRegion(Location start, Consumer<Region> whenFound) {
        if (Settings.CLEAR_ALL_MERGED) {
            for (RegionWrapper wrapper : plot.getRegions()) {
                com.intellectualcrafters.plot.object.Location[] locations = wrapper.getCorners(plot.getWorldName());

                Location[] convertedLocation = covertLocation(locations);

                CuboidSelection selection = new CuboidSelection(start.getWorld(), convertedLocation[0], convertedLocation[1]);
                Region region = selection.getRegionSelector().getRegion();

                whenFound.accept(region);
            }
        } else {
            for (Plot connectedPlot : plot.getConnectedPlots()) {

                Location[] convertedLocation = {
                        covertLocation(connectedPlot.getBottomAbs()),
                        covertLocation(connectedPlot.getTopAbs()),
                };

                CuboidSelection selection = new CuboidSelection(start.getWorld(), convertedLocation[0], convertedLocation[1]);
                Region region = selection.getRegionSelector().getRegion();

                if (region.contains(BukkitUtil.toVector(start))) {
                    whenFound.accept(region);
                }
            }
        }
    }

    private org.bukkit.Location[] covertLocation(com.intellectualcrafters.plot.object.Location[] location) {
        return Arrays.stream(location).map(item -> new Location(Bukkit.getWorld(item.getWorld()), item.getX(), item.getY(), item.getZ())).toArray(Location[]::new);
    }

    private org.bukkit.Location covertLocation(com.intellectualcrafters.plot.object.Location location) {
        return new org.bukkit.Location(Bukkit.getWorld(location.getWorld()), location.getX(), location.getY(), location.getZ());
    }
}
