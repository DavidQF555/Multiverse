package io.github.davidqf555.minecraft.multiverse.registration.custom;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseShape;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class MultiverseShapeRegistry {

    public static final ResourceKey<Registry<MultiverseShape>> LOCATION = ResourceKey.createRegistryKey(new ResourceLocation(Multiverse.MOD_ID, "multiverse_shape"));
    private static Supplier<IForgeRegistry<MultiverseShape>> registry = null;

    private MultiverseShapeRegistry() {
    }

    public static IForgeRegistry<MultiverseShape> getRegistry() {
        return registry.get();
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<MultiverseShape>().setType(MultiverseShape.class).setName(LOCATION.location()).dataPackRegistry(MultiverseShape.DIRECT_CODEC));
    }

}
