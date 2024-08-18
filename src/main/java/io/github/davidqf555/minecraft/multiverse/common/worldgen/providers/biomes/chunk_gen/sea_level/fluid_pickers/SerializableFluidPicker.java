package io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.sea_level.fluid_pickers;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.sea_level.FluidPickerType;
import io.github.davidqf555.minecraft.multiverse.registration.custom.FluidPickerTypeRegistry;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.Aquifer;

public interface SerializableFluidPicker extends Aquifer.FluidPicker {

    Codec<SerializableFluidPicker> CODEC = ExtraCodecs.lazyInitializedCodec(() -> FluidPickerTypeRegistry.getRegistry().getCodec().dispatch(SerializableFluidPicker::getType, FluidPickerType::getCodec));

    FluidPickerType<?> getType();

}
