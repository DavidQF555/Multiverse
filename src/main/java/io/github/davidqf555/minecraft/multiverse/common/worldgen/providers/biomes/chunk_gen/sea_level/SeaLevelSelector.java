package io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.sea_level;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.sea_level.fluid_pickers.SerializableFluidPicker;
import io.github.davidqf555.minecraft.multiverse.registration.custom.SeaLevelSelectorRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.custom.SeaLevelSelectorTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.RandomSource;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class SeaLevelSelector extends ForgeRegistryEntry<SeaLevelSelector> {

    public static final Codec<SeaLevelSelector> DIRECT_CODEC = ExtraCodecs.lazyInitializedCodec(() -> SeaLevelSelectorTypeRegistry.getRegistry().getCodec().dispatch(SeaLevelSelector::getType, SeaLevelSelectorType::getCodec));
    public static final Codec<Holder<SeaLevelSelector>> CODEC = RegistryFileCodec.create(SeaLevelSelectorRegistry.LOCATION, DIRECT_CODEC);

    public abstract SerializableFluidPicker getSeaLevel(BlockState block, RandomSource random);

    public abstract SeaLevelSelectorType<?> getType();

}
