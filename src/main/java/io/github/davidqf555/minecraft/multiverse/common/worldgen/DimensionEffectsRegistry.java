package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DimensionEffectsRegistry {

    public static final Map<ResourceLocation, Integer> FOG = new HashMap<>();

    static {
        for (DyeColor color : DyeColor.values()) {
            FOG.put(new ResourceLocation(Multiverse.MOD_ID, "fog/" + color.getName()), color.getFireworkColor());
        }
    }

    private DimensionEffectsRegistry() {
    }

}
