package multiverse.registration.custom.generator;

import multiverse.common.Multiverse;
import multiverse.common.world.worldgen.generators.biomes.BiomeConfig;
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
public final class BiomeConfigRegistry {

    public static final ResourceKey<Registry<BiomeConfig>> LOCATION = ResourceKey.createRegistryKey(new ResourceLocation(Multiverse.MOD_ID, "generator/biome_configs"));
    private static Supplier<IForgeRegistry<BiomeConfig>> registry = null;

    private BiomeConfigRegistry() {
    }

    public static IForgeRegistry<BiomeConfig> getRegistry() {
        return registry.get();
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<BiomeConfig>().setType(BiomeConfig.class).setName(LOCATION.location()).dataPackRegistry(BiomeConfig.DIRECT_CODEC));
    }

}
