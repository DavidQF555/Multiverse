package io.github.davidqf555.minecraft.multiverse.common.worldgen.sea;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.aquifers.SerializableFluidPicker;
import io.github.davidqf555.minecraft.multiverse.registration.worldgen.SeaLevelSelectorRegistry;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;
import java.util.function.Supplier;

public interface SeaLevelSelector {

    Supplier<Codec<SeaLevelSelector>> CODEC = Suppliers.memoize(() -> SeaLevelSelectorRegistry.getRegistry().byNameCodec().dispatch(SeaLevelSelector::codec, Function.identity()));

    SerializableFluidPicker getSeaLevel(BlockState block, long seed, int index);

    MapCodec<? extends SeaLevelSelector> codec();

}
