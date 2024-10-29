package io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseType;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes.BiomeConfig;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.DimensionProvider;
import io.github.davidqf555.minecraft.multiverse.registration.custom.DimensionProviderTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.Set;

public class BiomeConfigDimensionProvider implements DimensionProvider {

    public static final Codec<BiomeConfigDimensionProvider> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            BiomeConfig.CODEC.fieldOf("biomes").forGetter(val -> val.config),
            BiomeDimensionProvider.CODEC.fieldOf("dimension").forGetter(val -> val.provider)
    ).apply(inst, BiomeConfigDimensionProvider::new));
    private final Holder<BiomeConfig> config;
    private final BiomeDimensionProvider provider;

    public BiomeConfigDimensionProvider(Holder<BiomeConfig> config, BiomeDimensionProvider provider) {
        this.config = config;
        this.provider = provider;
    }

    @Override
    public LevelStem createDimension(RegistryAccess access, long seed, RandomSource random) {
        BiomeConfig config = this.config.value();
        Pair<MultiverseType, Set<ResourceKey<Biome>>> biomes = config.selectRandom(access.registryOrThrow(Registries.BIOME), random);
        return provider.provide(access, seed, random, biomes.getFirst(), biomes.getSecond());
    }

    @Override
    public Codec<? extends DimensionProvider> getCodec() {
        return DimensionProviderTypeRegistry.BIOME_CONFIG.get();
    }

}
