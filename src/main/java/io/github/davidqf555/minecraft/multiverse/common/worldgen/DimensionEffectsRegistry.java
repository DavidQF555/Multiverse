package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DimensionEffectsRegistry {

    public static final Map<ResourceLocation, Integer> FOG = new HashMap<>();

    static {
        for (DyeColor color : DyeColor.values()) {
            FOG.put(new ResourceLocation(Multiverse.MOD_ID, color.getName() + "_fog"), color.getFireworkColor());
        }
    }

    private DimensionEffectsRegistry() {
    }

    @SubscribeEvent
    public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            MultiverseEffectType.register(new MultiverseEffectType(16, false, DimensionType.OVERWORLD_EFFECTS));
            MultiverseEffectType.register(new MultiverseEffectType(4, true, DimensionType.END_EFFECTS));
            for (ResourceLocation key : FOG.keySet()) {
                MultiverseEffectType.register(new MultiverseEffectType(1, false, key));
            }
        });
    }

}
