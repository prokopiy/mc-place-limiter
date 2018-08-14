package io.github.prokopiy.mcplacelimiter.commands;

import io.github.prokopiy.mcplacelimiter.Main;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

public class RemoveGroup implements CommandExecutor {
    private final Main plugin;

    public RemoveGroup(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String groupName = args.<String>getOne("GroupName").get();

        if (plugin.removeGroupByName(groupName.toLowerCase()) != null) {
            src.sendMessage(Text.of("Group " + groupName + " was removed."));
            try {
                plugin.saveData();
            } catch (Exception e) {
                src.sendMessage(Text.of("Data was not saved correctly."));
                e.printStackTrace();
            }
        } else {
            src.sendMessage(Text.of(groupName + " not found!"));
        }

        return CommandResult.success();
    }
}
