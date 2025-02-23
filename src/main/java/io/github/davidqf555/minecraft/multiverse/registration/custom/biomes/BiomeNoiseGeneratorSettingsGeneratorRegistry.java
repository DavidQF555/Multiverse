package io.github.davidqf555.minecraft.multiverse.registration.custom.biomes;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.chunk_gen.noise_settings.BiomeNoiseGeneratorSettingsGenerator;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@EventBusSubscriber(modid = Multiverse.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class BiomeNoiseGeneratorSettingsGeneratorRegistry {

    public static final ResourceKey<Registry<BiomeNoiseGeneratorSettingsGenerator>> LOCATION = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "noise_generator_settings_generator"));

    private BiomeNoiseGeneratorSettingsGeneratorRegistry() {
    }

    @SubscribeEvent
    public static void onNewDataPackRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(LOCATION, BiomeNoiseGeneratorSettingsGenerator.DIRECT_CODEC);
    }

}
