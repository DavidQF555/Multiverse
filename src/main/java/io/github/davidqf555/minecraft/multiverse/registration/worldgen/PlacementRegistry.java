package io.github.davidqf555.minecraft.multiverse.registration.worldgen;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.features.placement.AboveGroundPlacement;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.features.placement.MultiverseDimensionPlacement;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.features.placement.RiftDimensionPlacement;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.features.placement.SolidPlacement;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class PlacementRegistry {

    public static final DeferredRegister<PlacementModifierType<?>> TYPES = DeferredRegister.create(Registry.PLACEMENT_MODIFIER_REGISTRY, Multiverse.MOD_ID);

    public static final RegistryObject<PlacementModifierType<RiftDimensionPlacement>> RIFT_DIMENSION = register("rift_dimension", RiftDimensionPlacement.CODEC);
    public static final RegistryObject<PlacementModifierType<MultiverseDimensionPlacement>> MULTIVERSE = register("multiverse", MultiverseDimensionPlacement.CODEC);
    public static final RegistryObject<PlacementModifierType<SolidPlacement>> SOLID = register("solid", SolidPlacement.CODEC);
    public static final RegistryObject<PlacementModifierType<AboveGroundPlacement>> ABOVE_GROUND = register("above_ground", AboveGroundPlacement.CODEC);

    private PlacementRegistry() {
    }

    private static <T extends PlacementModifier> RegistryObject<PlacementModifierType<T>> register(String name, Codec<T> codec) {
        return TYPES.register(name, () -> () -> codec);
    }

}
