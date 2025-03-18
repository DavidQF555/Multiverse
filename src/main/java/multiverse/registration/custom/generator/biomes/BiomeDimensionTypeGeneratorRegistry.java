package multiverse.registration.custom.generator.biomes;

import multiverse.common.Multiverse;
import multiverse.common.world.worldgen.generators.biomes.dim_type.BiomeDimensionTypeGenerator;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class BiomeDimensionTypeGeneratorRegistry {

    public static final ResourceKey<Registry<BiomeDimensionTypeGenerator>> LOCATION = ResourceKey.createRegistryKey(new ResourceLocation(Multiverse.MOD_ID, "generator/dimension_types"));

    private BiomeDimensionTypeGeneratorRegistry() {
    }

    @SubscribeEvent
    public static void onNewDataPackRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(LOCATION, BiomeDimensionTypeGenerator.DIRECT_CODEC);
    }

}
