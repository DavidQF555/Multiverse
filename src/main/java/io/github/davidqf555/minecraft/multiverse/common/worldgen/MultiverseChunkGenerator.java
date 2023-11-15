package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

public class MultiverseChunkGenerator extends NoiseBasedChunkGenerator {

    public static final Codec<MultiverseChunkGenerator> CODEC = RecordCodecBuilder.create(p_188643_ -> commonCodec(p_188643_).and(p_188643_.group(
            RegistryOps.retrieveRegistry(Registry.NOISE_REGISTRY).forGetter(p_188716_ -> p_188716_.noises),
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(p_188711_ -> p_188711_.biomeSource),
            Codec.LONG.fieldOf("seed").stable().forGetter(p_188690_ -> p_188690_.seed),
            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(p_204585_ -> p_204585_.settings),
            Codec.INT.xmap(i -> MultiverseShape.values()[i], Enum::ordinal).fieldOf("shape").forGetter(gen -> gen.shape),
            Codec.INT.fieldOf("index").forGetter(gen -> gen.index)
    )).apply(p_188643_, p_188643_.stable(MultiverseChunkGenerator::new)));

    private final int index;
    private final long seed;
    private final MultiverseShape shape;

    public MultiverseChunkGenerator(Registry<StructureSet> p_209106_, Registry<NormalNoise.NoiseParameters> p_209107_, BiomeSource p_209108_, long p_209109_, Holder<NoiseGeneratorSettings> p_209110_, MultiverseShape shape, int index) {
        super(p_209106_, p_209107_, p_209108_, p_209110_);
        this.index = index;
        this.shape = shape;
        this.seed = p_209109_;
        globalFluidPicker = shape.getSea(p_209110_.value().defaultFluid(), p_209109_, index);
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return ChunkGeneratorRegistry.MULTIVERSE.get();
    }

}
