package multiverse.registration.worldgen;

import multiverse.common.Multiverse;
import multiverse.common.world.worldgen.features.RiftConfig;
import multiverse.common.world.worldgen.features.RiftFeature;
import multiverse.common.world.worldgen.features.WaterLoggedBlockFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class FeatureRegistry {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, Multiverse.MOD_ID);

    public static final DeferredHolder<Feature<?>, RiftFeature> RIFT = register("rift", () -> new RiftFeature(RiftConfig.CODEC));
    public static final DeferredHolder<Feature<?>, WaterLoggedBlockFeature> WATERLOGGED_BLOCK = register("waterlogged_block", () -> new WaterLoggedBlockFeature(SimpleBlockConfiguration.CODEC));

    private FeatureRegistry() {
    }

    private static <T extends Feature<?>> DeferredHolder<Feature<?>, T> register(String name, Supplier<T> feature) {
        return FEATURES.register(name, feature);
    }

}
