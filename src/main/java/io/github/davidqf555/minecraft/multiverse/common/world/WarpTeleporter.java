package io.github.davidqf555.minecraft.multiverse.common.world;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.packets.RiftParticlesPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public class WarpTeleporter implements ITeleporter {

    public static final WarpTeleporter INSTANCE = new WarpTeleporter();

    @Nullable
    public static Entity warp(Entity entity, ResourceKey<Level> target) {
        if (entity.level.isClientSide()) {
            return null;
        }
        ResourceKey<Level> current = entity.level.dimension();
        if (target.equals(current)) {
            return null;
        }
        ServerLevel world = entity.getServer().getLevel(target);
        if (world == null) {
            return null;
        }
        Multiverse.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new RiftParticlesPacket(Optional.of(target), entity.getEyePosition()));
        Entity copy = entity.changeDimension(world, INSTANCE);
        if (copy != null) {
            Multiverse.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> copy), new RiftParticlesPacket(Optional.of(current), copy.position()));
            if (copy instanceof LivingEntity) {
                int duration = ServerConfigs.INSTANCE.slowFalling.get();
                if (duration > 0) {
                    ((LivingEntity) copy).addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, duration, 1, false, true));
                }
            }
        }
        return copy;
    }

    protected WarpTeleporter() {
    }

    @Nullable
    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
        DimensionType target = destWorld.dimensionType();
        DimensionType from = entity.level.dimensionType();
        Vec3 scaled = DimensionHelper.translate(entity.position(), from, target, true);
        if (scaled.y() <= target.minY()) {
            scaled = new Vec3(scaled.x(), target.minY() + 1, scaled.z());
        }
        WorldBorder border = destWorld.getWorldBorder();
        BlockPos clamped = border.clampToBounds(scaled.x(), scaled.y(), scaled.z());
        Vec3 to = Vec3.atBottomCenterOf(clamped);
        AABB box = AABB.ofSize(to.add(0, entity.getBbHeight() / 2, 0), entity.getBbWidth(), entity.getBbHeight(), entity.getBbWidth());
        BlockPos.betweenClosedStream(box)
                .filter(pos -> !destWorld.isOutsideBuildHeight(pos) && RiftPlacementHelper.ReplacementType.DESTROY.canReplace(destWorld, pos, destWorld.getBlockState(pos)))
                .forEach(pos -> destWorld.destroyBlock(pos, true));
        return new PortalInfo(to, Vec3.ZERO, entity.getYRot(), entity.getXRot());
    }

}
