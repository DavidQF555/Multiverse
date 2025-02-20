package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.chunk_gen.noise_settings;

import com.mojang.serialization.Codec;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class BiomeNoiseGeneratorSettingsGeneratorType<T extends BiomeNoiseGeneratorSettingsGenerator> extends ForgeRegistryEntry<BiomeNoiseGeneratorSettingsGeneratorType<?>> {

    private final Codec<T> codec;

    public BiomeNoiseGeneratorSettingsGeneratorType(Codec<T> codec) {
        this.codec = codec;
    }

    public Codec<T> getCodec() {
        return codec;
    }

}
