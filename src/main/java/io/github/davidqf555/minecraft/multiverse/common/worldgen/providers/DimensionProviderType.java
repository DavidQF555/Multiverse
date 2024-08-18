package io.github.davidqf555.minecraft.multiverse.common.worldgen.providers;

import com.mojang.serialization.Codec;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class DimensionProviderType<T extends DimensionProvider> extends ForgeRegistryEntry<DimensionProviderType<?>> {

    private final Codec<T> codec;

    public DimensionProviderType(Codec<T> codec) {
        this.codec = codec;
    }

    public Codec<T> getCodec() {
        return codec;
    }

}
