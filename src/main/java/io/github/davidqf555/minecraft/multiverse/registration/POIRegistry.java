package io.github.davidqf555.minecraft.multiverse.registration;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;
import java.util.function.Supplier;

public final class POIRegistry {

    public static final DeferredRegister<PoiType> TYPES = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, Multiverse.MOD_ID);

    public static final DeferredHolder<PoiType, PoiType> RIFT = register("rift", () -> Set.copyOf(BlockRegistry.RIFT.get().getStateDefinition().getPossibleStates()), 0, 1);

    private POIRegistry() {
    }

    private static DeferredHolder<PoiType, PoiType> register(String name, Supplier<Set<BlockState>> blocks, int tickets, int range) {
        return TYPES.register(name, () -> new PoiType(blocks.get(), tickets, range));
    }
}
