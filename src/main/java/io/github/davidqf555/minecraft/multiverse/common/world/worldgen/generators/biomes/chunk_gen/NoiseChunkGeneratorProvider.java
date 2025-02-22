package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.chunk_gen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.MultiverseNoiseChunkGenerator;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.MultiverseType;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.chunk_gen.biome_source.BiomeSourceGenerator;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.chunk_gen.noise_settings.BiomeNoiseGeneratorSettingsGenerator;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.chunk_gen.sea_level.SeaLevelGenerator;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.chunk_gen.sea_level.fluid_pickers.SerializableFluidPicker;
import io.github.davidqf555.minecraft.multiverse.registration.custom.biomes.BiomeChunkGeneratorProviderTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public class NoiseChunkGeneratorProvider implements BiomeChunkGeneratorProvider<MultiverseNoiseChunkGenerator> {

    public static final Codec<NoiseChunkGeneratorProvider> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            SeaLevelGenerator.CODEC.fieldOf("sea_level").forGetter(val -> val.sea),
            BiomeNoiseGeneratorSettingsGenerator.CODEC.fieldOf("noise_settings").forGetter(val -> val.noise),
            BiomeSourceGenerator.CODEC.fieldOf("biomes").forGetter(val -> val.biomes)
    ).apply(inst, NoiseChunkGeneratorProvider::new));
    private final Holder<BiomeNoiseGeneratorSettingsGenerator> noise;
    private final Holder<SeaLevelGenerator> sea;
    private final BiomeSourceGenerator<?> biomes;

    public NoiseChunkGeneratorProvider(Holder<SeaLevelGenerator> sea, Holder<BiomeNoiseGeneratorSettingsGenerator> noise, BiomeSourceGenerator<?> biomes) {
        this.noise = noise;
        this.sea = sea;
        this.biomes = biomes;
    }

    @Override
    public MultiverseNoiseChunkGenerator provide(RegistryAccess access, long seed, RandomSource random, MultiverseType type, HolderSet<Biome> biomes) {
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
