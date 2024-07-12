package io.github.davidqf555.minecraft.multiverse.registration.worldgen;

import com.mojang.serialization.MapCodec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.aquifers.FlatFluidPicker;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.aquifers.SerializableFluidPicker;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.aquifers.WaveFluidPicker;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import javax.annotation.Nullable;

@EventBusSubscriber(modid = Multiverse.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class SerializableFluidPickerRegistry {

    public static final ResourceKey<Registry<MapCodec<? extends SerializableFluidPicker>>> LOCATION = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "fluid_picker"));
    public static final DeferredRegister<MapCodec<? extends SerializableFluidPicker>> CODECS = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends SerializableFluidPicker>, MapCodec<FlatFluidPicker>> FLAT = register("flat", FlatFluidPicker.CODEC);
    public static final DeferredHolder<MapCodec<? extends SerializableFluidPicker>, MapCodec<WaveFluidPicker>> WAVE = register("wave", WaveFluidPicker.CODEC);

    private static Registry<MapCodec<? extends SerializableFluidPicker>> registry;

    private SerializableFluidPickerRegistry() {
    }

    public static <T extends SerializableFluidPicker> DeferredHolder<MapCodec<? extends SerializableFluidPicker>, MapCodec<T>> register(String name, MapCodec<T> codec) {
        return CODECS.register(name, () -> codec);
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<>(LOCATION));
    }

    @Nullable
    public static Registry<MapCodec<? extends SerializableFluidPicker>> getRegistry() {
        return registry;
    }

}
