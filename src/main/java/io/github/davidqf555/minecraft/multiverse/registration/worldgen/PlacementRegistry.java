package io.github.davidqf555.minecraft.multiverse.registration.worldgen;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.features.placement.DimensionPlacement;
import io.github.davidqf555.minecraft.multiverse.common.world.worldgen.features.placement.SolidPlacement;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class PlacementRegistry {

    public static final DeferredRegister<PlacementModifierType<?>> TYPES = DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, Multiverse.MOD_ID);

    public static final RegistryObject<PlacementModifierType<DimensionPlacement>> DIMENSION = register("dimension", DimensionPlacement.CODEC);
    public static final RegistryObject<PlacementModifierType<SolidPlacement>> SOLID = register("solid", SolidPlacement.CODEC);

    private PlacementRegistry() {
    }

    private static <T extends PlacementModifier> RegistryObject<PlacementModifierType<T>> register(String name, Codec<T> codec) {
        return TYPES.register(name, () -> () -> codec);
    }

}
