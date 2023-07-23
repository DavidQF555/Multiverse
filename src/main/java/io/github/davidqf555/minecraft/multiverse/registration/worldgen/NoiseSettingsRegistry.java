package io.github.davidqf555.minecraft.multiverse.registration.worldgen;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.IMultiverseNoiseGeneratorSettings;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseChunkGenerator;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseShape;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseType;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes.MultiverseBiomeSource;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.dynamic.DynamicDefaultChunkGenerator;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
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
            for (MultiverseShape type : MultiverseShape.values()) {
                for (MultiverseType biome : MultiverseType.values()) {
                    NoiseGeneratorSettings settings = type.createNoiseSettings(biome);
                    ((IMultiverseNoiseGeneratorSettings) (Object) settings).setSettings(type, biome);
                    Registry.register(BuiltinRegistries.NOISE_GENERATOR_SETTINGS, type.getNoiseSettingsKey(biome), settings);
                }
            }
            Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation(Multiverse.MOD_ID, "dynamic_default"), DynamicDefaultChunkGenerator.CODEC);
            Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation(Multiverse.MOD_ID, "multiverse"), MultiverseChunkGenerator.CODEC);
            Registry.register(Registry.BIOME_SOURCE, new ResourceLocation(Multiverse.MOD_ID, "multiverse"), MultiverseBiomeSource.CODEC);
        });
    }

}
