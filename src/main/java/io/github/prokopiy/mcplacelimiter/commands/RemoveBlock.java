package io.github.prokopiy.mcplacelimiter.commands;

import io.github.prokopiy.mcplacelimiter.Main;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;

public class RemoveBlock implements CommandExecutor {
    private final Main plugin;

    public RemoveBlock(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String blockId = args.<String>getOne("BlockId").get();

        if (plugin.removeBlockById(blockId) != null) {
            src.sendMessage(Text.of("Block " + blockId + " was removed the list."));
            try {
                plugin.saveData();
            } catch (Exception e) {
                src.sendMessage(Text.of("Data was not saved correctly."));
                e.printStackTrace();
            }
        } else {
            src.sendMessage(Text.of(blockId + " not found!"));
        }

        return CommandResult.success();
    }
}
