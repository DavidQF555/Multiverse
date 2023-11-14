package io.github.davidqf555.minecraft.multiverse.common.worldgen.dimension_types.time;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

public class MultiverseTime {

    public static final Codec<MultiverseTime> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.xmap(MultiverseTimeType::byName, MultiverseTimeType::getName).fieldOf("type").forGetter(MultiverseTime::getType),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("weight", 1).forGetter(MultiverseTime::getWeight)
    ).apply(inst, MultiverseTime::new));
    private final MultiverseTimeType type;
    private final int weight;

    public MultiverseTime(MultiverseTimeType type, int weight) {
        this.type = type;
        this.weight = weight;
    }

    public MultiverseTimeType getType() {
        return type;
    }

    public int getWeight() {
        return weight;
    }

}
