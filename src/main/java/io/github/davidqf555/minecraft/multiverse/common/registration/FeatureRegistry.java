package io.github.davidqf555.minecraft.multiverse.common.registration;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftBlock;
import io.github.davidqf555.minecraft.multiverse.common.world.gen.features.RiftConfig;
import io.github.davidqf555.minecraft.multiverse.common.world.gen.features.RiftFeature;
import io.github.davidqf555.minecraft.multiverse.common.world.gen.features.placement.AboveGroundPlacement;
import io.github.davidqf555.minecraft.multiverse.common.world.gen.features.placement.RiftDimensionPlacement;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class FeatureRegistry {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, Multiverse.MOD_ID);

    public static final RegistryObject<RiftFeature> RIFT = register("rift", () -> new RiftFeature(RiftConfig.CODEC));
    public static final ConfiguredPlacement<NoPlacementConfig> ABOVE_GROUND = new AboveGroundPlacement(NoPlacementConfig.CODEC).configured(NoPlacementConfig.INSTANCE);
    public static final ConfiguredPlacement<NoPlacementConfig> RIFT_DIMENSION = new RiftDimensionPlacement(NoPlacementConfig.CODEC).configured(NoPlacementConfig.INSTANCE);
    public static ConfiguredFeature<?, ?> CONFIG_RIFT = null;

    private FeatureRegistry() {
    }

    private static <T extends Feature<?>> RegistryObject<T> register(String name, Supplier<T> feature) {
        return FEATURES.register(name, feature);
    }

    @SubscribeEvent
    public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CONFIG_RIFT = new ConfiguredFeature<>(RIFT.get(), RiftConfig.of(Optional.empty(), BlockRegistry.RIFT.get().defaultBlockState().setValue(RiftBlock.TEMPORARY, false), true)).chance(ServerConfigs.INSTANCE.riftChance.get()).decorated(ABOVE_GROUND).squared().decorated(RIFT_DIMENSION);
        });
    }
}
