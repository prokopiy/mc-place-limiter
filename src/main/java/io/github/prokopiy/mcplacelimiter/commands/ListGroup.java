package io.github.prokopiy.mcplacelimiter.commands;

import io.github.prokopiy.mcplacelimiter.Main;
import io.github.prokopiy.mcplacelimiter.data.BlockData;
import io.github.prokopiy.mcplacelimiter.data.GroupData;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;
import java.util.List;

public class ListGroup implements CommandExecutor {
    private final Main plugin;

    public ListGroup(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = (Player) src;
//        String groupname = args.<String>getOne("GroupName").get();

        final List<GroupData> list = new ArrayList<GroupData>(plugin.getGroupsData());

        String msg = "&6Limitation groups:";
        player.sendMessage(plugin.fromLegacy(msg));
        for (GroupData i : list) {
            player.sendMessage(plugin.fromLegacy("&e" + i.getGroupName() + " &6with limit &e" + i.getGroupLimit()));
        }

        return CommandResult.success();
    }
}
