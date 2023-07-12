package io.github.davidqf555.minecraft.registration;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public final class TileEntityRegistry {

    public static final DeferredRegister<TileEntityType<?>> TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Multiverse.MOD_ID);

    private TileEntityRegistry() {
    }

    private static <T extends TileEntityType<?>> RegistryObject<T> register(String name, Supplier<T> type) {
        return TYPES.register(name, type);
    }

    public static final RegistryObject<TileEntityType<RiftTileEntity>> RIFT = register("rift", () -> TileEntityType.Builder.of(RiftTileEntity::new, BlockRegistry.RIFT.get()).build(null));


}
