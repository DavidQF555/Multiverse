package io.github.davidqf555.minecraft.multiverse.registration;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftTileEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class TileEntityRegistry {

    public static final DeferredRegister<BlockEntityType<?>> TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Multiverse.MOD_ID);

    private TileEntityRegistry() {
    }

    private static <T extends BlockEntityType<?>> DeferredHolder<BlockEntityType<?>, T> register(String name, Supplier<T> type) {
        return TYPES.register(name, type);
    }

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RiftTileEntity>> RIFT = register("rift", () -> BlockEntityType.Builder.of(RiftTileEntity::new, BlockRegistry.RIFT.get()).build(null));


}
