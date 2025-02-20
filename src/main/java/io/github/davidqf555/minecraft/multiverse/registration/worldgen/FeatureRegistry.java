package io.github.davidqf555.minecraft.multiverse.registration.worldgen;

import com.google.common.collect.ImmutableSet;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.world.blocks.RiftBlock;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.features.RiftConfig;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.features.RiftFeature;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.features.WaterLoggedBlockFeature;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.features.placement.DimensionPlacement;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.features.placement.SolidPlacement;
import io.github.davidqf555.minecraft.multiverse.registration.BlockRegistry;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleRandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public final class FeatureRegistry {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, Multiverse.MOD_ID);
    public static final DeferredRegister<PlacedFeature> PLACED = DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, Multiverse.MOD_ID);

    public static final RegistryObject<RiftFeature> RIFT = register("rift", () -> new RiftFeature(RiftConfig.CODEC));
    public static final RegistryObject<WaterLoggedBlockFeature> WATERLOGGED_BLOCK = register("waterlogged_block", () -> new WaterLoggedBlockFeature(SimpleBlockConfiguration.CODEC));

    private static final Set<ResourceKey<Level>> CLUSTER_DIM;
    private static final Set<ResourceKey<Level>> RIFT_DIM;
    static {
        ImmutableSet.Builder<ResourceKey<Level>> cluster = ImmutableSet.builder();
        ImmutableSet.Builder<ResourceKey<Level>> rift = ImmutableSet.builder();
        rift.add(Level.OVERWORLD);
        for (int i = 1; i <= 25; i++) {
            ResourceKey<Level> key = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(Multiverse.MOD_ID, i + ""));
            cluster.add(key);
            rift.add(key);
        }
        CLUSTER_DIM = cluster.build();
        RIFT_DIM = rift.build();
    }

    public static final RegistryObject<PlacedFeature> PLACED_RIFT = registerPlaced("rift", () -> new PlacedFeature(Holder.direct(new ConfiguredFeature<>(RIFT.get(), RiftConfig.of(BlockRegistry.RIFT.get().defaultBlockState().setValue(RiftBlock.TEMPORARY, false)))), List.of(new DimensionPlacement(RIFT_DIM), RarityFilter.onAverageOnceEvery(ServerConfigs.INSTANCE.riftChance.get()), BiomeFilter.biome())));
    public static final RegistryObject<PlacedFeature> KALEIDITE_CLUSTER = registerPlaced("kaleidite_cluster", () -> new PlacedFeature(Holder.direct(new ConfiguredFeature<>(Feature.SIMPLE_RANDOM_SELECTOR, new SimpleRandomFeatureConfiguration(HolderSet.direct(FeatureRegistry::getDirectional, Direction.values())))), List.of(new DimensionPlacement(CLUSTER_DIM), PlacementUtils.FULL_RANGE, CountPlacement.of(16), InSquarePlacement.spread(), BiomeFilter.biome())));

    private FeatureRegistry() {
    }

    private static <T extends Feature<?>> RegistryObject<T> register(String name, Supplier<T> feature) {
        return FEATURES.register(name, feature);
    }

    private static RegistryObject<PlacedFeature> registerPlaced(String name, Supplier<PlacedFeature> feature) {
        return PLACED.register(name, feature);
    }

    private static Holder<PlacedFeature> getDirectional(Direction direction) {
        return PlacementUtils.inlinePlaced(WATERLOGGED_BLOCK.get(), new SimpleBlockConfiguration(BlockStateProvider.simple(BlockRegistry.KALEIDITE_CLUSTER.get().defaultBlockState().setValue(AmethystClusterBlock.FACING, direction))), SolidPlacement.of(direction.getOpposite(), 4));
    }

}
