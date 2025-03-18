package multiverse.registration.custom;

import com.mojang.serialization.Codec;
import multiverse.common.Multiverse;
import multiverse.common.world.worldgen.generators.biomes.chunk_gen.sea.FluidPickerType;
import multiverse.common.world.worldgen.sea.FlatFluidPicker;
import multiverse.common.world.worldgen.sea.SerializableFluidPicker;
import multiverse.common.world.worldgen.sea.WaveFluidPicker;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class FluidPickerTypeRegistry {

    public static final ResourceKey<Registry<FluidPickerType<?>>> LOCATION = ResourceKey.createRegistryKey(new ResourceLocation(Multiverse.MOD_ID, "fluid_pickers"));
    public static final DeferredRegister<FluidPickerType<?>> TYPES = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);
    public static final RegistryObject<FluidPickerType<FlatFluidPicker>> FLAT = register("flat", () -> FlatFluidPicker.CODEC);
    public static final RegistryObject<FluidPickerType<WaveFluidPicker>> WAVE = register("wave", () -> WaveFluidPicker.CODEC);
    private static Supplier<IForgeRegistry<FluidPickerType<?>>> registry = null;

    private FluidPickerTypeRegistry() {
    }

    private static <T extends SerializableFluidPicker> RegistryObject<FluidPickerType<T>> register(String name, Supplier<Codec<T>> codec) {
        return TYPES.register(name, () -> new FluidPickerType<>(codec.get()));
    }

    public static IForgeRegistry<FluidPickerType<?>> getRegistry() {
        return registry.get();
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<FluidPickerType<?>>().setName(LOCATION.location()).setType((Class<FluidPickerType<?>>) (Class<?>) FluidPickerType.class));
    }

}
