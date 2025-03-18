package multiverse.common.world.worldgen.generators.biomes.chunk_gen.sea;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import multiverse.common.world.worldgen.sea.SerializableFluidPicker;
import multiverse.registration.custom.generator.SeaLevelGeneratorRegistry;
import multiverse.registration.custom.generator.SeaLevelGeneratorTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public interface SeaLevelGenerator {

    Codec<SeaLevelGenerator> DIRECT_CODEC = Codec.lazyInitialized(() -> SeaLevelGeneratorTypeRegistry.getRegistry().byNameCodec().dispatch(SeaLevelGenerator::getCodec, Function.identity()));
    Codec<Holder<SeaLevelGenerator>> CODEC = RegistryFileCodec.create(SeaLevelGeneratorRegistry.LOCATION, DIRECT_CODEC);

    SerializableFluidPicker getSeaLevel(BlockState block, RandomSource random);

    MapCodec<? extends SeaLevelGenerator> getCodec();

}
