package multiverse.common.world.worldgen.sea;

import com.mojang.serialization.Codec;
import multiverse.common.world.worldgen.generators.biomes.chunk_gen.sea.FluidPickerType;
import multiverse.registration.custom.FluidPickerTypeRegistry;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.Aquifer;

public interface SerializableFluidPicker extends Aquifer.FluidPicker {

    Codec<SerializableFluidPicker> CODEC = ExtraCodecs.lazyInitializedCodec(() -> FluidPickerTypeRegistry.getRegistry().getCodec().dispatch(SerializableFluidPicker::getType, FluidPickerType::getCodec));

    FluidPickerType<?> getType();

}
