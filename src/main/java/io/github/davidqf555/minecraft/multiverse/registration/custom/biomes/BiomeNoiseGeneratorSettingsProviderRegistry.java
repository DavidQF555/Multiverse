package io.github.davidqf555.minecraft.multiverse.registration.custom.biomes;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.noise_settings.BiomeNoiseGeneratorSettingsProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class BiomeNoiseGeneratorSettingsProviderRegistry {

    public static final ResourceKey<Registry<BiomeNoiseGeneratorSettingsProvider>> LOCATION = ResourceKey.createRegistryKey(new ResourceLocation(Multiverse.MOD_ID, "noise_generator_settings_provider"));

    private BiomeNoiseGeneratorSettingsProviderRegistry() {
    }

    @SubscribeEvent
    public static void onNewDataPackRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(LOCATION, BiomeNoiseGeneratorSettingsProvider.DIRECT_CODEC);
    }

}
