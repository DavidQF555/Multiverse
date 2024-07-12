package io.github.davidqf555.minecraft.multiverse.registration;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class DataComponentTypeRegistry {

    public static final DeferredRegister<DataComponentType<?>> TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Multiverse.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> TARGET = register("target", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

    private DataComponentTypeRegistry() {
    }

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, Supplier<DataComponentType<T>> type) {
        return TYPES.register(name, type);
    }

}
