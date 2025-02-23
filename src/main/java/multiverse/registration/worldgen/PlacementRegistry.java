package multiverse.registration.worldgen;

import com.mojang.serialization.MapCodec;
import multiverse.common.Multiverse;
import multiverse.common.world.worldgen.features.placement.DimensionPlacement;
import multiverse.common.world.worldgen.features.placement.SolidPlacement;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class PlacementRegistry {

    public static final DeferredRegister<PlacementModifierType<?>> TYPES = DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, Multiverse.MOD_ID);

    public static final DeferredHolder<PlacementModifierType<?>, PlacementModifierType<DimensionPlacement>> DIMENSION = register("dimension", DimensionPlacement.CODEC);
    public static final DeferredHolder<PlacementModifierType<?>, PlacementModifierType<SolidPlacement>> SOLID = register("solid", SolidPlacement.CODEC);

    private PlacementRegistry() {
    }

    private static <T extends PlacementModifier> DeferredHolder<PlacementModifierType<?>, PlacementModifierType<T>> register(String name, MapCodec<T> codec) {
        return TYPES.register(name, () -> () -> codec);
    }

}
