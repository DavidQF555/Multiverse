package io.github.davidqf555.minecraft.multiverse.common.world;

import io.github.davidqf555.minecraft.multiverse.common.packets.RiftParticlesPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class WarpTeleporter {

    private static final DimensionTransition.PostDimensionTransition POST = RiftHelper.SLOW_FALLING.then(RiftHelper.CLEAR).then(DimensionTransition.PLAY_PORTAL_SOUND);

    private WarpTeleporter() {
    }

    @Nullable
    public static Entity warp(Entity entity, ResourceKey<Level> target) {
        if (entity.level().isClientSide()) {
            return null;
        }
        ResourceKey<Level> current = entity.level().dimension();
        if (target.equals(current)) {
            return null;
        }
        ServerLevel world = entity.getServer().getLevel(target);
        if (world == null || !entity.canChangeDimensions(entity.level(), world)) {
            return null;
        }
        Level from = entity.level();
        Vec3 pos = entity.getEyePosition();
        if (entity.canChangeDimensions(from, world)) {
            Entity copy = entity.changeDimension(getPortalDestination(world, entity, entity.blockPosition()));
            if (copy != null) {
                PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) from, new ChunkPos(BlockPos.containing(pos)), new RiftParticlesPacket(Optional.of(target), pos));
                from.playSound(null, pos.x(), pos.y(), pos.z(), SoundEvents.ENDERMAN_TELEPORT, copy.getSoundSource(), 1, 1);
                Vec3 changed = copy.getEyePosition();
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(copy, new RiftParticlesPacket(Optional.of(current), changed));
                world.playSound(null, changed.x(), changed.y(), changed.z(), SoundEvents.ENDERMAN_TELEPORT, copy.getSoundSource(), 1, 1);
                return copy;
            }
        }
        return null;
    }

    public static DimensionTransition getPortalDestination(ServerLevel level, Entity entity, BlockPos pos) {
        DimensionType target = level.dimensionType();
        DimensionType from = entity.level().dimensionType();
        Vec3 scaled = DimensionHelper.translate(Vec3.atCenterOf(pos), from, target, true);
        if (scaled.y() <= target.minY()) {
            scaled = new Vec3(scaled.x(), target.minY() + 1, scaled.z());
        }
        WorldBorder border = level.getWorldBorder();
        BlockPos clamped = border.clampToBounds(scaled.x(), scaled.y(), scaled.z());
        Vec3 to = Vec3.atBottomCenterOf(clamped);
        return new DimensionTransition(level, to, Vec3.ZERO, entity.getYRot(), entity.getXRot(), POST);
    }

}
