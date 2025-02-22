package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.chunk_gen.sea_level;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.chunk_gen.sea_level.fluid_pickers.SerializableFluidPicker;
import io.github.davidqf555.minecraft.multiverse.registration.custom.SeaLevelProviderRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.custom.SeaLevelProviderTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public interface SeaLevelGenerator {

    Codec<SeaLevelGenerator> DIRECT_CODEC = ExtraCodecs.lazyInitializedCodec(() -> SeaLevelProviderTypeRegistry.getRegistry().getCodec().dispatch(SeaLevelGenerator::getCodec, Function.identity()));
    Codec<Holder<SeaLevelGenerator>> CODEC = RegistryFileCodec.create(SeaLevelProviderRegistry.LOCATION, DIRECT_CODEC);

    SerializableFluidPicker getSeaLevel(BlockState block, RandomSource random);

    Codec<? extends SeaLevelGenerator> getCodec();

}
