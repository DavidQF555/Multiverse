package multiverse.common.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public record DimensionsList(ListOperation operation, Set<ResourceLocation> values) {

    public static final Codec<DimensionsList> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ListOperation.CODEC.optionalFieldOf("type", ListOperation.WHITELIST).forGetter(DimensionsList::operation),
            ResourceLocation.CODEC.listOf().xmap(Set::copyOf, List::copyOf).optionalFieldOf("values", Set.of()).forGetter(DimensionsList::values)
    ).apply(inst, DimensionsList::new));

    public enum ListOperation implements StringRepresentable {

        WHITELIST("whitelist"),
        BLACKLIST("blacklist");

        public static final Codec<ListOperation> CODEC = Codec.STRING.xmap(ListOperation::byName, ListOperation::getSerializedName);
        private final String name;

        ListOperation(String name) {
            this.name = name;
        }

        @Nullable
        public static ListOperation byName(String name) {
            for (ListOperation type : values()) {
                if (type.getSerializedName().equals(name)) {
                    return type;
                }
            }
            return null;
        }

        @Override
        public String getSerializedName() {
            return name;
        }

    }

}