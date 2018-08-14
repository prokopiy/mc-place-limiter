package io.github.prokopiy.mcplacelimiter.data;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.util.List;

public class BlockData {
    protected String blockid, blockgroup;

    public BlockData(String blockid, String blockgroup) {
        this.blockid = blockid;
//        this.itemname = itemname;
        this.blockgroup = blockgroup;
    }


    public static class BlockDataSerializer implements TypeSerializer<BlockData> {
        @SuppressWarnings("serial")
        final public static TypeToken<List<BlockData>> token = new TypeToken<List<BlockData>>() {};

        @Override
        public BlockData deserialize(TypeToken<?> token, ConfigurationNode node) throws ObjectMappingException {
            return new BlockData(
                    node.getNode("blockid").getString(),
//                    node.getNode("itemname").getString(),
                    node.getNode("blockgroup").getString());
        }

        @Override
        public void serialize(TypeToken<?> token, BlockData itemdata, ConfigurationNode node) throws ObjectMappingException {
            node.getNode("blockid").setValue(itemdata.blockid);
//            node.getNode("itemname").setValue(itemdata.itemname);
            node.getNode("blockgroup").setValue(itemdata.blockgroup);
        }
    }



//    public String getItemname() {
//        return itemname;
//    }

    public String getBlockid() {
        return blockid;
    }

    public String getBlockGroup() {
        return blockgroup;
    }
}
