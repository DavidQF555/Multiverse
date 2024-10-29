package io.github.davidqf555.minecraft.multiverse.registration.custom;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.sea_level.fluid_pickers.FlatFluidPicker;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.sea_level.fluid_pickers.SerializableFluidPicker;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.chunk_gen.sea_level.fluid_pickers.WaveFluidPicker;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class FluidPickerTypeRegistry {

    public static final ResourceKey<Registry<Codec<? extends SerializableFluidPicker>>> LOCATION = ResourceKey.createRegistryKey(new ResourceLocation(Multiverse.MOD_ID, "fluid_picker"));
    public static final DeferredRegister<Codec<? extends SerializableFluidPicker>> TYPES = DeferredRegister.create(LOCATION, Multiverse.MOD_ID);
    public static final RegistryObject<Codec<FlatFluidPicker>> FLAT = register("flat", () -> FlatFluidPicker.CODEC);
    public static final RegistryObject<Codec<WaveFluidPicker>> WAVE = register("wave", () -> WaveFluidPicker.CODEC);
    private static Supplier<IForgeRegistry<Codec<? extends SerializableFluidPicker>>> registry = null;

    private FluidPickerTypeRegistry() {
    }

    private static <T extends SerializableFluidPicker> RegistryObject<Codec<T>> register(String name, Supplier<Codec<T>> codec) {
        return TYPES.register(name, codec);
    }

    public static IForgeRegistry<Codec<? extends SerializableFluidPicker>> getRegistry() {
        return registry.get();
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<Codec<? extends SerializableFluidPicker>>().setName(LOCATION.location()));
    }

}
