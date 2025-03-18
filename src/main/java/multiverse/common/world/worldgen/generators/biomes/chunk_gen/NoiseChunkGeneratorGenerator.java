package multiverse.common.world.worldgen.generators.biomes.chunk_gen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import multiverse.common.world.worldgen.MultiverseNoiseChunkGenerator;
import multiverse.common.world.worldgen.MultiverseType;
import multiverse.common.world.worldgen.generators.biomes.chunk_gen.biome_source.BiomeSourceGenerator;
import multiverse.common.world.worldgen.generators.biomes.chunk_gen.noise_settings.BiomeNoiseGeneratorSettingsGenerator;
import multiverse.common.world.worldgen.generators.biomes.chunk_gen.sea.SeaLevelGenerator;
import multiverse.common.world.worldgen.sea.SerializableFluidPicker;
import multiverse.registration.custom.generator.biomes.BiomeChunkGeneratorGeneratorTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public class NoiseChunkGeneratorGenerator implements BiomeChunkGeneratorGenerator<MultiverseNoiseChunkGenerator> {

    public static final Codec<NoiseChunkGeneratorGenerator> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            SeaLevelGenerator.CODEC.fieldOf("sea_level").forGetter(val -> val.sea),
            BiomeNoiseGeneratorSettingsGenerator.CODEC.fieldOf("noise_settings").forGetter(val -> val.noise),
            BiomeSourceGenerator.CODEC.fieldOf("biomes").forGetter(val -> val.biomes)
    ).apply(inst, NoiseChunkGeneratorGenerator::new));
    private final Holder<BiomeNoiseGeneratorSettingsGenerator> noise;
    private final Holder<SeaLevelGenerator> sea;
    private final BiomeSourceGenerator<?> biomes;

    public NoiseChunkGeneratorGenerator(Holder<SeaLevelGenerator> sea, Holder<BiomeNoiseGeneratorSettingsGenerator> noise, BiomeSourceGenerator<?> biomes) {
        this.noise = noise;
        this.sea = sea;
        this.biomes = biomes;
    }

    @Override
    public MultiverseNoiseChunkGenerator generate(RegistryAccess access, long seed, RandomSource random, MultiverseType type, HolderSet<Biome> biomes) {
        BiomeSource source = this.biomes.generate(access, seed, random, type, biomes);
        Holder<NoiseGeneratorSettings> noise = this.noise.value().generate(access, seed, random, type, biomes);
        SerializableFluidPicker fluid = sea.value().getSeaLevel(noise.value().defaultFluid(), random);
        return new MultiverseNoiseChunkGenerator(
                access.registryOrThrow(Registry.STRUCTURE_SET_REGISTRY),
                access.registryOrThrow(Registry.NOISE_REGISTRY),
                source,
                noise,
                fluid
        );
    }

    @Override
    public Codec<? extends NoiseChunkGeneratorGenerator> getCodec() {
        return BiomeChunkGeneratorGeneratorTypeRegistry.NOISE.get();
    }

}
