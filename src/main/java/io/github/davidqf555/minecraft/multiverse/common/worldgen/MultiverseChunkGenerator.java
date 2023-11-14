package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.aquifers.SerializableFluidPicker;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.shapes.MultiverseShapeType;
import io.github.davidqf555.minecraft.multiverse.registration.worldgen.ChunkGeneratorRegistry;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

import java.util.function.Supplier;

public class MultiverseChunkGenerator extends NoiseBasedChunkGenerator {

    public static final Supplier<Codec<MultiverseChunkGenerator>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(p_255585_ -> p_255585_.group(
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(p_255584_ -> p_255584_.biomeSource),
            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(p_224278_ -> p_224278_.settings),
            Codec.INT.xmap(i -> MultiverseShapeType.values()[i], Enum::ordinal).fieldOf("shape").forGetter(gen -> gen.shape),
            SerializableFluidPicker.CODEC.get().fieldOf("fluid").forGetter(gen -> gen.fluid)
    ).apply(p_255585_, p_255585_.stable(MultiverseChunkGenerator::new))));

    private final SerializableFluidPicker fluid;
    private final MultiverseShapeType shape;

    public MultiverseChunkGenerator(BiomeSource p_209108_, Holder<NoiseGeneratorSettings> p_209110_, MultiverseShapeType shape, SerializableFluidPicker fluid) {
        super(p_209108_, p_209110_);
        this.shape = shape;
        globalFluidPicker = () -> fluid;
        this.fluid = fluid;
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return ChunkGeneratorRegistry.MULTIVERSE.get();
    }

}
