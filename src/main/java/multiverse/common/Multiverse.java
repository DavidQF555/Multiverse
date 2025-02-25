package multiverse.common;

import multiverse.client.ClientConfigs;
import multiverse.registration.*;
import multiverse.registration.custom.DimensionGeneratorTypeRegistry;
import multiverse.registration.custom.FluidPickerTypeRegistry;
import multiverse.registration.custom.SeaLevelGeneratorTypeRegistry;
import multiverse.registration.custom.biomes.*;
import multiverse.registration.worldgen.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;

@Mod(Multiverse.MOD_ID)
public class Multiverse {

    public static final String MOD_ID = "multiverse";
    public static final SimpleChannel CHANNEL = ChannelBuilder.named(new ResourceLocation(MOD_ID, MOD_ID)).simpleChannel();

    public Multiverse() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfigs.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfigs.SPEC);
        addRegistries(FMLJavaModLoadingContext.get().getModEventBus());
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void addRegistries(IEventBus bus) {
        BlockRegistry.BLOCKS.register(bus);
        EntityRegistry.TYPES.register(bus);
        FeatureRegistry.FEATURES.register(bus);
        ItemRegistry.ITEMS.register(bus);
        POIRegistry.TYPES.register(bus);
        TileEntityRegistry.TYPES.register(bus);
        ParticleTypeRegistry.TYPES.register(bus);
        PlacementRegistry.TYPES.register(bus);
        ChunkGeneratorRegistry.GENERATORS.register(bus);
        CreativeModeTabRegistry.TABS.register(bus);
        SurfaceRuleSourceRegistry.SOURCES.register(bus);
        EffectRegistry.EFFECTS.register(bus);
        BiomeModifierRegistry.TYPES.register(bus);

        BiomeChunkGeneratorGeneratorTypeRegistry.TYPES.register(bus);
        BiomeDimensionGeneratorTypeRegistry.TYPES.register(bus);
        BiomeDimensionTypeGeneratorTypeRegistry.TYPES.register(bus);
        BiomeNoiseGeneratorSettingsGeneratorTypeRegistry.TYPES.register(bus);
        BiomeSourceGeneratorTypeRegistry.TYPES.register(bus);
        DimensionGeneratorTypeRegistry.TYPES.register(bus);
        FluidPickerTypeRegistry.TYPES.register(bus);
        SeaLevelGeneratorTypeRegistry.TYPES.register(bus);
    }

}
