package multiverse.common.world;

import multiverse.common.packets.RiftEffectPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public final class WarpTeleporter {

    private static final TeleportTransition.PostTeleportTransition POST = RiftHelper.SLOW_FALLING.then(RiftHelper.CLEAR).then(TeleportTransition.PLAY_PORTAL_SOUND);

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
        if (world == null || !entity.canTeleport(entity.level(), world)) {
            return null;
        }
        Level from = entity.level();
        Vec3 pos = entity.getEyePosition();
        if (entity.canTeleport(from, world)) {
            Entity copy = entity.teleport(getPortalDestination(world, entity, entity.blockPosition()));
            if (copy != null) {
                PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) from, new ChunkPos(BlockPos.containing(pos)), new RiftEffectPacket(pos, copy.getSoundSource(), target));
                Vec3 changed = copy.getEyePosition();
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(copy, new RiftEffectPacket(changed, copy.getSoundSource(), current));
                return copy;
            }
        }
        return null;
    }

    public static TeleportTransition getPortalDestination(ServerLevel level, Entity entity, BlockPos pos) {
        DimensionType target = level.dimensionType();
        DimensionType from = entity.level().dimensionType();
        Vec3 scaled = RiftHelper.translate(Vec3.atCenterOf(pos), from, target, true);
        if (scaled.y() <= target.minY()) {
            scaled = new Vec3(scaled.x(), target.minY() + 1, scaled.z());
        }
        WorldBorder border = level.getWorldBorder();
        BlockPos clamped = border.clampToBounds(scaled.x(), scaled.y(), scaled.z());
        Vec3 to = Vec3.atBottomCenterOf(clamped);
        return new TeleportTransition(level, to, Vec3.ZERO, entity.getYRot(), entity.getXRot(), POST);
    }

}
