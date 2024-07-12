package io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.aquifers;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.davidqf555.minecraft.multiverse.registration.worldgen.SerializableFluidPickerRegistry;
import net.minecraft.world.level.levelgen.Aquifer;

import java.util.function.Function;
import java.util.function.Supplier;

public interface SerializableFluidPicker extends Aquifer.FluidPicker {

    Supplier<Codec<SerializableFluidPicker>> CODEC = Suppliers.memoize(() -> SerializableFluidPickerRegistry.getRegistry().byNameCodec().dispatch(SerializableFluidPicker::codec, Function.identity()));

    MapCodec<? extends SerializableFluidPicker> codec();

}
