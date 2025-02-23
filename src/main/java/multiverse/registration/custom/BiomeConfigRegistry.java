package multiverse.registration.custom;

import multiverse.common.Multiverse;
import multiverse.common.world.worldgen.generators.biomes.BiomeConfig;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class BiomeConfigRegistry {

    public static final ResourceKey<Registry<BiomeConfig>> LOCATION = ResourceKey.createRegistryKey(new ResourceLocation(Multiverse.MOD_ID, "biome_config"));

    private BiomeConfigRegistry() {
    }

    @SubscribeEvent
    public static void onNewDataPackRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(LOCATION, BiomeConfig.DIRECT_CODEC);
    }


}
