package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.chunk_gen.sea;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.sea.SerializableFluidPicker;
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
