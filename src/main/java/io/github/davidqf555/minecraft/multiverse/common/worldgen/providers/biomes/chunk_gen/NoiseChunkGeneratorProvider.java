package io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseNoiseChunkGenerator;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseType;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.biome_source.BiomeSourceProvider;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.noise_settings.BiomeNoiseGeneratorSettingsProvider;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.sea_level.SeaLevelProvider;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.sea_level.fluid_pickers.SerializableFluidPicker;
import io.github.davidqf555.minecraft.multiverse.registration.custom.biomes.BiomeChunkGeneratorProviderTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

import java.util.Set;

public class NoiseChunkGeneratorProvider implements BiomeChunkGeneratorProvider<MultiverseNoiseChunkGenerator> {

    public static final Codec<NoiseChunkGeneratorProvider> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            SeaLevelProvider.CODEC.fieldOf("sea_level").forGetter(val -> val.sea),
            BiomeNoiseGeneratorSettingsProvider.CODEC.fieldOf("noise_settings").forGetter(val -> val.noise),
            BiomeSourceProvider.CODEC.fieldOf("biomes").forGetter(val -> val.biomes)
    ).apply(inst, NoiseChunkGeneratorProvider::new));
    private final Holder<BiomeNoiseGeneratorSettingsProvider> noise;
    private final Holder<SeaLevelProvider> sea;
    private final BiomeSourceProvider<?> biomes;

    public NoiseChunkGeneratorProvider(Holder<SeaLevelProvider> sea, Holder<BiomeNoiseGeneratorSettingsProvider> noise, BiomeSourceProvider<?> biomes) {
        this.noise = noise;
        this.sea = sea;
        this.biomes = biomes;
    }

    @Override
    public MultiverseNoiseChunkGenerator provide(RegistryAccess access, long seed, RandomSource random, MultiverseType type, Set<ResourceKey<Biome>> biomes) {
        BiomeSource source = this.biomes.provide(access, seed, random, type, biomes);
        Holder<NoiseGeneratorSettings> noise = this.noise.value().provide(access, seed, random, type, biomes);
        SerializableFluidPicker fluid = sea.value().getSeaLevel(noise.value().defaultFluid(), random);
        return new MultiverseNoiseChunkGenerator(
                source,
                noise,
                fluid
        );
    }

    @Override
    public Codec<? extends NoiseChunkGeneratorProvider> getCodec() {
        return BiomeChunkGeneratorProviderTypeRegistry.NOISE.get();
    }

}
