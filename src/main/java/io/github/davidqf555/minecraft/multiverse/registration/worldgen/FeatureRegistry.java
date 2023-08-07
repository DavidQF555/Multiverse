package io.github.davidqf555.minecraft.multiverse.registration.worldgen;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseChunkGenerator;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.features.RiftConfig;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.features.RiftFeature;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.features.placement.AboveGroundPlacement;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.features.placement.MultiverseDimensionPlacement;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.features.placement.RiftDimensionPlacement;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.features.placement.SolidPlacement;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Multiverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class FeatureRegistry {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, Multiverse.MOD_ID);

    public static final RegistryObject<RiftFeature> RIFT = register("rift", () -> new RiftFeature(RiftConfig.CODEC));
    public static PlacementModifierType<AboveGroundPlacement> ABOVE_GROUND;
    public static PlacementModifierType<RiftDimensionPlacement> RIFT_DIMENSION;
    public static PlacementModifierType<MultiverseDimensionPlacement> MULTIVERSE_DIMENSION;
    public static PlacementModifierType<SolidPlacement> SOLID;

    private FeatureRegistry() {
    }

    private static <T extends Feature<?>> RegistryObject<T> register(String name, Supplier<T> feature) {
        return FEATURES.register(name, feature);
    }

    @SubscribeEvent
    public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Registry.register(BuiltInRegistries.CHUNK_GENERATOR, new ResourceLocation(Multiverse.MOD_ID, "multiverse"), MultiverseChunkGenerator.CODEC);
            ABOVE_GROUND = Registry.register(BuiltInRegistries.PLACEMENT_MODIFIER_TYPE, new ResourceLocation(Multiverse.MOD_ID, "above_ground"), () -> AboveGroundPlacement.CODEC);
            RIFT_DIMENSION = Registry.register(BuiltInRegistries.PLACEMENT_MODIFIER_TYPE, new ResourceLocation(Multiverse.MOD_ID, "rift_dimension"), () -> RiftDimensionPlacement.CODEC);
            MULTIVERSE_DIMENSION = Registry.register(BuiltInRegistries.PLACEMENT_MODIFIER_TYPE, new ResourceLocation(Multiverse.MOD_ID, "multiverse"), () -> MultiverseDimensionPlacement.CODEC);
            SOLID = Registry.register(BuiltInRegistries.PLACEMENT_MODIFIER_TYPE, new ResourceLocation(Multiverse.MOD_ID, "solid"), () -> SolidPlacement.CODEC);
        });
    }

}
