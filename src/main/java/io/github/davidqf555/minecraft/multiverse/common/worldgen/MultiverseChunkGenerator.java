package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public class MultiverseChunkGenerator extends NoiseBasedChunkGenerator {

    public static final Codec<MultiverseChunkGenerator> CODEC = RecordCodecBuilder.create(p_255585_ -> p_255585_.group(
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(p_255584_ -> p_255584_.biomeSource),
            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(p_224278_ -> p_224278_.settings),
            Codec.LONG.fieldOf("seed").forGetter(gen -> gen.seed),
            Codec.INT.xmap(i -> MultiverseShape.values()[i], Enum::ordinal).fieldOf("shape").forGetter(gen -> gen.shape),
            Codec.INT.fieldOf("index").forGetter(gen -> gen.index)
    ).apply(p_255585_, p_255585_.stable(MultiverseChunkGenerator::new)));

    private final long seed;
    private final int index;
    private final MultiverseShape shape;

    public MultiverseChunkGenerator(BiomeSource p_209108_, Holder<NoiseGeneratorSettings> p_209110_, long seed, MultiverseShape shape, int index) {
        super(p_209108_, p_209110_);
        this.index = index;
        this.shape = shape;
        this.seed = seed;
        globalFluidPicker = Suppliers.memoize(() -> shape.getSea(p_209110_.value().defaultFluid(), seed, index));
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

}
