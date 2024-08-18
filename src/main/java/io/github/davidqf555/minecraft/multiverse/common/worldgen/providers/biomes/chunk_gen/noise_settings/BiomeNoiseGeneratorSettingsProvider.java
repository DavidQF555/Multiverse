package io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.noise_settings;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.BiomeFieldProvider;
import io.github.davidqf555.minecraft.multiverse.registration.custom.biomes.BiomeNoiseGeneratorSettingsProviderRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.custom.biomes.BiomeNoiseGeneratorSettingsProviderTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class BiomeNoiseGeneratorSettingsProvider extends ForgeRegistryEntry<BiomeNoiseGeneratorSettingsProvider> implements BiomeFieldProvider<Holder<NoiseGeneratorSettings>> {

    public static final Codec<BiomeNoiseGeneratorSettingsProvider> DIRECT_CODEC = ExtraCodecs.lazyInitializedCodec(() -> BiomeNoiseGeneratorSettingsProviderTypeRegistry.getRegistry().getCodec().dispatch(BiomeNoiseGeneratorSettingsProvider::getType, BiomeNoiseGeneratorSettingsProviderType::getCodec));
    public static final Codec<Holder<BiomeNoiseGeneratorSettingsProvider>> CODEC = RegistryFileCodec.create(BiomeNoiseGeneratorSettingsProviderRegistry.LOCATION, DIRECT_CODEC);

    public abstract BiomeNoiseGeneratorSettingsProviderType<?> getType();

}
