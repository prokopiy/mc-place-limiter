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
        if (sender.hasPermission(Permissions.WHATS_THIS)) contents.add(plugin.fromLegacy("&3/placerestrict &badd - &7Show the block ID the player is looking at"));
        if (sender.hasPermission(Permissions.ADD_GROUP)) contents.add(plugin.fromLegacy("&3/placerestrict &baddgroup - &7Add group."));
        if (sender.hasPermission(Permissions.ADD_BLOCK)) contents.add(plugin.fromLegacy("&3/placerestrict &badd - &7Add block, the player is looking at, to the limited block list."));
        if (sender.hasPermission(Permissions.REMOVE_BLOCK)) contents.add(plugin.fromLegacy("&3/placerestrict &bremove - &7Remove block, the player is looking at, to the limited block list."));
//        if (sender.hasPermission(Permissions.EDIT_BANNED_ITEM)) contents.add(plugin.fromLegacy("&3/restrict &bedit (option) (value) - &7List options for a banned item or edit an option."));
        //if (sender.hasPermission(Permissions.SEARCH_WORLD)) contents.add(plugin.fromLegacy("&3/restrict &bsearch (itemID) - &7Search active chunks for a block"));
//        if (sender.hasPermission(Permissions.LIST_BANNED_ITEMS)) contents.add(plugin.fromLegacy("&3/restrict &blist &6| &3/banneditems &b- &7List all current banned items"));

        if (contents.isEmpty()) {
            contents.add(plugin.fromLegacy("&cYou currently do not have any permissions for this plugin."));
        }
        paginationService.builder()
                .title(plugin.fromLegacy("&6PlaceRestrict Help"))
                .contents(contents)
                .header(plugin.fromLegacy("&3[] = required  () = optional"))
                .padding(Text.of("="))
                .sendTo(sender);
    }
}
