package io.github.davidqf555.minecraft.multiverse.common.blocks;

import com.mojang.serialization.MapCodec;
import io.github.davidqf555.minecraft.multiverse.common.MultiverseTags;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.DimensionHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.portal.DimensionTransition;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class RiftBlock extends BaseEntityBlock {

    public static final MapCodec<RiftBlock> CODEC = simpleCodec(RiftBlock::new);
    public static final BooleanProperty TEMPORARY = BooleanProperty.create("temporary");

    public RiftBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(TEMPORARY, false));
    }

    @Override
    protected MapCodec<? extends RiftBlock> codec() {
        return CODEC;
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
        if (rand.nextDouble() < 0.01) {
            world.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 0.5f, rand.nextFloat() * 0.4f + 0.8f, false);
        }
    }

    @Nonnull
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RiftTileEntity(pos, state);
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
        if (state.getValue(TEMPORARY)) {
            world.destroyBlock(pos, true);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TEMPORARY);
    }

    @Override
    public boolean canBeReplaced(BlockState state, Fluid fluid) {
        return false;
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (world instanceof ServerLevel && tile instanceof RiftTileEntity && !entity.isPassenger() && !entity.isVehicle() && !(entity instanceof ItemEntity)) {
            if (!entity.isOnPortalCooldown()) {
                MinecraftServer server = world.getServer();
                int target = ((RiftTileEntity) tile).getTarget();
                if (DimensionHelper.getWorld(server, target).isPresent() || entity.getType().is(MultiverseTags.GENERATE_MULTIVERSE)) {
                    ServerLevel dim = DimensionHelper.getOrCreateWorld(server, target);
                    if (entity.canChangeDimensions(world, dim)) {
                        DimensionTransition trans = ((RiftTileEntity) tile).getPortalDestination(dim, entity, entity.blockPosition());
                        if (trans != null) {
                            entity.changeDimension(trans);
                        }
                    }
                }
            }
            entity.setPortalCooldown();
        }
    }

}
