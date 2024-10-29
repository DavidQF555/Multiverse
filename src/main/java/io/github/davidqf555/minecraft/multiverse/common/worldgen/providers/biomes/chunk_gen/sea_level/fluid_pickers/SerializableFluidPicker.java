package io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.sea_level.fluid_pickers;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.registration.custom.FluidPickerTypeRegistry;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.Aquifer;

import java.util.function.Function;

public interface SerializableFluidPicker extends Aquifer.FluidPicker {

    Codec<SerializableFluidPicker> CODEC = ExtraCodecs.lazyInitializedCodec(() -> FluidPickerTypeRegistry.getRegistry().getCodec().dispatch(SerializableFluidPicker::getCodec, Function.identity()));

    Codec<? extends SerializableFluidPicker> getCodec();

}
