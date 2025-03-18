package multiverse.registration.custom;

import com.mojang.serialization.MapCodec;
import multiverse.common.Multiverse;
import multiverse.common.world.worldgen.sea.FlatFluidPicker;
import multiverse.common.world.worldgen.sea.SerializableFluidPicker;
import multiverse.common.world.worldgen.sea.WaveFluidPicker;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Supplier;

@EventBusSubscriber(modid = Multiverse.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class FluidPickerTypeRegistry {

    public static final ResourceKey<Registry<MapCodec<? extends SerializableFluidPicker>>> LOCATION = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "fluid_pickers"));
    public static final DeferredRegister<MapCodec<? extends SerializableFluidPicker>> TYPES = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends SerializableFluidPicker>, MapCodec<FlatFluidPicker>> FLAT = register("flat", () -> FlatFluidPicker.CODEC);
    public static final DeferredHolder<MapCodec<? extends SerializableFluidPicker>, MapCodec<WaveFluidPicker>> WAVE = register("wave", () -> WaveFluidPicker.CODEC);
    private static Registry<MapCodec<? extends SerializableFluidPicker>> registry = null;

    private FluidPickerTypeRegistry() {
    }

    private static <T extends SerializableFluidPicker> DeferredHolder<MapCodec<? extends SerializableFluidPicker>, MapCodec<T>> register(String name, Supplier<MapCodec<T>> codec) {
        return TYPES.register(name, codec);
    }

    public static Registry<MapCodec<? extends SerializableFluidPicker>> getRegistry() {
        return registry;
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<>(LOCATION));
    }

}
