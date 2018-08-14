package io.github.prokopiy.mcplacelimiter.commands;

import io.github.prokopiy.mcplacelimiter.Main;
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

public class Whatsthis implements CommandExecutor {
    private final Main plugin;

    public Whatsthis(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            throw new CommandException(plugin.fromLegacy("Only players can run this command"));
        }
        Player player = (Player) src;


        BlockRay<World> blockRay = BlockRay.from(player).stopFilter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1)).build();
        Optional<BlockRayHit<World>> hitOpt = blockRay.end();
        if (hitOpt.isPresent()) {
            BlockRayHit<World> hit = hitOpt.get();
            String itemId = plugin.getLocationID(hit.getLocation());
//            plugin.logToFile("ban-list", player.getName() + " added " +mainHandItem.getTranslation().get()+ " to the ban list");
            String msg = "&6This block is: &e" + itemId;
            String gn = plugin.getBlockGroup(itemId);
            Integer gl = plugin.getGroupLimit(gn);
            if (gn != null) {
                msg = msg + " &6in &e" + gn + " &6witch limit &e" + gl.toString() + " &6blocks.";
            }
            player.sendMessage(plugin.fromLegacy(msg));

        } else {
            throw new CommandException(Text.of("Is empty!"));
        }


        return CommandResult.success();
    }
}
