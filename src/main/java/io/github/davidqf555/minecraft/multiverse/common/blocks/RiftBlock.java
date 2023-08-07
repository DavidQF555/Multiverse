package io.github.davidqf555.minecraft.multiverse.common.blocks;

import io.github.davidqf555.minecraft.multiverse.client.MultiverseColorHelper;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.DimensionHelper;
import io.github.davidqf555.minecraft.multiverse.registration.ParticleTypeRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FastColor;
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
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@ParametersAreNonnullByDefault
public class RiftBlock extends BaseEntityBlock {

    public static final BooleanProperty TEMPORARY = BooleanProperty.create("temporary");
    private static final double PARTICLE_DIST = 1.5;

    public RiftBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, Random rand) {
        if (rand.nextDouble() < 0.01) {
            world.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 0.5f, rand.nextFloat() * 0.4f + 0.8f, false);
        }
        Vec3 vec = Vec3.atCenterOf(pos).add(rand.nextGaussian() * PARTICLE_DIST, rand.nextGaussian() * PARTICLE_DIST, rand.nextGaussian() * PARTICLE_DIST);
        int color = MultiverseColorHelper.getColor(world, ((RiftTileEntity) world.getBlockEntity(pos)).getTarget());
        world.addParticle(ParticleTypeRegistry.RIFT.get(), vec.x(), vec.y(), vec.z(), FastColor.ARGB32.red(color) / 255.0, FastColor.ARGB32.green(color) / 255.0, FastColor.ARGB32.blue(color) / 255.0);
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
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerLevel world, BlockPos pos, Random rand) {
        if (state.getValue(TEMPORARY)) {
            world.destroyBlock(pos, true);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TEMPORARY);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canBeReplaced(BlockState state, Fluid fluid) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (world instanceof ServerLevel && entity.canChangeDimensions() && tile instanceof RiftTileEntity && !entity.isPassenger() && !entity.isVehicle() && !(entity instanceof ItemEntity)) {
            if (!entity.isOnPortalCooldown()) {
                MinecraftServer server = world.getServer();
                int target = ((RiftTileEntity) tile).getTarget();
                if (DimensionHelper.getWorld(server, target).isPresent() || entity.getType().is(TagRegistry.GENERATE_MULTIVERSE)) {
                    ServerLevel dim = DimensionHelper.getOrCreateWorld(server, target);
                    entity.changeDimension(dim, (RiftTileEntity) tile);
                }
            }
            entity.setPortalCooldown();
        }
    }

}
