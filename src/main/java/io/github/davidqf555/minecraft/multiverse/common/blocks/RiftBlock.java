package io.github.davidqf555.minecraft.multiverse.common.blocks;

import io.github.davidqf555.minecraft.multiverse.common.MultiverseTags;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.advancements.EnterRiftTrigger;
import io.github.davidqf555.minecraft.multiverse.common.entities.TravelerEntity;
import io.github.davidqf555.minecraft.multiverse.common.util.EntityUtil;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.DimensionHelper;
import io.github.davidqf555.minecraft.multiverse.registration.EntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class RiftBlock extends BaseEntityBlock implements BucketPickup, LiquidBlockContainer {

    public static final BooleanProperty TEMPORARY = BooleanProperty.create("temporary");
    public static final EnumProperty<LoggedFluid> FLUID = EnumProperty.create("fluid", LoggedFluid.class, LoggedFluid.values());

    public RiftBlock(Properties properties) {
        super(properties.noCollission().randomTicks());
        registerDefaultState(getStateDefinition().any().setValue(TEMPORARY, true).setValue(FLUID, LoggedFluid.AIR));
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
        if (rand.nextDouble() < 0.01) {
            world.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 0.5f, rand.nextFloat() * 0.4f + 0.8f, false);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return Shapes.empty();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RiftTileEntity(pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
        if (state.getValue(TEMPORARY)) {
            world.destroyBlock(pos, true);
        } else if (rand.nextDouble() < ServerConfigs.INSTANCE.travelerSpawnChance.get()) {
            TravelerEntity entity = EntityUtil.randomSpawn(EntityRegistry.TRAVELER.get(), world, pos, 0, 8, MobSpawnType.NATURAL);
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
    @SuppressWarnings("deprecation")
    public boolean canBeReplaced(BlockState state, Fluid fluid) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (world instanceof ServerLevel && entity.canChangeDimensions() && tile instanceof RiftTileEntity && !entity.isPassenger() && !entity.isVehicle() && !(entity instanceof ItemEntity) && ((RiftTileEntity) tile).isColliding(entity.getBoundingBox())) {
            if (!entity.isOnPortalCooldown()) {
                MinecraftServer server = world.getServer();
                int target = ((RiftTileEntity) tile).getTarget();
                if (DimensionHelper.getWorld(server, target).isPresent() || entity.getType().is(MultiverseTags.GENERATE_MULTIVERSE)) {
                    ServerLevel dim = DimensionHelper.getOrCreateWorld(server, target);
                    Entity transported = entity.changeDimension(dim, (RiftTileEntity) tile);
                    if (transported instanceof LivingEntity) {
                        if (entity instanceof ServerPlayer) {
                            EnterRiftTrigger.INSTANCE.trigger((ServerPlayer) entity);
                        }
                        int duration = ServerConfigs.INSTANCE.slowFalling.get();
                        if (duration > 0) {
                            ((LivingEntity) transported).addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, duration, 1, false, true));
                        }
                    }
                }
            }
            entity.setPortalCooldown();
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
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction dir, BlockState next, LevelAccessor world, BlockPos pos, BlockPos update) {
        LoggedFluid fluid = state.getValue(FLUID);
        if (fluid != LoggedFluid.AIR) {
            world.scheduleTick(pos, fluid.getFluid(), fluid.getFluid().getTickDelay(world));
        }
        return super.updateShape(state, dir, next, world, pos, update);
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state) {
        return state.getValue(FLUID).getState();
    }

    @Override
    public ItemStack pickupBlock(LevelAccessor world, BlockPos pos, BlockState state) {
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
    public boolean canPlaceLiquid(BlockGetter getter, BlockPos pos, BlockState state, Fluid fluid) {
        return state.getValue(FLUID) == LoggedFluid.AIR && (fluid == Fluids.WATER || fluid == Fluids.LAVA);
    }

    @Override
    public boolean placeLiquid(LevelAccessor world, BlockPos pos, BlockState block, FluidState fluid) {
        if (canPlaceLiquid(world, pos, block, fluid.getType())) {
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