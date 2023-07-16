package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;

public enum MultiverseEffectType {

    NONE("none", BuiltinDimensionTypes.OVERWORLD_EFFECTS),
    FOG("fog", BuiltinDimensionTypes.NETHER_EFFECTS);

    private final String name;
    private final ResourceLocation effect;

    MultiverseEffectType(String name, ResourceLocation effect) {
        this.name = name;
        this.effect = effect;
    }

    public String getName() {
        return name;
    }

    public ResourceLocation getEffect() {
        return effect;
    }

}
