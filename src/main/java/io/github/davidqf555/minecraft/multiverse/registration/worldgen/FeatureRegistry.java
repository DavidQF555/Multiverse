package io.github.davidqf555.minecraft.multiverse.registration.worldgen;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.features.RiftConfig;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.features.RiftFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class FeatureRegistry {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, Multiverse.MOD_ID);

    public static final DeferredHolder<Feature<?>, RiftFeature> RIFT = register("rift", () -> new RiftFeature(RiftConfig.CODEC));

    private FeatureRegistry() {
    }

    private static <T extends Feature<?>> DeferredHolder<Feature<?>, T> register(String name, Supplier<T> feature) {
        return FEATURES.register(name, feature);
    }

}
