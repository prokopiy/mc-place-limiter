package io.github.prokopiy.mcplacelimiter.commands;

import io.github.prokopiy.mcplacelimiter.Main;
import io.github.prokopiy.mcplacelimiter.Permissions;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;

public class Help implements CommandExecutor {

    private final Main plugin;
    public Help(Main instance) {
        plugin = instance;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        showHelp(src);
        return CommandResult.success();
    }

    void showHelp(CommandSource sender) {
        PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();

        List<Text> contents = new ArrayList<>();
        if (sender.hasPermission(Permissions.WHATS_THIS)) contents.add(plugin.fromLegacy("&3/placelimiter &bwhatsthis - &7Show the block ID the player is looking at"));
        if (sender.hasPermission(Permissions.GROUP_ADD)) contents.add(plugin.fromLegacy("&3/placelimiter &bgroup [add] [GroupName] - &7Add group."));
        if (sender.hasPermission(Permissions.GROUP_REMOVE)) contents.add(plugin.fromLegacy("&3/placelimiter &bgroup [remove] [GroupName] - &7Remove group and all blocks in."));
        if (sender.hasPermission(Permissions.GROUP_UPDATE)) contents.add(plugin.fromLegacy("&3/placelimiter &bgroup [update] [GroupName] - &7Update new limit of group."));
        if (sender.hasPermission(Permissions.GROUP_INFO)) contents.add(plugin.fromLegacy("&3/placelimiter &bgroup [info] [GroupName] - &7Show group info."));
        if (sender.hasPermission(Permissions.GROUP_LIST)) contents.add(plugin.fromLegacy("&3/placelimiter &bgroup [list] - &7Show list of groups."));
        if (sender.hasPermission(Permissions.BLOCK_ADD)) contents.add(plugin.fromLegacy("&3/placelimiter &bblock [add] [GroupName]- &7Add block, the player is looking at, to the limited block group."));
        if (sender.hasPermission(Permissions.BLOCK_REMOVE)) contents.add(plugin.fromLegacy("&3/placelimiter &bblock [remove] - &7Remove block, the player is looking at, to the limited block list."));

        if (contents.isEmpty()) {
            contents.add(plugin.fromLegacy("&cYou currently do not have any permissions for this plugin."));
        }
        paginationService.builder()
                .title(plugin.fromLegacy("&6Place Limiter Help"))
                .contents(contents)
                .header(plugin.fromLegacy("&3[] = required  () = optional"))
                .padding(Text.of("="))
                .sendTo(sender);
    }
}
