package io.github.davidqf555.minecraft.multiverse.registration;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.util.TagUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

public final class DataComponentTypeRegistry {

    public static final DeferredRegister.DataComponents TYPES = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Multiverse.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceKey<Level>>> TARGET = register("target", builder -> builder.persistent(ResourceKey.codec(Registries.DIMENSION)).networkSynchronized(TagUtil.WORLD_CODEC));

    private DataComponentTypeRegistry() {
    }

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> type) {
        return TYPES.registerComponentType(name, type);
    }

}
