package io.github.prokopiy.mcplacelimiter;

import io.github.prokopiy.mcplacelimiter.commands.*;
import io.github.prokopiy.mcplacelimiter.data.*;
import io.github.prokopiy.mcplacelimiter.data.BlockData.BlockDataSerializer;
import io.github.prokopiy.mcplacelimiter.data.GroupData.GroupDataSerializer;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Plugin(
        id = "mc-place-limiter",
        name = "MC Place Limiter",
        description = "Place Limiter",
        authors = {
                "Prokopiy N. Stelmash"
        }
)
public class Main {

    private static Main instance;

    @Inject
    private Logger logger;

    @Inject
    @DefaultConfig(sharedRoot = false)
    public Path defaultConf;

    @Inject
    @org.spongepowered.api.config.ConfigDir(sharedRoot = false)
    public Path ConfigDir;

    private CommandManager cmdManager = Sponge.getCommandManager();

    private Map<String, BlockData> blocks;
    private Map<String, GroupData> groups;

    private Config config;


    @Listener
    public void Init(GameInitializationEvent event) throws IOException, ObjectMappingException {
        instance = this;
        this.config = new Config(this);
        Sponge.getEventManager().registerListeners(this, new EventListener(this));

        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(GroupData.class), new GroupDataSerializer());
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(BlockData.class), new BlockData.BlockDataSerializer());

        loadCommands(); logger.info("Load commands...");
        loadData(); logger.info("Load data...");
    }


    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        logger.info("Started!");
    }



    private void loadCommands() {

        // /placerestrict whatsthis
        CommandSpec whatsThis = CommandSpec.builder()
                .description(Text.of("Show the block ID the player is looking at"))
                .executor(new Whatsthis(this))
                .permission(Permissions.WHATS_THIS)
                .build();

        // /placerestrict addgroup
        CommandSpec groupAdd = CommandSpec.builder()
                .description(Text.of("Add group"))
                .executor(new AddGroup(this))
                .arguments(GenericArguments.optional(GenericArguments.string(Text.of("GroupName"))),
                        GenericArguments.optional(GenericArguments.integer(Text.of("GroupLimit"))))
                .permission(Permissions.ADD_GROUP)
                .build();

        // /placerestrict removegroup
        CommandSpec groupRemove = CommandSpec.builder()
                .description(Text.of("Remove group and all block in this"))
                .executor(new RemoveGroup(this))
                .arguments(GenericArguments.string(Text.of("GroupName")))
                .permission(Permissions.REMOVE_GROUP)
                .build();

        // /placerestrict updategroup
        CommandSpec groupUpdate = CommandSpec.builder()
                .description(Text.of("Update group limit"))
                .executor(new UpdateGroup(this))
                .arguments(GenericArguments.optional(GenericArguments.string(Text.of("GroupName"))),
                        GenericArguments.optional(GenericArguments.integer(Text.of("GroupLimit"))))
                .permission(Permissions.UPDATE_GROUP)
                .build();

        // /placerestrict group info
        CommandSpec groupInfo = CommandSpec.builder()
                .description(Text.of("Info group limit"))
                .executor(new InfoGroup(this))
                .arguments(GenericArguments.optional(GenericArguments.string(Text.of("GroupName"))))
                .permission(Permissions.INFO_GROUP)
                .build();

        // /placerestrict add
        CommandSpec blockAddLookAt = CommandSpec.builder()
                .description(Text.of("Add the block, the player is looking at, to the group of limited blocks"))
                .executor(new LookAt(this))
                .arguments(GenericArguments.string(Text.of("GroupName")))
                .permission(Permissions.ADD_BLOCK)
                .build();


        // /placerestrict remove
        CommandSpec blockRemove = CommandSpec.builder()
                .description(Text.of("Remove limited block"))
                .executor(new RemoveBlock(this))
                .arguments(GenericArguments.string(Text.of("BlockId")))
                .permission(Permissions.REMOVE_BLOCK)
                .build();


        // /placerestrict block
        CommandSpec block = CommandSpec.builder()
                .description(Text.of("Base placerestrict block command"))
                .executor(new Help(this))
                .child(blockAddLookAt, "add")
                .child(blockRemove, "remove")
                .build();

        // /placerestrict group
        CommandSpec group = CommandSpec.builder()
                .description(Text.of("Base placerestrict block command"))
                .executor(new Help(this))
                .child(groupAdd, "add")
                .child(groupRemove, "remove")
                .child(groupUpdate, "update")
                .child(groupInfo, "info")
                .build();


        // /placerestrict
        CommandSpec placerestrict = CommandSpec.builder()
                .description(Text.of("Base placerestrict command"))
                .executor(new Help(this))
                .child(whatsThis, "whatsthis")
                .child(block, "block")
                .child(group, "group")
                .build();

//        cmdManager.register(this, bannedList, "banneditems");
//        cmdManager.register(this, whatsThis, "whatsthis");
//        cmdManager.register(this, itemAddLookAt, "itemAddLookAt");
//        cmdManager.register(this, itemAddHand, "itemAddHand");
        cmdManager.register(this, placerestrict, "placerestrict");
    }


    public HoconConfigurationLoader getDataLoader() {
        return HoconConfigurationLoader.builder().setPath(this.ConfigDir.resolve("data.conf")).build();
    }

    private void loadData() throws IOException, ObjectMappingException {
        HoconConfigurationLoader loader = getDataLoader();
        ConfigurationNode rootNode = loader.load();
        List<GroupData> groupList = rootNode.getNode("Groups").getList(TypeToken.of(GroupData.class));
        this.groups = new HashMap<String, GroupData>();
        for (GroupData i : groupList) {
            this.groups.put(i.getGroupName(), i);
        }
        List<BlockData> blockList = rootNode.getNode("Blocks").getList(TypeToken.of(BlockData.class));
        this.blocks = new HashMap<String, BlockData>();
        for (BlockData i : blockList) {
            this.blocks.put(i.getBlockid(), i);
        }
    }

    public void saveData() throws IOException, ObjectMappingException {
        HoconConfigurationLoader loader = getDataLoader();
        ConfigurationNode rootNode = loader.load();
            rootNode.getNode("Groups").setValue(GroupDataSerializer.token, new ArrayList<GroupData>(this.groups.values()));
            rootNode.getNode("Blocks").setValue(BlockDataSerializer.token, new ArrayList<BlockData>(this.blocks.values()));
        loader.save(rootNode);
    }


    public Collection<BlockData> getBlocksData() {
        return Collections.unmodifiableCollection(this.blocks.values());
    }
    public Collection<GroupData> getGroupsData() {
        return Collections.unmodifiableCollection(this.groups.values());
    }


    public BlockData removeBlockById(String id) {
        return this.blocks.remove(id);
    }

    public BlockData removeAllBlocksByGroup(String groupname) {
        final List<BlockData> list = new ArrayList<BlockData>(getBlocksData());
        BlockData last = null;
        for (BlockData i : list) {
            if (i.getBlockGroup().equalsIgnoreCase(groupname)) {
                last = this.removeBlockById(i.getBlockid());
            }
        }
        return last;
    }

    public GroupData removeGroupByName(String name) {
        removeAllBlocksByGroup(name);
        return this.groups.remove(name);
    }


    public BlockData addBlock(BlockData item) {
        return this.blocks.put(item.getBlockid(), item);
    }

    public GroupData addGroup(GroupData group) {
        return this.groups.put(group.getGroupName(), group);
    }


    public String getBlockGroup(String id) {
        final List<BlockData> list = new ArrayList<BlockData>(getBlocksData());
        for (BlockData i : list) {
            if (i.getBlockid().equalsIgnoreCase(id)) {
                return i.getBlockGroup();
            }
        }
        return null;
    }

    public boolean groupNameExists(String name) {
        final List<GroupData> list = new ArrayList<GroupData>(getGroupsData());

        for (GroupData i : list) {
            if (i.getGroupName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public Integer getGroupLimit(String name) {
        final List<GroupData> list = new ArrayList<GroupData>(getGroupsData());
        for (GroupData i : list) {
            if (i.getGroupName().equalsIgnoreCase(name)) {
                return i.getGroupLimit();
            }
        }
        return null;
    }


    public boolean checkPerm(CommandSource src, String banType, String itemID) {
        if (!src.hasPermission(Permissions.BYPASS_LIMITED_BLOCK + "." + banType + "." + itemID.replace(":", "."))) {
            return true;
        } else {
            return false;
        }
    }



    public String getLocationID(Location<World> location) {
        if (location.getTileEntity().isPresent()) {
            return location.getTileEntity().get().getType().getId().toLowerCase();
        } else {
            String itemID = location.getBlockType().getName().toLowerCase();
            return itemID;
        }


    }

    public Logger getLogger() {
        return logger;
    }

    public void logToFile(String filename, String message) {
        if (Config.logToFile) {
            try {
                if (!Files.exists(ConfigDir.resolve("logs"))) {
                    Files.createDirectory(ConfigDir.resolve("logs"));
                }
                Path saveTo = ConfigDir.resolve("logs/" + filename + ".txt");
                if (!Files.exists(saveTo)) {
                    Files.createFile(saveTo);
                }
                FileWriter fw = new FileWriter(saveTo.toFile(), true);
                PrintWriter pw = new PrintWriter(fw);
                pw.println(message);
                pw.flush();
                pw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }



    public Text fromLegacy(String legacy) {
        return TextSerializers.FORMATTING_CODE.deserializeUnchecked(legacy);
    }
}
