package io.github.davidqf555.minecraft.multiverse.common.items;

import io.github.davidqf555.minecraft.multiverse.common.entities.DoppelgangerEntity;
import io.github.davidqf555.minecraft.multiverse.registration.EntityRegistry;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class BeaconArmorItem extends ArmorItem {

    private static final int MIN_OFFSET = 1, MAX_OFFSET = 8, LIMIT = 8, PERIOD = 40, TIMEOUT = 600;

    public BeaconArmorItem(Holder<ArmorMaterial> material, ArmorItem.Type slot, Properties properties) {
        super(material, slot, properties);
    }


    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotIndex, boolean selectedIndex) {
        super.inventoryTick(stack, level, entity, slotIndex, selectedIndex);
        if (entity instanceof ServerPlayer && getEquipmentSlot().getIndex(Inventory.INVENTORY_SIZE) == slotIndex && ((Player) entity).getLastHurtByMob() != null && entity.tickCount - ((Player) entity).getLastHurtByMobTimestamp() < TIMEOUT && level.getGameTime() % PERIOD == 0) {
            int count = 0;
            UUID id = entity.getUUID();
            for (Entity test : ((ServerLevel) level).getAllEntities()) {
                if (test instanceof DoppelgangerEntity && id.equals(((DoppelgangerEntity) test).getOriginalId())) {
                    count++;
                }
            }
            if (count < LIMIT) {
                DoppelgangerEntity.spawnRandom(EntityRegistry.DOPPELGANGER.get(), (ServerPlayer) entity, entity.blockPosition(), MIN_OFFSET, MAX_OFFSET);
            }
        }
    }

}
