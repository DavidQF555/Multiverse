package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.sea_level.fluid_pickers.SerializableFluidPicker;
import io.github.davidqf555.minecraft.multiverse.registration.worldgen.ChunkGeneratorRegistry;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public class MultiverseNoiseChunkGenerator extends NoiseBasedChunkGenerator {

    public static final Codec<MultiverseNoiseChunkGenerator> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(p_188711_ -> p_188711_.biomeSource),
            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(p_204585_ -> p_204585_.settings),
            SerializableFluidPicker.CODEC.fieldOf("fluid").forGetter(val -> val.fluid)
    ).apply(inst, inst.stable(MultiverseNoiseChunkGenerator::new)));

    private final SerializableFluidPicker fluid;

    public MultiverseNoiseChunkGenerator(BiomeSource p_209108_, Holder<NoiseGeneratorSettings> p_209110_, SerializableFluidPicker fluid) {
        super(p_209108_, p_209110_);
        this.fluid = fluid;
        globalFluidPicker = () -> fluid;
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return ChunkGeneratorRegistry.MULTIVERSE.get();
    }

}
