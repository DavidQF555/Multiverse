package io.github.davidqf555.minecraft.multiverse.registration.custom.biomes;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.chunk_gen.noise_settings.BiomeNoiseGeneratorSettingsGenerator;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class BiomeNoiseGeneratorSettingsGeneratorRegistry {

    public static final ResourceKey<Registry<BiomeNoiseGeneratorSettingsGenerator>> LOCATION = ResourceKey.createRegistryKey(new ResourceLocation(Multiverse.MOD_ID, "noise_generator_settings_generator"));
    private static Supplier<IForgeRegistry<BiomeNoiseGeneratorSettingsGenerator>> registry = null;

    private BiomeNoiseGeneratorSettingsGeneratorRegistry() {
    }

    public static IForgeRegistry<BiomeNoiseGeneratorSettingsGenerator> getRegistry() {
        return registry.get();
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<BiomeNoiseGeneratorSettingsGenerator>().setType(BiomeNoiseGeneratorSettingsGenerator.class).setName(LOCATION.location()).dataPackRegistry(BiomeNoiseGeneratorSettingsGenerator.DIRECT_CODEC));
    }

}
