package io.github.prokopiy.mcplacelimiter.data;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.spongepowered.api.Sponge;

import java.util.List;

public class GroupData {
    protected String groupname;
    protected Integer grouplimit;

    public GroupData(String groupname, Integer grouplimit) {
        this.groupname = groupname.toLowerCase();
        this.grouplimit = grouplimit;
    }


    public static class GroupDataSerializer implements TypeSerializer<GroupData> {
        @SuppressWarnings("serial")
        final public static TypeToken<List<GroupData>> token = new TypeToken<List<GroupData>>() {};

        @Override
        public GroupData deserialize(TypeToken<?> token, ConfigurationNode node) throws ObjectMappingException {
            return new GroupData(
                    node.getNode("groupname").getString(),
                    node.getNode("grouplimit").getInt());
        }

        @Override
        public void serialize(TypeToken<?> token, GroupData groupdata, ConfigurationNode node) throws ObjectMappingException {
            node.getNode("groupname").setValue(groupdata.groupname);
            node.getNode("grouplimit").setValue(groupdata.grouplimit);
        }
    }



    public String getGroupName() {
        return groupname;
    }

    public Integer getGroupLimit() {
        return grouplimit;
    }


}
