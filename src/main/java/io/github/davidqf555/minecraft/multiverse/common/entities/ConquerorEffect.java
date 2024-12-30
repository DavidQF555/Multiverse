package io.github.davidqf555.minecraft.multiverse.common.entities;

import io.github.davidqf555.minecraft.multiverse.common.util.EntityUtil;
import io.github.davidqf555.minecraft.multiverse.common.util.RiftCoordinationHelper;
import io.github.davidqf555.minecraft.multiverse.registration.EntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.phys.Vec3;

public class ConquerorEffect extends MobEffect {

    public ConquerorEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if (pLivingEntity instanceof ServerPlayer && !pLivingEntity.isSpectator()) {
            ServerLevel world = ((ServerPlayer) pLivingEntity).getLevel();
            if (world.getDifficulty() == Difficulty.PEACEFUL) {
                return;
            }
            if (world.isVillage(pLivingEntity.blockPosition())) {
                ConquerorEntity entity = EntityRegistry.CONQUEROR.get().create(world);
                if (entity != null) {
                    Raid raid = world.getRaids().createOrExtendRaid((ServerPlayer) pLivingEntity);
                    if (raid != null) {
                        Vec3 pos = EntityUtil.randomAroundAbove(pLivingEntity.getRandom(), Vec3.atBottomCenterOf(raid.getCenter()), 0, 10);
                        entity.setPortalCooldown();
                        RiftCoordinationHelper.placeRandomRift(world, false, pos);
                        raid.joinRaid(raid.getGroupsSpawned(), entity, new BlockPos(pos), false);
                        raid.setLeader(raid.getGroupsSpawned(), entity);
                        pLivingEntity.removeEffect(this);
                    }
                }
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }

}
