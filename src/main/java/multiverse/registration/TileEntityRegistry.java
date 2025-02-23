package multiverse.registration;

import multiverse.common.Multiverse;
import multiverse.common.world.blocks.RiftTileEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class TileEntityRegistry {

    public static final DeferredRegister<BlockEntityType<?>> TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Multiverse.MOD_ID);

    private TileEntityRegistry() {
    }

    private static <T extends BlockEntityType<?>> RegistryObject<T> register(String name, Supplier<T> type) {
        return TYPES.register(name, type);
    }

    public static final RegistryObject<BlockEntityType<RiftTileEntity>> RIFT = register("rift", () -> BlockEntityType.Builder.of(RiftTileEntity::new, BlockRegistry.RIFT.get()).build(null));


}
