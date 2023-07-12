package io.github.davidqf555.minecraft.multiverse.registration.worldgen;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseShape;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseType;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.dynamic.DynamicDefaultChunkGenerator;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
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
                    Registry.register(BuiltinRegistries.NOISE_GENERATOR_SETTINGS, type.getNoiseSettingsKey(biome), type.createNoiseSettings(biome));
                }
            }
            Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation(Multiverse.MOD_ID, "dynamic_default"), DynamicDefaultChunkGenerator.CODEC);
        });
    }

}
