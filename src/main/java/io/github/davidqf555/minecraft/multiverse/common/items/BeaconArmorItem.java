package io.github.davidqf555.minecraft.multiverse.common.items;

import io.github.davidqf555.minecraft.multiverse.common.entities.DoppelgangerEntity;
import io.github.davidqf555.minecraft.multiverse.registration.EntityRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;

import java.util.UUID;

public class BeaconArmorItem extends ArmorItem implements IArmorHitEffect {

    private static final int MIN_OFFSET = 4, MAX_OFFSET = 8, LIMIT = 8;

    public BeaconArmorItem(ArmorMaterial material, ArmorItem.Type slot, Properties properties) {
        super(material, slot, properties);
    }

    @Override
    public boolean onHit(LivingEntity entity, DamageSource source, float damage) {
        Entity target = source.getEntity();
        if (entity instanceof ServerPlayer && target != null) {
            UUID id = entity.getUUID();
            int count = 0;
            for (Entity test : ((ServerPlayer) entity).serverLevel().getAllEntities()) {
                if (test instanceof DoppelgangerEntity && id.equals(((DoppelgangerEntity) test).getOriginalId())) {
                    count++;
                }
            }
            if (count < LIMIT) {
                DoppelgangerEntity.spawnRandom(EntityRegistry.DOPPELGANGER.get(), (ServerPlayer) entity, target.blockPosition(), MIN_OFFSET, MAX_OFFSET);
            }
        }
        return true;
    }

}
