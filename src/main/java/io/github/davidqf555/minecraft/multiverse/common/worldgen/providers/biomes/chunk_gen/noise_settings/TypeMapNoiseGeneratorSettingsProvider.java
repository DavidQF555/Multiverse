package io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.noise_settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseType;
import io.github.davidqf555.minecraft.multiverse.registration.custom.biomes.BiomeNoiseGeneratorSettingsProviderTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class TypeMapNoiseGeneratorSettingsProvider implements BiomeNoiseGeneratorSettingsProvider {

    public static final Codec<TypeMapNoiseGeneratorSettingsProvider> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.simpleMap(MultiverseType.CODEC, NoiseGeneratorSettings.CODEC, Keyable.forStrings(() -> Arrays.stream(MultiverseType.values()).map(MultiverseType::getName))).codec().fieldOf("types").forGetter(val -> val.settings)
    ).apply(inst, TypeMapNoiseGeneratorSettingsProvider::new));
    private final Map<MultiverseType, Holder<NoiseGeneratorSettings>> settings;

    public TypeMapNoiseGeneratorSettingsProvider(Map<MultiverseType, Holder<NoiseGeneratorSettings>> settings) {
        this.settings = settings;
    }

    @Override
    public Holder<NoiseGeneratorSettings> provide(RegistryAccess access, long seed, RandomSource random, MultiverseType type, Set<ResourceKey<Biome>> biomes) {
        return settings.get(type);
    }

    @Override
    public Codec<? extends TypeMapNoiseGeneratorSettingsProvider> getCodec() {
        return BiomeNoiseGeneratorSettingsProviderTypeRegistry.TYPE_MAP.get();
    }
}
