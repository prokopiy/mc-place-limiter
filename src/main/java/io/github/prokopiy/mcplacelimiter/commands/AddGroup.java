package io.github.prokopiy.mcplacelimiter.commands;

//import com.crycode.mc.placerestrict.placerestrict.Config;

import io.github.prokopiy.mcplacelimiter.Main;
import io.github.prokopiy.mcplacelimiter.data.GroupData;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;


public class AddGroup implements CommandExecutor {
    private final Main plugin;

    public AddGroup(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String groupName = args.<String>getOne("GroupName").get().toLowerCase();
        Integer groupLimit = args.<Integer>getOne("GroupLimit").get();
        if (!(src instanceof Player)) {
            throw new CommandException(Text.of("Console users cannot use this command"));
        }
        Player player = (Player) src;
//        final java.util.List<ItemData> items = new ArrayList<ItemData>(plugin.getBlocksData());

        if (plugin.groupNameExists(groupName)) {
            throw new CommandException(Text.of("Group already exists!"));
        } else {
            plugin.addGroup(new GroupData(groupName, groupLimit));
            try {
                plugin.saveData();
            } catch (Exception e) {
                player.sendMessage(Text.of("Data was not saved correctly."));
                e.printStackTrace();
            }
            player.sendMessage(plugin.fromLegacy("&e" + groupName + " &6was added witch &e" + groupLimit + "&6 limit."));
        }

        return CommandResult.success();
    }
}
