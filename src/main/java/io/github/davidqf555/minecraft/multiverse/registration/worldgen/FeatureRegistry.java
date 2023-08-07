package io.github.davidqf555.minecraft.multiverse.registration.worldgen;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftBlock;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.features.RiftConfig;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.features.RiftFeature;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.features.placement.MultiverseDimensionPlacement;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.features.placement.RiftDimensionPlacement;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.features.placement.SolidPlacement;
import io.github.davidqf555.minecraft.multiverse.registration.BlockRegistry;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleRandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public final class FeatureRegistry {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, Multiverse.MOD_ID);
    public static final DeferredRegister<PlacedFeature> PLACED = DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, Multiverse.MOD_ID);

    public static final RegistryObject<RiftFeature> RIFT = register("rift", () -> new RiftFeature(RiftConfig.CODEC));

    public static final RegistryObject<PlacedFeature> PLACED_RIFT = registerPlaced("rift", () -> new PlacedFeature(Holder.direct(new ConfiguredFeature<>(RIFT.get(), RiftConfig.of(Optional.empty(), BlockRegistry.RIFT.get().defaultBlockState().setValue(RiftBlock.TEMPORARY, false), true))), List.of(RiftDimensionPlacement.INSTANCE, PlacementUtils.FULL_RANGE, RarityFilter.onAverageOnceEvery(ServerConfigs.INSTANCE.riftChance.get()), InSquarePlacement.spread(), BiomeFilter.biome())));
    public static final RegistryObject<PlacedFeature> KALEIDITE_CLUSTER = registerPlaced("kaleidite_cluster", () -> new PlacedFeature(Holder.direct(new ConfiguredFeature<>(Feature.SIMPLE_RANDOM_SELECTOR, new SimpleRandomFeatureConfiguration(HolderSet.direct(FeatureRegistry::getDirectional, Direction.values())))), List.of(MultiverseDimensionPlacement.INSTANCE, PlacementUtils.FULL_RANGE, CountPlacement.of(16), InSquarePlacement.spread(), BiomeFilter.biome())));

    private FeatureRegistry() {
    }

    private static <T extends Feature<?>> RegistryObject<T> register(String name, Supplier<T> feature) {
        return FEATURES.register(name, feature);
    }

    private static RegistryObject<PlacedFeature> registerPlaced(String name, Supplier<PlacedFeature> feature) {
        return PLACED.register(name, feature);
    }

    private static Holder<PlacedFeature> getDirectional(Direction direction) {
        return PlacementUtils.inlinePlaced(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(BlockRegistry.KALEIDITE_CLUSTER.get().defaultBlockState().setValue(AmethystClusterBlock.FACING, direction))), SolidPlacement.of(direction.getOpposite(), 4));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<PlacedFeature> register(ResourceLocation name, ConfiguredFeature<FC, F> feature, PlacementModifier... placement) {
        Holder<ConfiguredFeature<?, ?>> placed = BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_FEATURE, name, feature);
        return PlacementUtils.register(name.getPath(), placed, placement);
    }
}
