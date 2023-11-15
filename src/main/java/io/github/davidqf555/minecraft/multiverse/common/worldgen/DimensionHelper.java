package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.packets.UpdateClientDimensionsPacket;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes.BiomeType;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes.MultiverseBiomeSource;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes.MultiverseBiomes;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.data.BiomesManager;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.data.EffectsManager;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.data.ShapesManager;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.phys.Vec3;
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
        return ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(Multiverse.MOD_ID, String.valueOf(index)));
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
        MultiverseShape shape = randomShape(random);
        boolean ceiling = shape.hasCeiling();
        RegistryAccess access = server.registryAccess();
        Registry<Biome> biomeRegistry = access.registryOrThrow(Registry.BIOME_REGISTRY);
        Pair<MultiverseType, Set<ResourceKey<Biome>>> pair = randomBiomes(biomeRegistry, random);
        Set<ResourceKey<Biome>> biomes = pair.getSecond();
        MultiverseType type = pair.getFirst();
        Holder<NoiseGeneratorSettings> settings = access.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY).getHolderOrThrow(shape.getNoiseSettingsKey(type));
        float lighting = ceiling ? random.nextFloat() * 0.5f + 0.1f : random.nextFloat() * 0.2f;
        OptionalLong time = ceiling ? OptionalLong.of(18000) : randomTime(random);
        BiomeSource provider = new MultiverseBiomeSource(getBiomeParameters(access, type, shape, biomes));
        ResourceLocation effect = randomEffect(random);
        Holder<DimensionType> dimType = createDimensionType(shape, type, time, effect, lighting);
        ChunkGenerator generator = new MultiverseChunkGenerator(access.registryOrThrow(Registry.STRUCTURE_SET_REGISTRY), server.registryAccess().registryOrThrow(Registry.NOISE_REGISTRY), provider, seed, settings, shape, index);
        return new LevelStem(dimType, generator);
    }

    private static Holder<DimensionType> createDimensionType(MultiverseShape shape, MultiverseType type, OptionalLong time, ResourceLocation effect, float light) {
        return Holder.direct(DimensionType.create(time, !shape.hasCeiling(), shape.hasCeiling(), type.isUltrawarm(), type.isNatural(), 1, false, type.isPiglinSafe(), true, true, type.hasRaids(), shape.getMinY(), shape.getHeight(), shape.getHeight(), type.getInfiniburn(), effect, light));
    }

    private static Climate.ParameterList<Holder<Biome>> getBiomeParameters(RegistryAccess access, MultiverseType type, MultiverseShape shape, Set<ResourceKey<Biome>> biomes) {
        MultiverseBiomes ref = BiomesManager.INSTANCE.getBiomes();
        Registry<Biome> biomeReg = access.registryOrThrow(Registry.BIOME_REGISTRY);
        Registry<DimensionType> dimTypeReg = access.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
        List<Pair<Climate.ParameterPoint, Holder<Biome>>> out = new ArrayList<>();
        for (ResourceKey<Biome> biome : biomes) {
            Holder<Biome> holder = biomeReg.getOrCreateHolder(biome);
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

    private static MultiverseShape randomShape(Random random) {
        Map<MultiverseShape, Integer> values = ShapesManager.INSTANCE.getShapes();
        int totalWeight = values.values().stream().mapToInt(Integer::intValue).sum();
        int selected = random.nextInt(totalWeight);
        int current = 0;
        for (MultiverseShape type : values.keySet()) {
            current += values.get(type);
            if (selected < current) {
                return type;
            }
        }
        throw new RuntimeException();
    }

    private static Pair<MultiverseType, Set<ResourceKey<Biome>>> randomBiomes(Registry<Biome> registry, Random random) {
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

    private static BiomeType selectRandom(Random random, Set<BiomeType> types) {
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

    private static OptionalLong randomTime(Random random) {
        if (random.nextDouble() < ServerConfigs.INSTANCE.fixedTimeChance.get()) {
            return OptionalLong.of(random.nextInt(24000));
        }
        return OptionalLong.empty();
    }

    private static ResourceLocation randomEffect(Random random) {
        Map<ResourceLocation, Integer> effects = EffectsManager.INSTANCE.getEffects();
        int total = effects.values().stream().mapToInt(Integer::intValue).sum();
        int rand = random.nextInt(total);
        for (ResourceLocation type : effects.keySet()) {
            total -= effects.get(type);
            if (rand >= total) {
                return type;
            }
        }
        throw new RuntimeException();
    }

}
