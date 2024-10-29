package io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.sea_level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.sea_level.fluid_pickers.SerializableFluidPicker;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.sea_level.fluid_pickers.WaveFluidPicker;
import io.github.davidqf555.minecraft.multiverse.registration.custom.SeaLevelProviderTypeRegistry;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class WaveSeaLevelProvider implements SeaLevelProvider {

    public static final Codec<WaveSeaLevelProvider> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            IntRange.CODEC.fieldOf("center").forGetter(sea -> sea.center),
            IntRange.CODEC.fieldOf("amplitude").forGetter(sea -> sea.amplitude),
            IntRange.CODEC.fieldOf("period").forGetter(sea -> sea.period)
    ).apply(inst, WaveSeaLevelProvider::new));
    private final IntRange center, amplitude, period;

    public WaveSeaLevelProvider(IntRange center, IntRange amplitude, IntRange period) {
        this.center = center;
        this.amplitude = amplitude;
        this.period = period;
    }

    @Override
    public SerializableFluidPicker getSeaLevel(BlockState block, RandomSource random) {
        return new WaveFluidPicker(block, center.getRandom(random), amplitude.getRandom(random), period.getRandom(random));
    }

    @Override
    public Codec<? extends WaveSeaLevelProvider> getCodec() {
        return SeaLevelProviderTypeRegistry.WAVE.get();
    }

}
