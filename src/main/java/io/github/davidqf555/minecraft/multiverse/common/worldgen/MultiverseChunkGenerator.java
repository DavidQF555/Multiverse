package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.aquifers.SerializableFluidPicker;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.function.Supplier;

public class MultiverseChunkGenerator extends NoiseBasedChunkGenerator {

    public static final Supplier<Codec<MultiverseChunkGenerator>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(p_188643_ -> commonCodec(p_188643_).and(p_188643_.group(
            RegistryOps.retrieveRegistry(Registry.NOISE_REGISTRY).forGetter(p_188716_ -> p_188716_.noises),
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(p_188711_ -> p_188711_.biomeSource),
            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(p_204585_ -> p_204585_.settings),
            Codec.INT.xmap(i -> MultiverseShape.values()[i], Enum::ordinal).fieldOf("shape").forGetter(gen -> gen.shape),
            SerializableFluidPicker.CODEC.get().fieldOf("fluid").forGetter(gen -> gen.fluid)
    )).apply(p_188643_, p_188643_.stable(MultiverseChunkGenerator::new))));

    private final SerializableFluidPicker fluid;
    private final MultiverseShape shape;

    public MultiverseChunkGenerator(Registry<StructureSet> p_209106_, Registry<NormalNoise.NoiseParameters> p_209107_, BiomeSource p_209108_, Holder<NoiseGeneratorSettings> p_209110_, MultiverseShape shape, SerializableFluidPicker fluid) {
        super(p_209106_, p_209107_, p_209108_, p_209110_);
        this.fluid = fluid;
        this.shape = shape;
        globalFluidPicker = fluid;
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC.get();
    }

}
