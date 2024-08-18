package io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.sea_level;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.sea_level.fluid_pickers.SerializableFluidPicker;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class FluidPickerType<T extends SerializableFluidPicker> extends ForgeRegistryEntry<FluidPickerType<?>> {

    private final Codec<T> codec;

    public FluidPickerType(Codec<T> codec) {
        this.codec = codec;
    }

    public Codec<T> getCodec() {
        return codec;
    }

}
