package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import com.google.common.collect.ImmutableList;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DimensionEffectsRegistry {

    public static final Map<ResourceLocation, Integer> FOG = new HashMap<>();
    private static final List<MultiverseEffectType> EFFECTS;

    static {
        ImmutableList.Builder<MultiverseEffectType> builder = ImmutableList.builder();
        for (DyeColor color : DyeColor.values()) {
            FOG.put(new ResourceLocation(Multiverse.MOD_ID, "fog/" + color.getName()), color.getFireworkColor());
        }
        builder.add(new MultiverseEffectType(16, false, BuiltinDimensionTypes.OVERWORLD_EFFECTS));
        builder.add(new MultiverseEffectType(4, true, BuiltinDimensionTypes.END_EFFECTS));
        for (ResourceLocation key : FOG.keySet()) {
            builder.add(new MultiverseEffectType(1, false, key));
        }
        EFFECTS = builder.build();
    }

    private DimensionEffectsRegistry() {
    }

    public static List<MultiverseEffectType> getEffects() {
        return EFFECTS;
    }

}
