package io.github.davidqf555.minecraft.multiverse.common.worldgen.dimension_types.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

public class MultiverseEffect {

    public static final Codec<MultiverseEffect> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.xmap(MultiverseEffectType::byName, MultiverseEffectType::getName).fieldOf("type").forGetter(MultiverseEffect::getType),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("weight", 1).forGetter(MultiverseEffect::getWeight)
    ).apply(inst, MultiverseEffect::new));
    private final MultiverseEffectType type;
    private final int weight;

    public MultiverseEffect(MultiverseEffectType type, int weight) {
        this.type = type;
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    public MultiverseEffectType getType() {
        return type;
    }

}
