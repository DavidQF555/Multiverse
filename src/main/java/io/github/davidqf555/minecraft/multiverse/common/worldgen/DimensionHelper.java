package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.packets.UpdateClientDimensionsPacket;
import io.github.davidqf555.minecraft.multiverse.registration.worldgen.MultiverseBiomesRegistry;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.*;
import java.util.function.Predicate;
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

    public static ResourceKey<Level> getRegistryKey(int index) {
        return ResourceKey.create(Registries.DIMENSION, new ResourceLocation(Multiverse.MOD_ID, String.valueOf(index)));
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
        ResourceKey<LevelStem> dimensionKey = ResourceKey.create(Registries.LEVEL_STEM, worldKey.location());
        LevelStem dimension = createDimension(server, index);
        WorldData worldData = server.getWorldData();
        WorldOptions worldGenSettings = worldData.worldGenOptions();
        DerivedLevelData derivedLevelData = new DerivedLevelData(worldData, worldData.overworldData());

        LayeredRegistryAccess<RegistryLayer> registries = server.registries();
        RegistryAccess.ImmutableRegistryAccess composite = (RegistryAccess.ImmutableRegistryAccess) registries.compositeAccess();
        Map<ResourceKey<? extends Registry<?>>, Registry<?>> regmap = new HashMap<>(composite.registries);
        ResourceKey<? extends Registry<?>> key = ResourceKey.create(ResourceKey.createRegistryKey(new ResourceLocation("root")), new ResourceLocation("dimension"));
        MappedRegistry<LevelStem> oldRegistry = (MappedRegistry<LevelStem>) regmap.get(key);
        Lifecycle oldLifecycle = oldRegistry.registryLifecycle();
        MappedRegistry<LevelStem> newRegistry = new MappedRegistry<>(Registries.LEVEL_STEM, oldLifecycle, false);
        for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : oldRegistry.entrySet()) {
            ResourceKey<LevelStem> oldKey = entry.getKey();
            ResourceKey<Level> oldLevelKey = ResourceKey.create(Registries.DIMENSION, oldKey.location());
            LevelStem dim = entry.getValue();
            if (dim != null && oldLevelKey != worldKey) {
                Registry.register(newRegistry, oldKey, dim);
            }
        }
        Registry.register(newRegistry, dimensionKey, dimension);
        regmap.replace(key, newRegistry);
        composite.registries = regmap;

        ServerLevel newWorld = new ServerLevel(
                server,
                server.executor,
                server.storageSource,
                derivedLevelData,
                worldKey,
                dimension,
                server.progressListenerFactory.create(11),
                worldData.isDebugWorld(),
                BiomeManager.obfuscateSeed(worldGenSettings.seed()),
                ImmutableList.of(),
                false
        );
        overworld.getWorldBorder().addListener(new BorderChangeListener.DelegateBorderChangeListener(newWorld.getWorldBorder()));
        map.put(worldKey, newWorld);
        server.markWorldsDirty();
        MinecraftForge.EVENT_BUS.post(new LevelEvent.Load(newWorld));
        Multiverse.CHANNEL.send(PacketDistributor.ALL.noArg(), new UpdateClientDimensionsPacket(worldKey));
        return newWorld;
    }

    public static LevelStem createDimension(MinecraftServer server, int index) {
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        long seed = getSeed(overworld.getSeed(), index, false);
        WorldgenRandom random = new WorldgenRandom(new XoroshiroRandomSource(seed));
        MultiverseShape type = randomType(random);
        RegistryAccess access = server.registryAccess();
        Registry<Biome> biomeRegistry = access.registryOrThrow(Registries.BIOME);
        Pair<MultiverseType, Set<ResourceKey<Biome>>> pair = randomBiomes(biomeRegistry, random);
        Set<ResourceKey<Biome>> biomes = pair.getSecond();
        MultiverseType biomeType = pair.getFirst();
        Holder<NoiseGeneratorSettings> settings = access.registryOrThrow(Registries.NOISE_SETTINGS).getHolderOrThrow(type.getNoiseSettingsKey(biomeType));
        BiomeSource provider = MultiNoiseBiomeSource.createFromList(new Climate.ParameterList<>(biomes.stream().map(key -> Pair.of(MultiverseBiomesRegistry.getMultiverseBiomes().getParameters(key), (Holder<Biome>) biomeRegistry.getHolderOrThrow(key))).collect(Collectors.toList())));
        Holder<DimensionType> dimType = access.registryOrThrow(Registries.DIMENSION_TYPE).getHolderOrThrow(getRandomType(type, biomeType, random));
        ChunkGenerator generator = new NoiseBasedChunkGenerator(provider, settings);
        return new LevelStem(dimType, generator);
    }

    private static ResourceKey<DimensionType> getRandomType(MultiverseShape shape, MultiverseType type, RandomSource rand) {
        boolean ceiling = shape.hasCeiling();
        List<ResourceKey<DimensionType>> types = new ArrayList<>();
        for (MultiverseTimeType time : MultiverseTimeType.values()) {
            for (MultiverseEffectType effect : MultiverseEffectType.values()) {
                if (!ceiling || time.isNight()) {
                    types.add(shape.getTypeKey(type, time, effect));
                }
            }
        }
        if (types.isEmpty()) {
            return BuiltinDimensionTypes.OVERWORLD;
        }
        return types.get(rand.nextInt(types.size()));
    }

    private static MultiverseShape randomType(RandomSource random) {
        MultiverseShape[] values = MultiverseShape.values();
        int totalWeight = Arrays.stream(values).mapToInt(MultiverseShape::getWeight).sum();
        int selected = random.nextInt(totalWeight);
        int current = 0;
        for (MultiverseShape type : values) {
            current += type.getWeight();
            if (selected < current) {
                return type;
            }
        }
        throw new RuntimeException();
    }

    private static Pair<MultiverseType, Set<ResourceKey<Biome>>> randomBiomes(Registry<Biome> registry, RandomSource random) {
        Set<MultiverseType> biomesTypes = EnumSet.allOf(MultiverseType.class);
        Predicate<ResourceKey<Biome>> valid = key -> biomesTypes.stream().anyMatch(type -> type.is(key));
        List<Set<ResourceKey<Biome>>> sets = MultiverseBiomesRegistry.getMultiverseBiomeTags().stream()
                .map(registry::getTag)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(named -> named.stream()
                        .map(Holder::value)
                        .map(biome -> ResourceKey.create(Registries.BIOME, registry.getKey(biome)))
                        .filter(valid)
                        .collect(Collectors.toSet())
                )
                .filter(set -> !set.isEmpty())
                .toList();
        Set<ResourceKey<Biome>> biomes = new HashSet<>();
        if (sets.isEmpty()) {
            biomes.add(Biomes.PLAINS);
        } else {
            biomes.addAll(sets.get(random.nextInt(sets.size())));
        }
        double chance = ServerConfigs.INSTANCE.additionalBiomeTypeChance.get();
        for (Set<ResourceKey<Biome>> set : sets) {
            if (random.nextDouble() < chance) {
                biomes.addAll(set);
            }
        }
        biomes.removeIf(valid.negate());
        Map<MultiverseType, Integer> counts = new EnumMap<>(MultiverseType.class);
        for (ResourceKey<Biome> biome : biomes) {
            for (MultiverseType type : biomesTypes) {
                if (type.is(biome)) {
                    counts.compute(type, (t, current) -> current == null ? 1 : current + 1);
                }
            }
        }
        MultiverseType type = counts.keySet().stream().max((i, j) -> counts.get(j) - counts.get(i)).orElseThrow();
        biomes.removeIf(key -> !type.is(key));
        return Pair.of(type, biomes);
    }

}
