package io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.registration.custom.BiomeConfigRegistry;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.List;

public class BiomeConfig extends ForgeRegistryEntry<BiomeConfig> {

    public static final Codec<BiomeConfig> DIRECT_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            BiomeType.CODEC.listOf().fieldOf("types").forGetter(BiomeConfig::getTypes)
    ).apply(inst, BiomeConfig::new));
    public static final Codec<Holder<BiomeConfig>> CODEC = RegistryFileCodec.create(BiomeConfigRegistry.LOCATION, DIRECT_CODEC);
    private final List<BiomeType> types;

    public BiomeConfig(List<BiomeType> types) {
        this.types = types;
    }

    public List<BiomeType> getTypes() {
        return types;
    }

}
