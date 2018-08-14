package io.github.prokopiy.mcplacelimiter.commands;

import io.github.prokopiy.mcplacelimiter.Main;
import io.github.prokopiy.mcplacelimiter.data.BlockData;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;

public class InfoGroup implements CommandExecutor {
    private final Main plugin;

    public InfoGroup(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = (Player) src;
        String groupname = args.<String>getOne("GroupName").get();

        final List<BlockData> list = new ArrayList<BlockData>(plugin.getBlocksData());

        String msg = "&6Limit in: &e" + groupname + " &6is &e" + plugin.getGroupLimit(groupname) + " &6. Blocks:";
        player.sendMessage(plugin.fromLegacy(msg));
        for (BlockData i : list) {
            if (i.getBlockGroup().equalsIgnoreCase(groupname)) {
                player.sendMessage(plugin.fromLegacy("&e" + i.getBlockid()));
            }
        }

        return CommandResult.success();
    }
}
