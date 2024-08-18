package io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.sea_level;

import com.mojang.serialization.Codec;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class SeaLevelSelectorType<T extends SeaLevelSelector> extends ForgeRegistryEntry<SeaLevelSelectorType<?>> {

    private final Codec<T> codec;

    public SeaLevelSelectorType(Codec<T> codec) {
        this.codec = codec;
    }

    public Codec<T> getCodec() {
        return codec;
    }

}
