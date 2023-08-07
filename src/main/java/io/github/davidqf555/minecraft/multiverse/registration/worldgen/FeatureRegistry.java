package io.github.davidqf555.minecraft.multiverse.registration.worldgen;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftBlock;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.features.RiftConfig;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.features.RiftFeature;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.features.placement.AboveGroundPlacement;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.features.placement.MultiverseDimensionPlacement;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.features.placement.RiftDimensionPlacement;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.features.placement.SolidPlacement;
import io.github.davidqf555.minecraft.multiverse.registration.BlockRegistry;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class FeatureRegistry {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, Multiverse.MOD_ID);

    public static final RegistryObject<RiftFeature> RIFT = register("rift", () -> new RiftFeature(RiftConfig.CODEC));
    public static Holder<PlacedFeature> PLACED_RIFT, KALEIDITE_CLUSTER;

    private FeatureRegistry() {
    }

    private static <T extends Feature<?>> RegistryObject<T> register(String name, Supplier<T> feature) {
        return FEATURES.register(name, feature);
    }

    @SubscribeEvent
    public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            PLACED_RIFT = register(new ResourceLocation(Multiverse.MOD_ID, "rift"), new ConfiguredFeature<>(RIFT.get(), RiftConfig.of(Optional.empty(), BlockRegistry.RIFT.get().defaultBlockState().setValue(RiftBlock.TEMPORARY, false), true)), RarityFilter.onAverageOnceEvery(ServerConfigs.INSTANCE.riftChance.get()), AboveGroundPlacement.INSTANCE, InSquarePlacement.spread(), RiftDimensionPlacement.INSTANCE);
            KALEIDITE_CLUSTER = register(new ResourceLocation(Multiverse.MOD_ID, "kaleidite_cluster"), new ConfiguredFeature<>(Feature.SIMPLE_RANDOM_SELECTOR, new SimpleRandomFeatureConfiguration(HolderSet.direct(FeatureRegistry::getDirectional, Direction.values()))), MultiverseDimensionPlacement.INSTANCE, PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, CountPlacement.of(16), InSquarePlacement.spread(), BiomeFilter.biome());
        });
    }

    private static Holder<PlacedFeature> getDirectional(Direction direction) {
        return PlacementUtils.inlinePlaced(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(BlockRegistry.KALEIDITE_CLUSTER.get().defaultBlockState().setValue(AmethystClusterBlock.FACING, direction))), SolidPlacement.of(direction.getOpposite(), 4));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<PlacedFeature> register(ResourceLocation name, ConfiguredFeature<FC, F> feature, PlacementModifier... placement) {
        Holder<ConfiguredFeature<?, ?>> placed = BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_FEATURE, name, feature);
        return PlacementUtils.register(name.getPath(), placed, placement);
    }
}
