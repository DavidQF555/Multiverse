package io.github.davidqf555.minecraft.multiverse.common.util;

import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftBlock;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftTileEntity;
import io.github.davidqf555.minecraft.multiverse.registration.BlockRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.POIRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.*;

public final class RiftCoordinationHelper {

    private RiftCoordinationHelper() {
    }

    public static Optional<BlockPos> getClosestRift(ServerLevel world, ResourceKey<Level> target, BlockPos center, int distance) {
        PoiManager manager = world.getPoiManager();
        PoiType poi = POIRegistry.RIFT.get();
        manager.ensureLoadedAndValid(world, center, distance);
        return manager.getInSquare(poi::equals, center, distance, PoiManager.Occupancy.ANY)
                .map(PoiRecord::getPos)
                .filter(block -> {
                    BlockEntity tile = world.getBlockEntity(block);
                    return tile instanceof RiftTileEntity && ((RiftTileEntity) tile).getTarget().equals(target);
                })
                .min(Comparator.comparingDouble(center::distSqr));
    }

    public static Vec3 getOrCreateRift(ServerLevel world, ResourceKey<Level> target, Vec3 center, boolean temporary, int distance, RiftPlacementHelper.ReplacementType replacement) {
        return getClosestRift(world, target, new BlockPos(center), distance)
                .map(Vec3::atCenterOf)
                .orElseGet(() -> {
                    placeRandomRift(world, target, temporary, center, replacement);
                    return center;
                });
    }

    public static void placeRandomRift(ServerLevel world, ResourceKey<Level> target, boolean temporary, double width, double height, Vec3 center, Vec3 normal, float angle, RiftPlacementHelper.ReplacementType replacement) {
        doRiftSpawnEffect(world, new BlockPos(center));
        RiftPlacementHelper.place(world, world, BlockRegistry.RIFT.get().defaultBlockState().setValue(RiftBlock.TEMPORARY, temporary), target, center, normal, angle, width, height, replacement);
    }

    public static void placeRandomRift(ServerLevel world, ResourceKey<Level> target, boolean temporary, Vec3 center, Vec3 normal, float angle, RiftPlacementHelper.ReplacementType replacement) {
        Random rand = world.getRandom();
        double minWidth = ServerConfigs.INSTANCE.minRiftWidth.get();
        double maxWidth = ServerConfigs.INSTANCE.maxRiftWidth.get();
        double minHeight = ServerConfigs.INSTANCE.minRiftHeight.get();
        double maxHeight = ServerConfigs.INSTANCE.maxRiftHeight.get();
        double width = minWidth + rand.nextDouble(maxWidth - minWidth);
        double height = minHeight + rand.nextDouble(maxHeight - minHeight);
        placeRandomRift(world, target, temporary, width, height, center, normal, angle, replacement);
    }

    public static void placeRandomRift(ServerLevel world, ResourceKey<Level> target, boolean temporary, Vec3 center, RiftPlacementHelper.ReplacementType replacement) {
        Random rand = world.getRandom();
        Vec3 normal = new Vec3(rand.nextDouble(), rand.nextDouble(), rand.nextDouble());
        float angle = rand.nextFloat(180);
        placeRandomRift(world, target, temporary, center, normal, angle, replacement);
    }

    public static void placeRandomRift(ServerLevel world, boolean temporary, Vec3 center, boolean mob) {
        RiftPlacementHelper.ReplacementType replacement = !mob || world.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) ? RiftPlacementHelper.ReplacementType.DESTROY : RiftPlacementHelper.ReplacementType.NONE;
        placeRandomRift(world, DimensionHelper.randomMultiverseDimension(world.getRandom(), Optional.of(world.dimension())), temporary, center, replacement);
    }

    public static void doRiftSpawnEffect(Level world, BlockPos pos) {
        world.levelEvent(LevelEvent.ANIMATION_END_GATEWAY_SPAWN, pos, 0);
    }

    public static void destroyRift(ServerLevel world, BlockPos pos, @Nullable Entity entity) {
        doRiftSpawnEffect(world, pos);
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof RiftTileEntity) {
            ResourceKey<Level> target = ((RiftTileEntity) be).getTarget();
            ServerLevel w = world.getServer().getLevel(target);
            if (w != null) {
                destroyConnectedRifts(world, w, pos, entity);
            }
        }
        destroyConnectedBlocks(world, pos, ServerConfigs.INSTANCE.coreRange.get(), entity);
    }

    private static void destroyConnectedBlocks(Level world, BlockPos start, double distance, @Nullable Entity entity) {
        int index = 0;
        List<BlockPos> list = new LinkedList<>();
        list.add(start);
        while (index < list.size()) {
            BlockPos pos = list.get(index++);
            if (pos.distSqr(start) <= distance * distance && world.getBlockState(pos).getBlock() instanceof RiftBlock) {
                BlockPos.betweenClosedStream(pos.relative(Direction.DOWN).relative(Direction.WEST).relative(Direction.SOUTH), pos.relative(Direction.UP).relative(Direction.EAST).relative(Direction.NORTH))
                        .filter(p -> !list.contains(p))
                        .map(BlockPos::immutable)
                        .forEach(list::add);
                world.destroyBlock(pos, true, entity);
            }
        }
    }

    private static void destroyConnectedRifts(Level from, ServerLevel target, BlockPos start, @Nullable Entity entity) {
        Vec3 pos = DimensionHelper.translate(Vec3.atCenterOf(start), from.dimensionType(), target.dimensionType(), true);
        RiftCoordinationHelper.getClosestRift(target, from.dimension(), new BlockPos(pos), ServerConfigs.INSTANCE.riftRange.get()).ifPresent(b -> {
            target.levelEvent(LevelEvent.ANIMATION_END_GATEWAY_SPAWN, b, 0);
            destroyConnectedBlocks(target, b, ServerConfigs.INSTANCE.coreRange.get(), entity);
        });
    }

}
