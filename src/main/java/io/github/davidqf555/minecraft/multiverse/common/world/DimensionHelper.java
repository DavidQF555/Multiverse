package io.github.davidqf555.minecraft.multiverse.common.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.packets.UpdateClientDimensionsPacket;
import io.github.davidqf555.minecraft.multiverse.common.registration.worldgen.NoiseSettingsRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.*;
import java.util.stream.Collectors;

public final class DimensionHelper {

    private DimensionHelper() {
    }

    @SuppressWarnings("deprecation")
    public static ServerLevel getOrCreateWorld(MinecraftServer server, int index) {
        if (index <= 0) {
            return server.getLevel(Level.OVERWORLD);
        }
        ResourceKey<Level> world = getRegistryKey(index);
        Map<ResourceKey<Level>, ServerLevel> map = server.forgeGetWorldMap();
        if (map.containsKey(world)) {
            return map.get(world);
        }
        return createAndRegisterWorldAndDimension(server, map, world, index);
    }

    public static long getSeed(long overworld, int index, boolean obfuscated) {
        if (!obfuscated) {
            overworld = BiomeManager.obfuscateSeed(overworld);
        }
        return overworld + 80000L * index;
    }

    private static ResourceKey<Level> getRegistryKey(int index) {
        return ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(Multiverse.MOD_ID, index + ""));
    }

    public static int getIndex(ResourceKey<Level> world) {
        if (world.location().getNamespace().equals(Multiverse.MOD_ID)) {
            return Integer.parseInt(world.location().getPath());
        }
        return 0;
    }

    @SuppressWarnings("deprecation")
    private static ServerLevel createAndRegisterWorldAndDimension(MinecraftServer server, Map<ResourceKey<Level>, ServerLevel> map, ResourceKey<Level> worldKey, int index) {
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        ResourceKey<LevelStem> dimensionKey = ResourceKey.create(Registry.LEVEL_STEM_REGISTRY, worldKey.location());
        LevelStem dimension = createDimension(server, index);
        WorldData serverConfig = server.getWorldData();
        WorldGenSettings dimensionGeneratorSettings = serverConfig.worldGenSettings();
        Registry.register(dimensionGeneratorSettings.dimensions(), dimensionKey.location(), dimension);
        DerivedLevelData derivedWorldInfo = new DerivedLevelData(serverConfig, serverConfig.overworldData());
        ServerLevel newWorld = new ServerLevel(server, server.executor, server.storageSource, derivedWorldInfo, worldKey, dimension.typeHolder(), server.progressListenerFactory.create(11), dimension.generator(), dimensionGeneratorSettings.isDebug(), BiomeManager.obfuscateSeed(dimensionGeneratorSettings.seed()), ImmutableList.of(), false);
        overworld.getWorldBorder().addListener(new BorderChangeListener.DelegateBorderChangeListener(newWorld.getWorldBorder()));
        map.put(worldKey, newWorld);
        server.markWorldsDirty();
        MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(newWorld));
        Multiverse.CHANNEL.send(PacketDistributor.ALL.noArg(), new UpdateClientDimensionsPacket(worldKey));
        return newWorld;
    }

    private static ResourceKey<NoiseGeneratorSettings> getNoiseGeneratorSettings(boolean floor, boolean ceiling) {
        if (floor) {
            if (ceiling) {
                return NoiseSettingsRegistry.BOTH;
            } else {
                return NoiseSettingsRegistry.BOTTOM;
            }
        } else if (ceiling) {
            return NoiseSettingsRegistry.TOP;
        } else {
            return NoiseSettingsRegistry.NONE;
        }
    }

    private static LevelStem createDimension(MinecraftServer server, int index) {
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        long seed = getSeed(overworld.getSeed(), index, false);
        WorldgenRandom random = new WorldgenRandom(new XoroshiroRandomSource(seed));
        Pair<Boolean, Boolean> bounds = randomBounds(random);
        boolean floor = bounds.getFirst();
        boolean ceiling = bounds.getSecond();
        Holder<NoiseGeneratorSettings> settings = server.registryAccess().registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY).getHolderOrThrow(getNoiseGeneratorSettings(floor, ceiling));
        float lighting = ceiling ? random.nextFloat() * 0.5f + 0.1f : random.nextFloat() * 0.2f;
        OptionalLong time = ceiling ? OptionalLong.of(18000) : randomTime(random);
        Registry<Biome> lookup = server.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
        BiomeSource provider = new MultiNoiseBiomeSource.Preset(getRegistryKey(index).location(), registry -> new Climate.ParameterList<>(randomBiomes(random).stream()
                .map(lookup::getOrCreateHolder)
                .map(holder -> {
                    Biome biome = holder.value();
                    return Pair.of(Climate.parameters(biome.getBaseTemperature(), biome.getDownfall(), 0, 0, 0, 0, 0), holder);
                }).collect(Collectors.toList()))).biomeSource(lookup);
        NoiseBasedChunkGenerator generator = new NoiseBasedChunkGenerator(server.registryAccess().registryOrThrow(Registry.STRUCTURE_SET_REGISTRY), server.registryAccess().registryOrThrow(Registry.NOISE_REGISTRY), provider, seed, settings);
        ResourceLocation effect = randomEffect(time.isPresent() && time.getAsLong() < 22300 && time.getAsLong() > 13188, random);
        Holder<DimensionType> type = createDimensionType(ceiling, time, effect, lighting);
        return new LevelStem(type, generator);
    }

    private static Holder<DimensionType> createDimensionType(boolean ceiling, OptionalLong time, ResourceLocation effect, float light) {
        return Holder.direct(DimensionType.create(time, !ceiling, ceiling, false, true, 1, false, false, true, true, true, 0, ceiling ? 128 : 256, 128, BlockTags.INFINIBURN_OVERWORLD, effect, light));
    }

    private static Set<ResourceKey<Biome>> randomBiomes(Random random) {
        List<BiomeDictionary.Type> types = BiomeDictionary.Type.getAll().stream().filter(type -> !BiomeDictionary.getBiomes(type).isEmpty()).filter(type -> type != BiomeDictionary.Type.NETHER && type != BiomeDictionary.Type.END).toList();
        if (types.isEmpty()) {
            return ImmutableSet.of(Biomes.PLAINS);
        }
        Set<ResourceKey<Biome>> biomes = new HashSet<>(BiomeDictionary.getBiomes(types.get(random.nextInt(types.size()))));
        double chance = ServerConfigs.INSTANCE.additionalBiomeTypeChance.get();
        for (BiomeDictionary.Type type : types) {
            if (random.nextDouble() < chance) {
                biomes.addAll(BiomeDictionary.getBiomes(type));
            }
        }
        biomes.removeIf(key -> !BiomeDictionary.hasType(key, BiomeDictionary.Type.OVERWORLD));
        if (biomes.isEmpty()) {
            return ImmutableSet.of(Biomes.PLAINS);
        }
        return biomes;
    }

    private static Pair<Boolean, Boolean> randomBounds(Random random) {
        List<Pair<Boolean, Boolean>> combo = new ArrayList<>();
        combo.add(Pair.of(true, true));
        combo.add(Pair.of(false, false));
        combo.add(Pair.of(true, false));
        if (ServerConfigs.INSTANCE.inverse.get()) {
            combo.add(Pair.of(false, true));
        }
        return combo.get(random.nextInt(combo.size()));
    }

    private static OptionalLong randomTime(Random random) {
        if (random.nextDouble() < ServerConfigs.INSTANCE.fixedTimeChance.get()) {
            return OptionalLong.of(random.nextInt(24000));
        }
        return OptionalLong.empty();
    }

    private static ResourceLocation randomEffect(boolean night, Random random) {
        int rand = random.nextInt(night ? 3 : 2);
        if (rand == 0) {
            return DimensionType.OVERWORLD_EFFECTS;
        } else if (rand == 1) {
            return DimensionType.NETHER_EFFECTS;
        } else {
            return DimensionType.END_EFFECTS;
        }
    }

}
