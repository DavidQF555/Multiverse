package io.github.davidqf555.minecraft.multiverse.common.worldgen.effects;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

import java.util.HashMap;
import java.util.Map;

public final class DimensionEffectsRegistry {

    public static final Map<DyeColor, ResourceLocation> FOG = new HashMap<>();

    static {
        for (DyeColor color : DyeColor.values()) {
            FOG.put(color, new ResourceLocation(Multiverse.MOD_ID, "fog/" + color.getName()));
        }
    }

    private DimensionEffectsRegistry() {
    }

}
