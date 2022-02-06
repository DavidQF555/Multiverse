package io.github.davidqf555.minecraft.multiverse.common.world;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.packets.UpdateClientDimensionsPacket;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.ColumnFuzzedBiomeMagnifier;
import net.minecraft.world.biome.FuzzedBiomeMagnifier;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.NetherBiomeProvider;
import net.minecraft.world.border.IBorderListener;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class DimensionHelper {

    private DimensionHelper() {
    }

    @SuppressWarnings("deprecation")
    public static ServerWorld getOrCreateWorld(MinecraftServer server, int index) {
        if (index <= 0) {
            return server.getLevel(World.OVERWORLD);
        }
        RegistryKey<World> world = getRegistryKey(index);
        Map<RegistryKey<World>, ServerWorld> map = server.forgeGetWorldMap();
        if (map.containsKey(world)) {
            return map.get(world);
        }
        return createAndRegisterWorldAndDimension(server, map, world, index);
    }

    private static RegistryKey<World> getRegistryKey(int index) {
        return RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(Multiverse.MOD_ID, index + ""));
    }

    public static int getIndex(RegistryKey<World> world) {
        if (world.location().getNamespace().equals(Multiverse.MOD_ID)) {
            return Integer.parseInt(world.location().getPath());
        }
        return 0;
    }

    @SuppressWarnings("deprecation")
    private static ServerWorld createAndRegisterWorldAndDimension(MinecraftServer server, Map<RegistryKey<World>, ServerWorld> map, RegistryKey<World> worldKey, int index) {
        ServerWorld overworld = server.getLevel(World.OVERWORLD);
        RegistryKey<Dimension> dimensionKey = RegistryKey.create(Registry.LEVEL_STEM_REGISTRY, worldKey.location());
        Dimension dimension = createDimension(server, index);
        IServerConfiguration serverConfig = server.getWorldData();
        DimensionGeneratorSettings dimensionGeneratorSettings = serverConfig.worldGenSettings();
        dimensionGeneratorSettings.dimensions().register(dimensionKey, dimension, Lifecycle.experimental());
        DerivedWorldInfo derivedWorldInfo = new DerivedWorldInfo(serverConfig, serverConfig.overworldData());
        ServerWorld newWorld = new ServerWorld(server, server.executor, server.storageSource, derivedWorldInfo, worldKey, dimension.type(), server.progressListenerFactory.create(11), dimension.generator(), dimensionGeneratorSettings.isDebug(), BiomeManager.obfuscateSeed(dimensionGeneratorSettings.seed()), ImmutableList.of(), false);
        overworld.getWorldBorder().addListener(new IBorderListener.Impl(newWorld.getWorldBorder()));
        map.put(worldKey, newWorld);
        server.markWorldsDirty();
        MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(newWorld));
        Multiverse.CHANNEL.send(PacketDistributor.ALL.noArg(), new UpdateClientDimensionsPacket(worldKey));
        return newWorld;
    }

    private static Dimension createDimension(MinecraftServer server, int index) {
        ServerWorld overworld = server.getLevel(World.OVERWORLD);
        long seed = overworld.getSeed() + index * 50000L;
        SharedSeedRandom random = new SharedSeedRandom(seed);
        boolean floor = random.nextBoolean();
        boolean ceiling = random.nextBoolean() && (floor || ServerConfigs.INSTANCE.inverse.get());
        float lighting = ceiling ? random.nextFloat() * 0.5f + 0.1f : random.nextFloat() * 0.2f;
        Registry<Biome> lookup = server.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
        BiomeProvider provider = new NetherBiomeProvider(seed, randomBiomes(random).stream().map(key -> (Supplier<Biome>) () -> lookup.getOrThrow(key)).map(sup -> {
            Biome biome = sup.get();
            return Pair.of(new Biome.Attributes(biome.getBaseTemperature(), biome.getDownfall(), 0, 0, 0), sup);
        }).collect(Collectors.toList()), Optional.empty());
        DimensionSettings settings = createSettings(ceiling, floor);
        ChunkGenerator generator = new MultiverseChunkGenerator(provider, seed, () -> settings);
        OptionalLong time = ceiling ? OptionalLong.of(18000) : randomTime(random);
        ResourceLocation effect = randomEffect(time.isPresent() && time.getAsLong() < 22300 && time.getAsLong() > 13188, random);
        DimensionType type = createDimensionType(ceiling, time, effect, lighting);
        return new Dimension(() -> type, generator);
    }

    private static DimensionType createDimensionType(boolean ceiling, OptionalLong time, ResourceLocation effect, float light) {
        return new DimensionType(time, !ceiling, ceiling, false, true, 1, false, false, true, true, true, ceiling ? 128 : 256, ceiling ? FuzzedBiomeMagnifier.INSTANCE : ColumnFuzzedBiomeMagnifier.INSTANCE, BlockTags.INFINIBURN_OVERWORLD.getName(), effect, light);
    }

    private static DimensionSettings createSettings(boolean ceiling, boolean floor) {
        int height = ceiling || !floor ? 128 : 256;
        int sizeHorizontal;
        int sizeVertical;
        int topTarget;
        int topSize;
        int topOffset;
        int bottomTarget;
        int bottomSize;
        int bottomOffset;
        double densityFactor;
        double densityOffset;
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
                densityFactor = 0;
                densityOffset = 0.02;
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
                densityFactor = 1;
                densityOffset = -0.5;
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
                densityFactor = 0;
                densityOffset = 0;
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
                densityFactor = 0;
                densityOffset = 0;
                yFactor = 160;
                xzScale = 2;
            }
            yScale = 1;
            seaLevel = 0;
        }
        int ceilingOffset = ceiling ? 0 : -10;
        int floorOffset = floor ? 0 : -10;
        SlideSettings topSlide = new SlideSettings(topTarget, topSize, topOffset);
        SlideSettings bottomSlide = new SlideSettings(bottomTarget, bottomSize, bottomOffset);
        ScalingSettings sampling = new ScalingSettings(xzScale, yScale, xzFactor, yFactor);
        NoiseSettings noise = new NoiseSettings(height, sampling, topSlide, bottomSlide, sizeHorizontal, sizeVertical, densityFactor, densityOffset, false, true, false, false);
        Map<Structure<?>, StructureSeparationSettings> map = new HashMap<>(DimensionStructuresSettings.DEFAULTS);
        if (ceiling) {
            for (Structure<?> structure : new HashSet<>(map.keySet())) {
                if (structure.step() == GenerationStage.Decoration.SURFACE_STRUCTURES) {
                    map.remove(structure);
                }
            }
        }
        DimensionStructuresSettings structures = new DimensionStructuresSettings(Optional.empty(), map);
        return new DimensionSettings(structures, noise, Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(), ceilingOffset, floorOffset, seaLevel, false);
    }

    private static Set<RegistryKey<Biome>> randomBiomes(Random random) {
        List<BiomeDictionary.Type> types = new ArrayList<>(BiomeDictionary.Type.getAll());
        Set<RegistryKey<Biome>> biomes = new HashSet<>(BiomeDictionary.getBiomes(types.get(random.nextInt(types.size()))));
        double chance = ServerConfigs.INSTANCE.additionalBiomeTypeChance.get();
        for (BiomeDictionary.Type type : types) {
            if (random.nextDouble() < chance) {
                biomes.addAll(BiomeDictionary.getBiomes(type));
            }
        }
        return biomes;
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
