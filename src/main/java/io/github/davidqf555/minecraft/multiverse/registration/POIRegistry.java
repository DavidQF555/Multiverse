package io.github.davidqf555.minecraft.multiverse.registration;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.block.BlockState;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;
import java.util.function.Supplier;

public final class POIRegistry {

    public static final DeferredRegister<PointOfInterestType> TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, Multiverse.MOD_ID);

    public static final RegistryObject<PointOfInterestType> RIFT = register("rift", () -> PointOfInterestType.getBlockStates(BlockRegistry.RIFT.get()), 0, 1);

    private POIRegistry() {
    }

    private static RegistryObject<PointOfInterestType> register(String name, Supplier<Set<BlockState>> blocks, int tickets, int range) {
        return TYPES.register(name, () -> new PointOfInterestType(name, blocks.get(), tickets, range));
    }
}
