package io.github.davidqf555.minecraft.multiverse.common.world.blocks;

import com.mojang.serialization.MapCodec;
import io.github.davidqf555.minecraft.multiverse.client.ClientConfigs;
import io.github.davidqf555.minecraft.multiverse.common.MultiverseTags;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.world.DimensionHelper;
import io.github.davidqf555.minecraft.multiverse.common.world.RiftHelper;
import io.github.davidqf555.minecraft.multiverse.common.world.RiftPlacementHelper;
import io.github.davidqf555.minecraft.multiverse.common.world.entities.EntityHelper;
import io.github.davidqf555.minecraft.multiverse.common.world.entities.TravelerEntity;
import io.github.davidqf555.minecraft.multiverse.registration.EntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class RiftBlock extends BaseEntityBlock implements BucketPickup, LiquidBlockContainer, Portal {

    public static final BooleanProperty TEMPORARY = BooleanProperty.create("temporary");
    public static final EnumProperty<LoggedFluid> FLUID = EnumProperty.create("fluid", LoggedFluid.class, LoggedFluid.values());
    public static final MapCodec<RiftBlock> CODEC = simpleCodec(RiftBlock::new);
    private static final TeleportTransition.PostTeleportTransition POST = RiftHelper.SLOW_FALLING.then(RiftHelper.TRIGGER).then(TeleportTransition.PLAY_PORTAL_SOUND);

    public RiftBlock(Properties properties) {
        super(properties.noCollission().randomTicks());
        registerDefaultState(getStateDefinition().any().setValue(TEMPORARY, true).setValue(FLUID, LoggedFluid.AIR));
    }

    @Override
    protected MapCodec<? extends RiftBlock> codec() {
        return CODEC;
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
        if (rand.nextDouble() < ClientConfigs.INSTANCE.riftSoundFrequency.get()) {
            world.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 0.5f, rand.nextFloat() * 0.4f + 0.8f, false);
        }
    }

    @Override
    protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return Shapes.empty();
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
        } else if (rand.nextDouble() < ServerConfigs.INSTANCE.travelerSpawnChance.get()) {
            TravelerEntity entity = EntityHelper.randomSpawn(EntityRegistry.TRAVELER.get(), world, pos, 0, 8, EntitySpawnReason.NATURAL);
            if (entity != null) {
                entity.setPortalCooldown();
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TEMPORARY, FLUID);
    }

    @Override
    public boolean canBeReplaced(BlockState state, Fluid fluid) {
        return false;
    }

    @Override
    protected void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (world instanceof ServerLevel && tile instanceof RiftTileEntity && entity.canUsePortal(false) && !entity.getType().is(MultiverseTags.IGNORE_RIFT) && ((RiftTileEntity) tile).isColliding(entity.getBoundingBox())) {
            entity.setAsInsidePortal(this, pos);
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelAccessor world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        LoggedFluid fluid = LoggedFluid.AIR;
        Fluid type = world.getFluidState(pos).getType();
        if (type == Fluids.WATER) {
            fluid = LoggedFluid.WATER;
        } else if (type == Fluids.LAVA) {
            fluid = LoggedFluid.LAVA;
        }
        return defaultBlockState().setValue(FLUID, fluid);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader world, ScheduledTickAccess tick, BlockPos pos, Direction dir, BlockPos update, BlockState next, RandomSource random) {
        LoggedFluid fluid = state.getValue(FLUID);
        if (fluid != LoggedFluid.AIR) {
            tick.scheduleTick(pos, fluid.getFluid(), fluid.getFluid().getTickDelay(world));
        }
        return super.updateShape(state, world, tick, pos, dir, update, next, random);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(FLUID).getState();
    }

    @Override
    public ItemStack pickupBlock(@Nullable Player player, LevelAccessor world, BlockPos pos, BlockState state) {
        return switch (state.getValue(FLUID)) {
            case AIR -> ItemStack.EMPTY;
            case WATER -> {
                world.setBlock(pos, state.setValue(FLUID, LoggedFluid.AIR), 3);
                yield new ItemStack(Items.WATER_BUCKET);
            }
            case LAVA -> {
                world.setBlock(pos, state.setValue(FLUID, LoggedFluid.AIR), 3);
                yield new ItemStack(Items.LAVA_BUCKET);
            }
        };
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Fluids.WATER.getPickupSound();
    }

    @Override
    public boolean canPlaceLiquid(@Nullable Player player, BlockGetter getter, BlockPos pos, BlockState state, Fluid fluid) {
        return state.getValue(FLUID) == LoggedFluid.AIR && (fluid == Fluids.WATER || fluid == Fluids.LAVA);
    }

    @Override
    public boolean placeLiquid(LevelAccessor world, BlockPos pos, BlockState block, FluidState fluid) {
        if (block.getValue(FLUID) == LoggedFluid.AIR) {
            if (!world.isClientSide()) {
                if (fluid.getType() == Fluids.WATER) {
                    world.setBlock(pos, block.setValue(FLUID, LoggedFluid.WATER), 3);
                    world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
                } else if (fluid.getType() == Fluids.LAVA) {
                    world.setBlock(pos, block.setValue(FLUID, LoggedFluid.LAVA), 3);
                    world.scheduleTick(pos, Fluids.LAVA, Fluids.LAVA.getTickDelay(world));
                }
            }
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public TeleportTransition getPortalDestination(ServerLevel level, Entity entity, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof RiftTileEntity) {
            ServerLevel target = level.getServer().getLevel(((RiftTileEntity) be).getTarget());
            if (target != null) {
                Vec3 scaled = DimensionHelper.translate(Vec3.atCenterOf(pos), level.dimensionType(), target.dimensionType(), true);
                WorldBorder border = target.getWorldBorder();
                BlockPos clamped = border.clampToBounds(scaled.x(), scaled.y(), scaled.z());
                Vec3 loc = RiftHelper.getOrCreateRift(target, level.dimension(), Vec3.atCenterOf(clamped), level.getBlockState(pos).getValue(RiftBlock.TEMPORARY), ServerConfigs.INSTANCE.riftRange.get(), RiftPlacementHelper.ReplacementType.DESTROY);
                return new TeleportTransition(target, loc, entity.getDeltaMovement(), entity.getYRot(), entity.getXRot(), POST);
            }
        }
        return null;
    }

    public enum LoggedFluid implements StringRepresentable {

        AIR("air", Fluids.EMPTY.defaultFluidState()),
        WATER("water", Fluids.WATER.getSource(false)),
        LAVA("lava", Fluids.LAVA.getSource(false));

        private final String name;
        private final Fluid fluid;
        private final FluidState state;

        LoggedFluid(String name, FluidState state) {
            this.name = name;
            this.state = state;
            fluid = state.getType();
        }

        @Override
        public String getSerializedName() {
            return name;
        }

        public Fluid getFluid() {
            return fluid;
        }

        public FluidState getState() {
            return state;
        }

    }

}