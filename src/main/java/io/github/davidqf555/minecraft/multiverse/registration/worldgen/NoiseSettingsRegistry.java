package io.github.davidqf555.minecraft.multiverse.registration.worldgen;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class NoiseSettingsRegistry {

    private NoiseSettingsRegistry() {
    }

    @SubscribeEvent
    public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            //Registry.register(Registries.CHUNK_GENERATOR, new ResourceLocation(Multiverse.MOD_ID, "dynamic_default"), DynamicDefaultChunkGenerator.CODEC);
        });
    }

}
