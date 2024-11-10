package io.github.davidqf555.minecraft.multiverse.common;

import io.github.davidqf555.minecraft.multiverse.registration.*;
import io.github.davidqf555.minecraft.multiverse.registration.custom.DimensionProviderTypeRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.custom.FluidPickerTypeRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.custom.SeaLevelProviderTypeRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.custom.biomes.*;
import io.github.davidqf555.minecraft.multiverse.registration.worldgen.ChunkGeneratorRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.worldgen.FeatureRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.worldgen.PlacementRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@Mod("multiverse")
public class Multiverse {

    public static final String MOD_ID = "multiverse";
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MOD_ID, MOD_ID),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public Multiverse() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfigs.SPEC);
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
        BiomeChunkGeneratorProviderTypeRegistry.TYPES.register(bus);
        BiomeDimensionProviderTypeRegistry.TYPES.register(bus);
        BiomeDimensionTypeProviderTypeRegistry.TYPES.register(bus);
        BiomeNoiseGeneratorSettingsProviderTypeRegistry.TYPES.register(bus);
        BiomeSourceProviderTypeRegistry.TYPES.register(bus);
        DimensionProviderTypeRegistry.TYPES.register(bus);
        FluidPickerTypeRegistry.TYPES.register(bus);
        SeaLevelProviderTypeRegistry.TYPES.register(bus);
    }

}
