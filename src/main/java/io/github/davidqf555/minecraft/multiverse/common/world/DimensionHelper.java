package io.github.davidqf555.minecraft.multiverse.common.world;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.packets.UpdateClientDimensionsPacket;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.data.worldgen.TerrainProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.*;
import java.util.function.Supplier;
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
        dimensionGeneratorSettings.dimensions().register(dimensionKey, dimension, Lifecycle.experimental());
        DerivedLevelData derivedWorldInfo = new DerivedLevelData(serverConfig, serverConfig.overworldData());
        ServerLevel newWorld = new ServerLevel(server, server.executor, server.storageSource, derivedWorldInfo, worldKey, dimension.type(), server.progressListenerFactory.create(11), dimension.generator(), dimensionGeneratorSettings.isDebug(), BiomeManager.obfuscateSeed(dimensionGeneratorSettings.seed()), ImmutableList.of(), false);
        overworld.getWorldBorder().addListener(new BorderChangeListener.DelegateBorderChangeListener(newWorld.getWorldBorder()));
        map.put(worldKey, newWorld);
        server.markWorldsDirty();
        MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(newWorld));
        Multiverse.CHANNEL.send(PacketDistributor.ALL.noArg(), new UpdateClientDimensionsPacket(worldKey));
        return newWorld;
    }

    private static LevelStem createDimension(MinecraftServer server, int index) {
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        long seed = overworld.getSeed() + index * 80000L;
        WorldgenRandom random = new WorldgenRandom(new XoroshiroRandomSource(seed));
        Pair<Boolean, Boolean> bounds = randomBounds(random);
        boolean floor = bounds.getFirst();
        boolean ceiling = bounds.getSecond();
        NoiseGeneratorSettings settings = createSettings(ceiling, floor);
        float lighting = ceiling ? random.nextFloat() * 0.5f + 0.1f : random.nextFloat() * 0.2f;
        OptionalLong time = ceiling ? OptionalLong.of(18000) : randomTime(random);
        Set<ResourceKey<Biome>> biomes = randomBiomes(random);
        BiomeSource provider = new MultiNoiseBiomeSource.Preset(getRegistryKey(index).location(), registry -> new Climate.ParameterList<>(biomes.stream()
                .map(key -> (Supplier<Biome>) () -> registry.getOrThrow(key))
                .map(sup -> {
                    Biome biome = sup.get();
                    return Pair.of(Climate.parameters(biome.getBaseTemperature(), biome.getDownfall(), 0, 0, 0, 0, 0), sup);
                }).collect(Collectors.toList()))).biomeSource(server.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY));
        ChunkGenerator generator = new MultiverseChunkGenerator(server.registryAccess().registryOrThrow(Registry.NOISE_REGISTRY), provider, seed, () -> settings);
        ResourceLocation effect = randomEffect(time.isPresent() && time.getAsLong() < 22300 && time.getAsLong() > 13188, random);
        DimensionType type = createDimensionType(ceiling, time, effect, lighting);
        return new LevelStem(() -> type, generator);
    }

    private static DimensionType createDimensionType(boolean ceiling, OptionalLong time, ResourceLocation effect, float light) {
        return DimensionType.create(time, !ceiling, ceiling, false, true, 1, false, false, true, true, true, 0, ceiling ? 128 : 256, 128, BlockTags.INFINIBURN_OVERWORLD.getName(), effect, light);
    }

    private static NoiseGeneratorSettings createSettings(boolean ceiling, boolean floor) {
        int height = ceiling || !floor ? 128 : 256;
        int sizeHorizontal;
        int sizeVertical;
        int topTarget;
        int topSize;
        int topOffset;
        int bottomTarget;
        int bottomSize;
        int bottomOffset;
        TerrainShaper shaper;
        double xzScale;
        double yScale;
        double xzFactor = 80;
        double yFactor;
        int seaLevel;
        if (floor) {
            sizeHorizontal = 1;
            sizeVertical = 2;
            if (ceiling) {
                topTarget = 120;
                topSize = 3;
                topOffset = 0;
                bottomTarget = 320;
                bottomSize = 4;
                bottomOffset = -1;
                shaper = TerrainProvider.nether();
                yFactor = 60;
                seaLevel = 32;
                xzScale = 1;
                yScale = 3;
            } else {
                topTarget = -10;
                topSize = 3;
                topOffset = 0;
                bottomTarget = -30;
                bottomSize = 0;
                bottomOffset = 0;
                shaper = TerrainProvider.overworld(false);
                yFactor = 160;
                seaLevel = 63;
                xzScale = 1;
                yScale = 1;
            }
        } else {
            if (ceiling) {
                sizeHorizontal = 1;
                sizeVertical = 2;
                topTarget = 120;
                topSize = 3;
                topOffset = 0;
                bottomTarget = -30;
                bottomSize = 7;
                bottomOffset = 1;
                shaper = TerrainProvider.nether();
                yFactor = 30;
                xzScale = 1;
            } else {
                sizeHorizontal = 2;
                sizeVertical = 1;
                topTarget = -3000;
                topSize = 64;
                topOffset = -46;
                bottomTarget = -30;
                bottomSize = 7;
                bottomOffset = 1;
                shaper = TerrainProvider.floatingIslands();
                yFactor = 160;
                xzScale = 2;
            }
            yScale = 1;
            seaLevel = 0;
        }
        NoiseSlider topSlide = new NoiseSlider(topTarget, topSize, topOffset);
        NoiseSlider bottomSlide = new NoiseSlider(bottomTarget, bottomSize, bottomOffset);
        NoiseSamplingSettings sampling = new NoiseSamplingSettings(xzScale, yScale, xzFactor, yFactor);
        NoiseSettings noise = NoiseSettings.create(0, height, sampling, topSlide, bottomSlide, sizeHorizontal, sizeVertical, false, false, false, shaper);
        Map<StructureFeature<?>, StructureFeatureConfiguration> map = new HashMap<>(StructureSettings.DEFAULTS);
        if (ceiling) {
            for (StructureFeature<?> structure : new HashSet<>(map.keySet())) {
                if (structure.step() == GenerationStep.Decoration.SURFACE_STRUCTURES) {
                    map.remove(structure);
                }
            }
        }
        StructureSettings structures = new StructureSettings(Optional.empty(), map);
        SurfaceRules.RuleSource rules = SurfaceRuleData.overworldLike(true, ceiling, floor);
        return new NoiseGeneratorSettings(structures, noise, Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(), rules, seaLevel, false, true, true, true, true, false);
    }

    private static Set<ResourceKey<Biome>> randomBiomes(Random random) {
        List<BiomeDictionary.Type> types = new ArrayList<>(BiomeDictionary.Type.getAll());
        Set<ResourceKey<Biome>> biomes = new HashSet<>(BiomeDictionary.getBiomes(types.get(random.nextInt(types.size()))));
        double chance = ServerConfigs.INSTANCE.additionalBiomeTypeChance.get();
        for (BiomeDictionary.Type type : types) {
            if (random.nextDouble() < chance) {
                biomes.addAll(BiomeDictionary.getBiomes(type));
            }
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
