package io.github.davidqf555.minecraft.multiverse.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class WarpTeleporter implements ITeleporter {

    public static final WarpTeleporter INSTANCE = new WarpTeleporter();

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
                .filter(pos -> !destWorld.isOutsideBuildHeight(pos) && RiftPlacementHelper.canDestroy(destWorld, pos, destWorld.getBlockState(pos)))
                .forEach(pos -> destWorld.destroyBlock(pos, true));
        return new PortalInfo(to, Vec3.ZERO, entity.getYRot(), entity.getXRot());
    }

}
