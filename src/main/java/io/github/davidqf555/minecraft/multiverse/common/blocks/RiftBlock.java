package io.github.davidqf555.minecraft.multiverse.common.blocks;

import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.multiverse.common.registration.ItemRegistry;
import io.github.davidqf555.minecraft.multiverse.common.world.DimensionHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@ParametersAreNonnullByDefault
public class RiftBlock extends ContainerBlock {

    public static final BooleanProperty TEMPORARY = BooleanProperty.create("temporary");

    public RiftBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
        if (rand.nextDouble() < 0.01) {
            world.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5D, SoundEvents.PORTAL_AMBIENT, SoundCategory.BLOCKS, 0.5f, rand.nextFloat() * 0.4f + 0.8f, false);
        }
        double x = pos.getX() + rand.nextDouble();
        double y = pos.getY() + rand.nextDouble();
        double z = pos.getZ() + rand.nextDouble();
        Pair<Integer, Integer> colors = ((RiftTileEntity) world.getBlockEntity(pos)).getColors();
        int color = rand.nextBoolean() ? colors.getFirst() : colors.getSecond();
        world.addParticle(ParticleTypes.ENTITY_EFFECT, x, y, z, ColorHelper.PackedColor.red(color) / 255.0, ColorHelper.PackedColor.green(color) / 255.0, ColorHelper.PackedColor.blue(color) / 255.0);
    }

    @Nonnull
    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.INVISIBLE;
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
        if (world instanceof ServerWorld && entity.canChangeDimensions() && tile instanceof RiftTileEntity && !entity.isPassenger() && !entity.isVehicle() && (!(entity instanceof ItemEntity) || !((ItemEntity) entity).getItem().getItem().equals(ItemRegistry.FABRIC_OF_REALITY.get()))) {
            if (!entity.isOnPortalCooldown()) {
                ServerWorld target = DimensionHelper.getOrCreateWorld(((ServerWorld) world).getServer(), ((RiftTileEntity) tile).getTarget());
                entity.changeDimension(target, (RiftTileEntity) tile);
            }
            entity.setPortalCooldown();
        }
    }

}
