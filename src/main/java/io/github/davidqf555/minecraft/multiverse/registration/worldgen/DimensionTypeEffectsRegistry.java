package io.github.davidqf555.minecraft.multiverse.registration.worldgen;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseEffectType;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DimensionTypeEffectsRegistry {

    private DimensionTypeEffectsRegistry() {
    }

    @SubscribeEvent
    public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            MultiverseEffectType.register(new MultiverseEffectType(1, false, DimensionType.OVERWORLD_EFFECTS));
            MultiverseEffectType.register(new MultiverseEffectType(1, false, DimensionType.NETHER_EFFECTS));
            MultiverseEffectType.register(new MultiverseEffectType(1, true, DimensionType.END_EFFECTS));
        });
    }

}
