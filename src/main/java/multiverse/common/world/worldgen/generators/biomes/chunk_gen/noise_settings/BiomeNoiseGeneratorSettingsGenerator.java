package multiverse.common.world.worldgen.generators.biomes.chunk_gen.noise_settings;

import com.mojang.serialization.Codec;
import multiverse.common.world.worldgen.generators.biomes.BiomeFieldGenerator;
import multiverse.registration.custom.generator.biomes.BiomeNoiseGeneratorSettingsGeneratorRegistry;
import multiverse.registration.custom.generator.biomes.BiomeNoiseGeneratorSettingsGeneratorTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class BiomeNoiseGeneratorSettingsGenerator extends ForgeRegistryEntry<BiomeNoiseGeneratorSettingsGenerator> implements BiomeFieldGenerator<Holder<NoiseGeneratorSettings>> {

    public static final Codec<BiomeNoiseGeneratorSettingsGenerator> DIRECT_CODEC = ExtraCodecs.lazyInitializedCodec(() -> BiomeNoiseGeneratorSettingsGeneratorTypeRegistry.getRegistry().getCodec().dispatch(BiomeNoiseGeneratorSettingsGenerator::getType, BiomeNoiseGeneratorSettingsGeneratorType::getCodec));
    public static final Codec<Holder<BiomeNoiseGeneratorSettingsGenerator>> CODEC = RegistryFileCodec.create(BiomeNoiseGeneratorSettingsGeneratorRegistry.LOCATION, DIRECT_CODEC);

    public abstract BiomeNoiseGeneratorSettingsGeneratorType<?> getType();

}
