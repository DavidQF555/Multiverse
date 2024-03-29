package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.packets.UpdateClientDimensionsPacket;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes.BiomeType;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes.MultiverseBiomes;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.data.BiomesManager;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.data.EffectsManager;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.data.ShapesManager;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.data.TimesManager;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.effects.MultiverseEffect;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.aquifers.SerializableFluidPicker;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.phys.Vec3;
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
    public static Optional<ServerLevel> getWorld(MinecraftServer server, int index) {
        if (index <= 0) {
            return Optional.of(server.overworld());
        }
        ResourceKey<Level> world = getRegistryKey(index);
        return Optional.ofNullable(server.forgeGetWorldMap().get(world));
    }

    @SuppressWarnings("deprecation")
    public static ServerLevel getOrCreateWorld(MinecraftServer server, int index) {
        return getWorld(server, index).orElseGet(() -> {
            Map<ResourceKey<Level>, ServerLevel> map = server.forgeGetWorldMap();
            ResourceKey<Level> world = getRegistryKey(index);
            return createAndRegisterWorldAndDimension(server, map, world, index);
        });
    }

    public static long getSeed(long overworld, int index, boolean obfuscated) {
        if (!obfuscated) {
            overworld = BiomeManager.obfuscateSeed(overworld);
        }
        return overworld + 80000L * index;
    }

    public static Vec3 translate(Vec3 pos, DimensionType from, DimensionType to, boolean logical) {
        int fromHeight = logical ? from.logicalHeight() : from.height();
        int toHeight = logical ? to.logicalHeight() : to.height();
        double factorY = Mth.clamp((pos.y() - from.minY()) / fromHeight, 0, 1);
        double y = to.minY() + toHeight * factorY;
        double scale = DimensionType.getTeleportationScale(from, to);
        return new Vec3(pos.x() * scale, y, pos.z() * scale);
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
        MappedRegistry<LevelStem> oldRegistry = (MappedRegistry<LevelStem>) regmap.get(Registries.LEVEL_STEM);
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
        regmap.replace(Registries.LEVEL_STEM, newRegistry);
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
        MultiverseShape shape = randomShape(random);
        RegistryAccess access = server.registryAccess();
        Registry<Biome> biomeRegistry = access.registryOrThrow(Registries.BIOME);
        Pair<MultiverseType, Set<ResourceKey<Biome>>> pair = randomBiomes(biomeRegistry, random);
        Set<ResourceKey<Biome>> biomes = pair.getSecond();
        MultiverseType type = pair.getFirst();
        Holder<NoiseGeneratorSettings> settings = access.registryOrThrow(Registries.NOISE_SETTINGS).getHolderOrThrow(shape.getNoiseSettingsKey(type));
        BiomeSource provider = MultiNoiseBiomeSource.createFromList(getBiomeParameters(access, type, shape, biomes));
        Holder<DimensionType> dimType = access.registryOrThrow(Registries.DIMENSION_TYPE).getHolderOrThrow(getRandomType(shape, type, random));
        SerializableFluidPicker fluid = shape.getSea(type.getDefaultFluid(), seed, index);
        ChunkGenerator generator = new MultiverseChunkGenerator(provider, settings, shape, fluid);
        return new LevelStem(dimType, generator);
    }

    private static ResourceKey<DimensionType> getRandomType(MultiverseShape shape, MultiverseType type, RandomSource rand) {
        Map<ResourceKey<DimensionType>, Integer> types = new HashMap<>();
        Map<MultiverseTime, Integer> times = shape.getFixedTime().map(time -> Map.of(time, 1)).orElseGet(TimesManager.INSTANCE::getTimes);
        Map<MultiverseEffect, Integer> effects = EffectsManager.INSTANCE.getEffects();
        for (MultiverseEffect effect : effects.keySet()) {
            int w = effects.get(effect);
            times.forEach((time, weight) -> {
                ResourceKey<DimensionType> key = shape.getTypeKey(type, time, effect);
                types.put(key, types.getOrDefault(key, 0) + w * weight);
            });
        }
        int total = types.values().stream().reduce(Integer::sum).orElseThrow();
        int random = rand.nextInt(total);
        for (ResourceKey<DimensionType> key : types.keySet()) {
            total -= types.get(key);
            if (random >= total) {
                return key;
            }
        }
        throw new RuntimeException();
    }

    private static Climate.ParameterList<Holder<Biome>> getBiomeParameters(RegistryAccess access, MultiverseType type, MultiverseShape shape, Set<ResourceKey<Biome>> biomes) {
        MultiverseBiomes ref = BiomesManager.INSTANCE.getBiomes();
        Registry<Biome> biomeReg = access.registryOrThrow(Registries.BIOME);
        Registry<DimensionType> dimTypeReg = access.registryOrThrow(Registries.DIMENSION_TYPE);
        List<Pair<Climate.ParameterPoint, Holder<Biome>>> out = new ArrayList<>();
        for (ResourceKey<Biome> biome : biomes) {
            Holder<Biome> holder = biomeReg.getHolderOrThrow(biome);
            for (Climate.ParameterPoint orig : ref.getParameters(biome)) {
                Climate.Parameter depth = translateDepth(orig.depth(), dimTypeReg.getOrThrow(type.getNormalType()), shape);
                Climate.ParameterPoint point = new Climate.ParameterPoint(orig.temperature(), orig.humidity(), orig.continentalness(), orig.erosion(), depth, orig.weirdness(), orig.offset());
                out.add(Pair.of(point, holder));
            }
        }
        return new Climate.ParameterList<>(out);
    }

    //needed because depth function has a constant lerp of y from -64 to 320, scaled from 1.5 to -1.5
    private static Climate.Parameter translateDepth(Climate.Parameter depth, DimensionType from, MultiverseShape to) {
        double start = Climate.unquantizeCoord(depth.min());
        double end = Climate.unquantizeCoord(depth.max());

        double fDepthStart = Mth.clampedMap(from.minY(), -64, 320, 1.5, -1.5);
        double fDepthEnd = Mth.clampedMap(from.minY() + from.height(), -64, 320, 1.5, -1.5);

        double fStartFactor = Mth.inverseLerp(start, fDepthStart, fDepthEnd);
        double fEndFactor = Mth.inverseLerp(end, fDepthStart, fDepthEnd);

        double tDepthStart = Mth.clampedMap(to.getMinY(), -64, 320, 1.5, -1.5);
        double tDepthEnd = Mth.clampedMap(to.getMinY() + to.getHeight(), -64, 320, 1.5, -1.5);

        float nStart = (float) Mth.lerp(fStartFactor, tDepthStart, tDepthEnd);
        float nEnd = (float) Mth.lerp(fEndFactor, tDepthStart, tDepthEnd);

        return Climate.Parameter.span(nStart, nEnd);
    }

    private static MultiverseShape randomShape(RandomSource random) {
        Map<MultiverseShape, Integer> shapes = ShapesManager.INSTANCE.getShapes();
        int totalWeight = shapes.values().stream().mapToInt(Integer::intValue).sum();
        int selected = random.nextInt(totalWeight);
        int current = 0;
        for (MultiverseShape type : shapes.keySet()) {
            current += shapes.get(type);
            if (selected < current) {
                return type;
            }
        }
        throw new RuntimeException();
    }

    private static Pair<MultiverseType, Set<ResourceKey<Biome>>> randomBiomes(Registry<Biome> registry, RandomSource random) {
        Set<MultiverseType> biomesTypes = EnumSet.allOf(MultiverseType.class);
        Predicate<ResourceKey<Biome>> valid = key -> biomesTypes.stream().anyMatch(type -> type.is(key));
        Set<BiomeType> types = BiomesManager.INSTANCE.getBiomeTypes().stream().filter(type -> type.getBiomes(registry).stream().anyMatch(valid)).collect(Collectors.toCollection(HashSet::new));
        Set<ResourceKey<Biome>> biomes = new HashSet<>();
        if (types.isEmpty()) {
            biomes.add(Biomes.PLAINS);
        } else {
            BiomeType type = selectRandom(random, types);
            types.remove(type);
            biomes.addAll(type.getBiomes(registry));
        }
        double chance = ServerConfigs.INSTANCE.additionalBiomeTypeChance.get();
        int count = types.size();
        for (int i = 0; i < count; i++) {
            if (random.nextDouble() < chance) {
                BiomeType type = selectRandom(random, types);
                types.remove(type);
                biomes.addAll(type.getBiomes(registry));
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

    private static BiomeType selectRandom(RandomSource random, Set<BiomeType> types) {
        int total = types.stream().mapToInt(BiomeType::getWeight).sum();
        int selected = random.nextInt(total);
        for (BiomeType type : types) {
            total -= type.getWeight();
            if (total <= selected) {
                return type;
            }
        }
        throw new RuntimeException();
    }

}
