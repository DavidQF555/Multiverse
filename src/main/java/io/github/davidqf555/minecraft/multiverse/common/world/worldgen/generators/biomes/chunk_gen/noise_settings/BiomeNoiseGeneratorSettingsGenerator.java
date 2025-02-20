package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.chunk_gen.noise_settings;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.BiomeFieldGenerator;
import io.github.davidqf555.minecraft.multiverse.registration.custom.biomes.BiomeNoiseGeneratorSettingsProviderRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.custom.biomes.BiomeNoiseGeneratorSettingsProviderTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class BiomeNoiseGeneratorSettingsGenerator extends ForgeRegistryEntry<BiomeNoiseGeneratorSettingsGenerator> implements BiomeFieldGenerator<Holder<NoiseGeneratorSettings>> {

    public static final Codec<BiomeNoiseGeneratorSettingsGenerator> DIRECT_CODEC = ExtraCodecs.lazyInitializedCodec(() -> BiomeNoiseGeneratorSettingsProviderTypeRegistry.getRegistry().getCodec().dispatch(BiomeNoiseGeneratorSettingsGenerator::getType, BiomeNoiseGeneratorSettingsGeneratorType::getCodec));
    public static final Codec<Holder<BiomeNoiseGeneratorSettingsGenerator>> CODEC = RegistryFileCodec.create(BiomeNoiseGeneratorSettingsProviderRegistry.LOCATION, DIRECT_CODEC);

    public abstract BiomeNoiseGeneratorSettingsGeneratorType<?> getType();

}
