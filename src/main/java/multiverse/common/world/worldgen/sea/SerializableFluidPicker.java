package multiverse.common.world.worldgen.sea;

import com.mojang.serialization.Codec;
import multiverse.registration.custom.FluidPickerTypeRegistry;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.Aquifer;

import java.util.function.Function;

public interface SerializableFluidPicker extends Aquifer.FluidPicker {

    Codec<SerializableFluidPicker> CODEC = ExtraCodecs.lazyInitializedCodec(() -> FluidPickerTypeRegistry.getRegistry().getCodec().dispatch(SerializableFluidPicker::getCodec, Function.identity()));

    Codec<? extends SerializableFluidPicker> getCodec();

}
