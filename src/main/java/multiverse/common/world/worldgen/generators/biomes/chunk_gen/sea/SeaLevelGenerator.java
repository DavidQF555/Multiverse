package multiverse.common.world.worldgen.generators.biomes.chunk_gen.sea;

import com.mojang.serialization.Codec;
import multiverse.common.world.worldgen.sea.SerializableFluidPicker;
import multiverse.registration.custom.SeaLevelGeneratorRegistry;
import multiverse.registration.custom.SeaLevelGeneratorTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.RandomSource;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class SeaLevelGenerator extends ForgeRegistryEntry<SeaLevelGenerator> {

    public static final Codec<SeaLevelGenerator> DIRECT_CODEC = ExtraCodecs.lazyInitializedCodec(() -> SeaLevelGeneratorTypeRegistry.getRegistry().getCodec().dispatch(SeaLevelGenerator::getType, SeaLevelGeneratorType::getCodec));
    public static final Codec<Holder<SeaLevelGenerator>> CODEC = RegistryFileCodec.create(SeaLevelGeneratorRegistry.LOCATION, DIRECT_CODEC);

    public abstract SerializableFluidPicker getSeaLevel(BlockState block, RandomSource random);

    public abstract SeaLevelGeneratorType<?> getType();

}
