package io.github.davidqf555.minecraft.multiverse.common.items;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.packets.RiftParticlesPacket;
import io.github.davidqf555.minecraft.multiverse.common.util.DimensionHelper;
import io.github.davidqf555.minecraft.multiverse.common.util.RiftHelper;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;
import java.util.function.Function;

public class RiftDeathItem extends Item implements IDeathEffect, ITeleporter {

    private final MobEffectInstance effect;

    public RiftDeathItem(Properties properties, int amp) {
        super(properties);
        effect = new MobEffectInstance(MobEffects.ABSORPTION, 400, amp - 1);
    }

    @Override
    public boolean onDeath(LivingEntity entity, ItemStack stack) {
        if (!entity.level.isClientSide()) {
            entity.setHealth(2);
            entity.addEffect(effect);
            int duration = ServerConfigs.INSTANCE.slowFalling.get();
            if (duration > 0) {
                entity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, duration, 1, false, true));
            }
            int current = DimensionHelper.getIndex(entity.level.dimension());
            int target = entity.getRandom().nextInt(ServerConfigs.INSTANCE.maxDimensions.get());
            if (target >= current) {
                target++;
            }
            Multiverse.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new RiftParticlesPacket(OptionalInt.of(target), entity.getEyePosition()));
            DimensionHelper.getWorld(entity.getServer(), target).ifPresent(world -> {
                Entity copy = entity.changeDimension(world, this);
                if (copy != null) {
                    Multiverse.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> copy), new RiftParticlesPacket(OptionalInt.of(current), copy.position()));
                }
            });
            if (entity instanceof ServerPlayer) {
                CriteriaTriggers.USED_TOTEM.trigger((ServerPlayer) entity, stack);
            }

        }
        return true;
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
                .filter(pos -> RiftHelper.canReplace(destWorld, pos))
                .forEach(pos -> destWorld.destroyBlock(pos, true));
        return new PortalInfo(to, Vec3.ZERO, entity.getYRot(), entity.getXRot());
    }

}
