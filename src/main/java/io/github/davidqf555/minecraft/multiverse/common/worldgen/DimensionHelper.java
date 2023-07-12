package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.packets.UpdateClientDimensionsPacket;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.dynamic.DynamicDefaultChunkGenerator;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.chunk.ChunkGenerator;
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
        MultiverseExistingData saved = MultiverseExistingData.getOrCreate(server);
        saved.add(index);
        saved.setDirty();
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        LevelStem dimension = createDimension(server, index);
        WorldData serverConfig = server.getWorldData();
        WorldGenSettings dimensionGeneratorSettings = serverConfig.worldGenSettings();
        ResourceKey<LevelStem> dimensionKey = ResourceKey.create(Registry.LEVEL_STEM_REGISTRY, worldKey.location());
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

    public static LevelStem createDimension(MinecraftServer server, int index) {
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        long seed = getSeed(overworld.getSeed(), index, false);
        WorldgenRandom random = new WorldgenRandom(new XoroshiroRandomSource(seed));
        MultiverseShape type = randomType(random);
        boolean ceiling = type.hasCeiling();
        Pair<MultiverseType, Set<ResourceKey<Biome>>> pair = randomBiomes(random);
        Set<ResourceKey<Biome>> biomes = pair.getSecond();
        MultiverseType biomeType = pair.getFirst();
        Holder<NoiseGeneratorSettings> settings = server.registryAccess().registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY).getHolderOrThrow(type.getNoiseSettingsKey(biomeType));
        float lighting = ceiling ? random.nextFloat() * 0.5f + 0.1f : random.nextFloat() * 0.2f;
        OptionalLong time = ceiling ? OptionalLong.of(18000) : randomTime(random);
        BiomeSource provider = new MultiNoiseBiomeSource.Preset(getRegistryKey(index).location(), registry -> new Climate.ParameterList<>(biomes.stream()
                .map(registry::getOrCreateHolder)
                .map(holder -> {
                    Biome biome = holder.value();
                    return Pair.of(Climate.parameters(biome.getBaseTemperature(), biome.getDownfall(), 0, 0, 0, 0, 0), holder);
                }).collect(Collectors.toList()))).biomeSource(server.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY));
        ResourceLocation effect = randomEffect(time.isPresent() && time.getAsLong() < 22300 && time.getAsLong() > 13188, random);
        Holder<DimensionType> dimType = createDimensionType(biomeType == null ? MultiverseType.OVERWORLD.getInfiniburn() : biomeType.getInfiniburn(), type.getHeight(), type.getMinY(), ceiling, time, effect, lighting);
        ChunkGenerator generator;
        if (biomeType == null) {
            generator = new DynamicDefaultChunkGenerator(server.registryAccess().registryOrThrow(Registry.STRUCTURE_SET_REGISTRY), server.registryAccess().registryOrThrow(Registry.NOISE_REGISTRY), provider, seed, settings);
        } else {
            generator = new NoiseBasedChunkGenerator(server.registryAccess().registryOrThrow(Registry.STRUCTURE_SET_REGISTRY), server.registryAccess().registryOrThrow(Registry.NOISE_REGISTRY), provider, seed, settings);
        }
        return new LevelStem(dimType, generator);
    }

    private static Holder<DimensionType> createDimensionType(TagKey<Block> infiniburn, int height, int minY, boolean ceiling, OptionalLong time, ResourceLocation effect, float light) {
        return Holder.direct(DimensionType.create(time, !ceiling, ceiling, false, true, 1, false, false, true, true, true, minY, height, height, infiniburn, effect, light));
    }

    private static MultiverseShape randomType(Random random) {
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

    private static Pair<MultiverseType, Set<ResourceKey<Biome>>> randomBiomes(Random random) {
        MultiverseType[] biomesTypes = MultiverseType.values();
        Predicate<ResourceKey<Biome>> valid = key -> Arrays.stream(biomesTypes).anyMatch(type -> type.is(key));
        BiomeDictionary.Type[] types = BiomeDictionary.Type.getAll().stream().filter(type -> !BiomeDictionary.getBiomes(type).isEmpty()).filter(type -> BiomeDictionary.getBiomes(type).stream().anyMatch(valid)).toArray(BiomeDictionary.Type[]::new);
        Set<ResourceKey<Biome>> biomes = new HashSet<>();
        if (types.length == 0) {
            biomes.add(Biomes.PLAINS);
        } else {
            biomes.addAll(BiomeDictionary.getBiomes(types[random.nextInt(types.length)]));
        }
        double chance = ServerConfigs.INSTANCE.additionalBiomeTypeChance.get();
        for (BiomeDictionary.Type type : types) {
            if (random.nextDouble() < chance) {
                biomes.addAll(BiomeDictionary.getBiomes(type));
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
        if (ServerConfigs.INSTANCE.mixedBiomes.get()) {
            if (counts.size() > 1) {
                return Pair.of(null, biomes);
            }
        }
        MultiverseType type = counts.keySet().stream().max((i, j) -> counts.get(j) - counts.get(i)).orElseThrow();
        biomes.removeIf(key -> !type.is(key));
        return Pair.of(type, biomes);
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