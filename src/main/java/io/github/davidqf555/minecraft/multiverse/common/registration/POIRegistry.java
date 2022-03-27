package io.github.davidqf555.minecraft.multiverse.common.registration;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;
import java.util.function.Supplier;

public final class POIRegistry {

    public static final DeferredRegister<PoiType> TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, Multiverse.MOD_ID);

    public static final RegistryObject<PoiType> RIFT = register("rift", () -> PoiType.getBlockStates(BlockRegistry.RIFT.get()), 0, 1);

    private POIRegistry() {
    }

    private static RegistryObject<PoiType> register(String name, Supplier<Set<BlockState>> blocks, int tickets, int range) {
        return TYPES.register(name, () -> new PoiType(name, blocks.get(), tickets, range));
    }
}
