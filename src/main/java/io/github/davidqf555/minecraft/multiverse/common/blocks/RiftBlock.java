package io.github.davidqf555.minecraft.multiverse.common.blocks;

import io.github.davidqf555.minecraft.multiverse.common.world.DimensionHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@ParametersAreNonnullByDefault
public class RiftBlock extends ContainerBlock {

    public static final BooleanProperty TEMPORARY = BooleanProperty.create("temporary");

    public RiftBlock() {
        super(Properties.of(Material.PORTAL, MaterialColor.COLOR_BLACK)
                .noCollission()
                .strength(-1, 3600000)
                .noDrops()
                .randomTicks()
        );
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader reader) {
        return new RiftTileEntity();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
        if (state.getValue(TEMPORARY)) {
            world.destroyBlock(pos, true);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(TEMPORARY);
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
        if (world instanceof ServerWorld && entity.canChangeDimensions() && tile instanceof RiftTileEntity && !entity.isPassenger() && !entity.isVehicle()) {
            if (!entity.isOnPortalCooldown()) {
                ServerWorld target = DimensionHelper.getOrCreateWorld(((ServerWorld) world).getServer(), ((RiftTileEntity) tile).getTarget());
                entity.changeDimension(target, (RiftTileEntity) tile);
            }
            entity.setPortalCooldown();
        }
    }

}
