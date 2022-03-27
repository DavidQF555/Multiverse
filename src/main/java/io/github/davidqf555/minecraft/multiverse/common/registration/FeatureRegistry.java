package io.github.davidqf555.minecraft.multiverse.common.registration;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftBlock;
import io.github.davidqf555.minecraft.multiverse.common.world.gen.features.RiftConfig;
import io.github.davidqf555.minecraft.multiverse.common.world.gen.features.RiftFeature;
import io.github.davidqf555.minecraft.multiverse.common.world.gen.features.placement.AboveGroundPlacement;
import io.github.davidqf555.minecraft.multiverse.common.world.gen.features.placement.RiftDimensionPlacement;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
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
    public static final AboveGroundPlacement ABOVE_GROUND = new AboveGroundPlacement();
    public static final RiftDimensionPlacement RIFT_DIMENSION = new RiftDimensionPlacement();
    public static PlacementModifierType<AboveGroundPlacement> ABOVE_GROUND_PLACEMENT_TYPE;
    public static PlacementModifierType<RiftDimensionPlacement> RIFT_DIMENSION_PLACEMENT_TYPE;
    public static Holder<PlacedFeature> PLACED_RIFT;

    private FeatureRegistry() {
    }

    private static <T extends Feature<?>> RegistryObject<T> register(String name, Supplier<T> feature) {
        return FEATURES.register(name, feature);
    }

    @SubscribeEvent
    public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ABOVE_GROUND_PLACEMENT_TYPE = Registry.register(Registry.PLACEMENT_MODIFIERS, new ResourceLocation(Multiverse.MOD_ID, "above_ground"), () -> AboveGroundPlacement.CODEC);
            RIFT_DIMENSION_PLACEMENT_TYPE = Registry.register(Registry.PLACEMENT_MODIFIERS, new ResourceLocation(Multiverse.MOD_ID, "rift_dimension"), () -> RiftDimensionPlacement.CODEC);
            PLACED_RIFT = register(new ResourceLocation(Multiverse.MOD_ID, "rift"), new ConfiguredFeature<>(RIFT.get(), RiftConfig.of(Optional.empty(), BlockRegistry.RIFT.get().defaultBlockState().setValue(RiftBlock.TEMPORARY, false), true)), RarityFilter.onAverageOnceEvery(ServerConfigs.INSTANCE.riftChance.get()), ABOVE_GROUND, InSquarePlacement.spread(), RIFT_DIMENSION);
        });
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<PlacedFeature> register(ResourceLocation name, ConfiguredFeature<FC, F> feature, PlacementModifier... placement) {
        Holder<ConfiguredFeature<?, ?>> placed = BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_FEATURE, name, feature);
        return PlacementUtils.register(name.getPath(), placed, placement);
    }
}
