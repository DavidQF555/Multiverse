package io.github.davidqf555.minecraft.multiverse.common.world.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.providers.biomes.chunk_gen.sea_level.fluid_pickers.SerializableFluidPicker;
import io.github.davidqf555.minecraft.multiverse.registration.worldgen.ChunkGeneratorRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import javax.annotation.Nonnull;

public class MultiverseNoiseChunkGenerator extends NoiseBasedChunkGenerator {

    public static final Codec<MultiverseNoiseChunkGenerator> CODEC = RecordCodecBuilder.create(p_224323_ -> commonCodec(p_224323_).and(p_224323_.group(
            RegistryOps.retrieveRegistry(Registry.NOISE_REGISTRY).forGetter(p_188716_ -> p_188716_.noises),
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource),
            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter((p_224278_) -> p_224278_.settings),
            SerializableFluidPicker.CODEC.fieldOf("fluid").forGetter(val -> val.fluid)
    )).apply(p_224323_, p_224323_.stable(MultiverseNoiseChunkGenerator::new)));
    private final SerializableFluidPicker fluid;

    public MultiverseNoiseChunkGenerator(Registry<StructureSet> p_224206_, Registry<NormalNoise.NoiseParameters> p_224207_, BiomeSource p_224208_, Holder<NoiseGeneratorSettings> p_224209_, SerializableFluidPicker fluid) {
        super(p_224206_, p_224207_, p_224208_, p_224209_);
        this.fluid = fluid;
        globalFluidPicker = fluid;
    }

    @Nonnull
    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return ChunkGeneratorRegistry.MULTIVERSE.get();
    }

}
