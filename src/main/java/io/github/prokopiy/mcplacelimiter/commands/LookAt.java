package io.github.prokopiy.mcplacelimiter.commands;

//import com.crycode.mc.placerestrict.placerestrict.Config;

import io.github.prokopiy.mcplacelimiter.Main;
import io.github.prokopiy.mcplacelimiter.data.BlockData;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.World;

import java.util.Optional;


public class LookAt implements CommandExecutor {
    private final Main plugin;

    public LookAt(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String groupName = args.<String>getOne("GroupName").get();
        if (!(src instanceof Player)) {
            throw new CommandException(Text.of("Console users cannot use this command"));
        }
        Player player = (Player) src;
        if (!plugin.groupNameExists(groupName)) {
//            player.sendMessage(plugin.fromLegacy("&eGroup not exists!"));
            throw new CommandException(Text.of("Group not exists!"));
        }


        BlockRay<World> blockRay = BlockRay.from(player).stopFilter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1)).build();
        Optional<BlockRayHit<World>> hitOpt = blockRay.end();
        if (hitOpt.isPresent()) {
            BlockRayHit<World> hit = hitOpt.get();
            String itemId = plugin.getLocationID(hit.getLocation());

            String g = plugin.getBlockGroup(itemId);
            if ( g != null) {
                throw new CommandException(Text.of("Block already in group " + g));
            }

            plugin.addBlock(new BlockData(
                    itemId,
                    groupName
                ));

            try {
                plugin.saveData();
            } catch (Exception e) {
                player.sendMessage(Text.of("Data was not saved correctly."));
                e.printStackTrace();
            }
//            plugin.logToFile("ban-list", player.getName() + " added " +mainHandItem.getTranslation().get()+ " to the ban list");
            player.sendMessage(plugin.fromLegacy("&e" + itemId + " &6was added to the group &e" + groupName + "&6."));

        } else {
                throw new CommandException(Text.of("Is empty!"));
            }

        return CommandResult.success();
    }
}
