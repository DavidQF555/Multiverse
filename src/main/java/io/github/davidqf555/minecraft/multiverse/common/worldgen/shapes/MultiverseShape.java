package io.github.davidqf555.minecraft.multiverse.common.worldgen.shapes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

public class MultiverseShape {

    public static final Codec<MultiverseShape> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.xmap(MultiverseShapeType::byName, MultiverseShapeType::getName).fieldOf("type").forGetter(MultiverseShape::getType),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("weight", 1).forGetter(MultiverseShape::getWeight)
    ).apply(inst, MultiverseShape::new));
    private final MultiverseShapeType type;
    private final int weight;

    public MultiverseShape(MultiverseShapeType type, int weight) {
        this.type = type;
        this.weight = weight;
    }

    public MultiverseShapeType getType() {
        return type;
    }

    public int getWeight() {
        return weight;
    }

}
