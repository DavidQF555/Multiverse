package io.github.davidqf555.minecraft.multiverse.common.blocks;

import io.github.davidqf555.minecraft.multiverse.common.world.DimensionHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public class RiftBlock extends ContainerBlock {

    public RiftBlock() {
        super(Properties.of(Material.PORTAL, MaterialColor.COLOR_BLACK)
                .noCollission()
                .noDrops()
        );
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader reader) {
        return new RiftTileEntity();
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canBeReplaced(BlockState state, Fluid fluid) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void entityInside(BlockState state, World world, BlockPos pos, Entity entity) {
        TileEntity tile = world.getBlockEntity(pos);
        if (entity.canChangeDimensions() && world instanceof ServerWorld && tile instanceof RiftTileEntity && !entity.isPassenger() && !entity.isVehicle()) {
            if (!entity.isOnPortalCooldown()) {
                ServerWorld target = DimensionHelper.getOrCreateWorld(((ServerWorld) world).getServer(), ((RiftTileEntity) tile).getWorld());
                entity.changeDimension(target, (RiftTileEntity) tile);
            }
            entity.setPortalCooldown();
        }
    }

}
