package multiverse.common.world.effects;

import multiverse.common.ServerConfigs;
import multiverse.common.world.RiftHelper;
import multiverse.common.world.entities.ConquerorEntity;
import multiverse.common.world.entities.EntityHelper;
import multiverse.registration.EntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

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
                        Vec3 pos = EntityHelper.getRandomSpawnAbove(world, pLivingEntity.getRandom(), Vec3.atBottomCenterOf(raid.getCenter()), ServerConfigs.INSTANCE.conquerorMaxSpawnHDist.get(), ServerConfigs.INSTANCE.conquerorMinSpawnDist.get(), ServerConfigs.INSTANCE.conquerorMaxSpawnDist.get(), Set.of(EntityRegistry.CONQUEROR.get()));
                        entity.setPortalCooldown();
                        RiftHelper.placeRandomRift(world, false, pos, true);
                        raid.joinRaid(raid.getGroupsSpawned(), entity, BlockPos.containing(pos), false);
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
