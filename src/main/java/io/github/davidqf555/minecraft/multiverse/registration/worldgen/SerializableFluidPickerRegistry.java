package io.github.davidqf555.minecraft.multiverse.registration.worldgen;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.aquifers.FlatFluidPicker;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.aquifers.SerializableFluidPicker;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.aquifers.WaveFluidPicker;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class SerializableFluidPickerRegistry {

    public static final ResourceLocation REGISTRY_LOCATION = new ResourceLocation(Multiverse.MOD_ID, "fluid_picker");
    public static final DeferredRegister<Codec<? extends SerializableFluidPicker>> CODECS = DeferredRegister.create(REGISTRY_LOCATION, Multiverse.MOD_ID);
    public static final RegistryObject<Codec<FlatFluidPicker>> FLAT = register("flat", FlatFluidPicker.CODEC);
    public static final RegistryObject<Codec<WaveFluidPicker>> WAVE = register("wave", WaveFluidPicker.CODEC);
    private static IForgeRegistry<Codec<? extends SerializableFluidPicker>> registry;

    private SerializableFluidPickerRegistry() {
    }

    public static <T extends SerializableFluidPicker> RegistryObject<Codec<T>> register(String name, Codec<T> codec) {
        return CODECS.register(name, () -> codec);
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        event.create(new RegistryBuilder<Codec<? extends SerializableFluidPicker>>().setName(REGISTRY_LOCATION), r -> registry = r);
    }

    @Nullable
    public static IForgeRegistry<Codec<? extends SerializableFluidPicker>> getRegistry() {
        return registry;
    }

}
