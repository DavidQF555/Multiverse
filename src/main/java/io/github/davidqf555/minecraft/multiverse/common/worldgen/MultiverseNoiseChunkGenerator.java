package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.sea_level.fluid_pickers.SerializableFluidPicker;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public class MultiverseNoiseChunkGenerator extends NoiseBasedChunkGenerator {

    public static final MapCodec<MultiverseNoiseChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
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

}
