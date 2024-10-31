package io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.sea_level.fluid_pickers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.davidqf555.minecraft.multiverse.registration.custom.FluidPickerTypeRegistry;
import net.minecraft.world.level.levelgen.Aquifer;

import java.util.function.Function;

public interface SerializableFluidPicker extends Aquifer.FluidPicker {

    Codec<SerializableFluidPicker> CODEC = Codec.lazyInitialized(() -> FluidPickerTypeRegistry.getRegistry().byNameCodec().dispatch(SerializableFluidPicker::getCodec, Function.identity()));

    MapCodec<? extends SerializableFluidPicker> getCodec();

}
