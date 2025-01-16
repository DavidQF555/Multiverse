package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.providers.biomes.chunk_gen.sea_level;

import com.mojang.serialization.Codec;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class SeaLevelProviderType<T extends SeaLevelProvider> extends ForgeRegistryEntry<SeaLevelProviderType<?>> {

    private final Codec<T> codec;

    public SeaLevelProviderType(Codec<T> codec) {
        this.codec = codec;
    }

    public Codec<T> getCodec() {
        return codec;
    }

}
