package io.github.davidqf555.minecraft.multiverse.common.world.worldgen.generators.biomes.chunk_gen.noise_settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.MultiverseType;
import io.github.davidqf555.minecraft.multiverse.registration.custom.biomes.BiomeNoiseGeneratorSettingsGeneratorTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

import java.util.Arrays;
import java.util.Map;

public class TypeMapNoiseGeneratorSettingsGenerator implements BiomeNoiseGeneratorSettingsGenerator {

    public static final MapCodec<TypeMapNoiseGeneratorSettingsGenerator> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.simpleMap(MultiverseType.CODEC, NoiseGeneratorSettings.CODEC, Keyable.forStrings(() -> Arrays.stream(MultiverseType.values()).map(MultiverseType::getName))).codec().fieldOf("types").forGetter(val -> val.settings)
    ).apply(inst, TypeMapNoiseGeneratorSettingsGenerator::new));
    private final Map<MultiverseType, Holder<NoiseGeneratorSettings>> settings;

    public TypeMapNoiseGeneratorSettingsGenerator(Map<MultiverseType, Holder<NoiseGeneratorSettings>> settings) {
        this.settings = settings;
    }

    @Override
    public Holder<NoiseGeneratorSettings> generate(RegistryAccess access, long seed, RandomSource random, MultiverseType type, HolderSet<Biome> biomes) {
        return settings.get(type);
    }

    @Override
    public MapCodec<? extends TypeMapNoiseGeneratorSettingsGenerator> getCodec() {
        return BiomeNoiseGeneratorSettingsGeneratorTypeRegistry.TYPE_MAP.get();
    }

}
