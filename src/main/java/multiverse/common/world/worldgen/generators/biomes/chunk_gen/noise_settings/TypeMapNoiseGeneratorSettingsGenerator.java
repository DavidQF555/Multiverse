package multiverse.common.world.worldgen.generators.biomes.chunk_gen.noise_settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Keyable;
import multiverse.common.world.worldgen.MultiverseType;
import multiverse.registration.custom.biomes.BiomeNoiseGeneratorSettingsGeneratorTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomSource;

import java.util.Arrays;
import java.util.Map;

public class TypeMapNoiseGeneratorSettingsGenerator extends BiomeNoiseGeneratorSettingsGenerator {

    public static final Codec<TypeMapNoiseGeneratorSettingsGenerator> CODEC = Codec.simpleMap(MultiverseType.CODEC, NoiseGeneratorSettings.CODEC, Keyable.forStrings(() -> Arrays.stream(MultiverseType.values()).map(MultiverseType::getName))).xmap(TypeMapNoiseGeneratorSettingsGenerator::new, gen -> gen.settings).fieldOf("types").codec();
    private final Map<MultiverseType, Holder<NoiseGeneratorSettings>> settings;

    public TypeMapNoiseGeneratorSettingsGenerator(Map<MultiverseType, Holder<NoiseGeneratorSettings>> settings) {
        this.settings = settings;
    }

    @Override
    public Holder<NoiseGeneratorSettings> generate(RegistryAccess access, long seed, RandomSource random, MultiverseType type, HolderSet<Biome> biomes) {
        return settings.get(type);
    }

    @Override
    public BiomeNoiseGeneratorSettingsGeneratorType<?> getType() {
        return BiomeNoiseGeneratorSettingsGeneratorTypeRegistry.TYPE_MAP.get();
    }
}
