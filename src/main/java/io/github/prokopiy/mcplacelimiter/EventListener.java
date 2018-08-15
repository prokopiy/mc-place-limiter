package io.github.prokopiy.mcplacelimiter;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Date;
import java.util.regex.Pattern;



public class EventListener {

    private static final Pattern PATTERN_META = Pattern.compile("\\.[\\d+]*$");

    private Main plugin;
    public EventListener(Main instance) {
        plugin = instance;
    }


    @Listener
    public void onBlockPlace(ChangeBlockEvent.Place event, @Root Player player) {
//        player.sendMessage(plugin.fromLegacy("&conBlockPlace"));
        if (event.getTransactions().get(0).getFinal().getState().getType().equals(BlockTypes.AIR)) {return;}
        if (player.hasPermission(Permissions.BYPASS_LIMITED_BLOCK)) {return;}

        BlockSnapshot targetBlock = event.getTransactions().get(0).getFinal();

        String limitedGroup = getLimitedGroup(targetBlock);
        if (limitedGroup != null) {
            String blockId = plugin.getLocationID(targetBlock.getLocation().get());
            Integer groupLimit = plugin.getGroupLimit(limitedGroup);
            if (Config.logToFile) {
                Date date = new Date();
                plugin.logToFile("place-limiter-log", date + " - " +  player.getName() + " tried to place " + blockId + " in " + targetBlock.getPosition().toString());
            }
            if (groupLimit < 1) {
                player.sendMessage(plugin.fromLegacy("&6The &e" + blockId + " &6is banned!"));
                event.setCancelled(true);
            } else {
                Integer limitedCount = checkCount(targetBlock, limitedGroup, player);
//                player.sendMessage(plugin.fromLegacy("&cPR: limitedCount = " + limitedCount.toString()));
                if (limitedCount > groupLimit) {
                    player.sendMessage(plugin.fromLegacy("&6In this chunk, the limit (&e" + groupLimit +"&6) of blocks from &e" + limitedGroup));
                    event.setCancelled(true);
                }
            }
        }
    }


    private String getLimitedGroup(BlockSnapshot blockSnapshot) {
        String itemID = plugin.getLocationID(blockSnapshot.getLocation().get());
        if (itemID != null) {
            return plugin.getBlockGroup(itemID);
        } else {
            return null;
        }
    }


    private Integer checkCount(BlockSnapshot targetBlock, String groupName, Player player) {
        int count = 0;
        World world = player.getWorld();
        Chunk chunk = world.getChunkAtBlock(targetBlock.getPosition()).get();
        Integer limit = plugin.getGroupLimit(groupName);
        String blockId;
        Vector3i min = chunk.getBlockMin();
        Vector3i max = chunk.getBlockMax();
        for (int y = min.getY(); y <= max.getY(); y++) {
            for (int x = min.getX(); x <= max.getX(); x++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    Location blockLoc = chunk.getLocation(x, y, z);
                    blockId = plugin.getLocationID(blockLoc);
                    if (blockId != null){
                        if (groupName.equals(plugin.getBlockGroup(blockId))) {
                            count += 1;
                        }
                        if (count > limit) {return count;}
                    }
                }
            }
        }
        return count;
    }



}
