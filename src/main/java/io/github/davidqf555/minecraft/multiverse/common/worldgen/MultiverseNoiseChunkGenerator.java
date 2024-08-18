package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.sea_level.fluid_pickers.SerializableFluidPicker;
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

public class MultiverseNoiseChunkGenerator extends NoiseBasedChunkGenerator {

    public static final Codec<MultiverseNoiseChunkGenerator> CODEC = RecordCodecBuilder.create(p_188643_ -> commonCodec(p_188643_).and(p_188643_.group(
            RegistryOps.retrieveRegistry(Registry.NOISE_REGISTRY).forGetter(p_188716_ -> p_188716_.noises),
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(p_188711_ -> p_188711_.biomeSource),
            Codec.LONG.fieldOf("seed").stable().forGetter(p_188690_ -> p_188690_.seed),
            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(p_204585_ -> p_204585_.settings),
            SerializableFluidPicker.CODEC.fieldOf("fluid").forGetter(val -> val.fluid)
    )).apply(p_188643_, p_188643_.stable(MultiverseNoiseChunkGenerator::new)));

    private final SerializableFluidPicker fluid;

    public MultiverseNoiseChunkGenerator(Registry<StructureSet> p_209106_, Registry<NormalNoise.NoiseParameters> p_209107_, BiomeSource p_209108_, long p_209109_, Holder<NoiseGeneratorSettings> p_209110_, SerializableFluidPicker fluid) {
        super(p_209106_, p_209107_, p_209108_, p_209109_, p_209110_);
        this.fluid = fluid;
        globalFluidPicker = fluid;
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return ChunkGeneratorRegistry.MULTIVERSE.get();
    }

}
