package io.github.davidqf555.minecraft.multiverse.common.world.items;

import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.world.entities.DoppelgangerEntity;
import io.github.davidqf555.minecraft.multiverse.registration.EntityRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.UUID;

public class BeaconArmorItem extends ArmorItem {

    private final Component lore;

    public BeaconArmorItem(ArmorMaterial material, ArmorType slot, Properties properties) {
        super(material, slot, properties);
        lore = Component.translatable(getDescriptionId() + ".lore").withStyle(ChatFormatting.GOLD);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(lore);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotIndex, boolean selectedIndex) {
        super.inventoryTick(stack, level, entity, slotIndex, selectedIndex);
        if (entity instanceof ServerPlayer && ((ServerPlayer) entity).getEquipmentSlotForItem(stack).getIndex(Inventory.INVENTORY_SIZE) == slotIndex && ((Player) entity).getLastHurtByMob() != null && entity.tickCount - ((Player) entity).getLastHurtByMobTimestamp() < ServerConfigs.INSTANCE.doppelTimeout.get() && level.getGameTime() % ServerConfigs.INSTANCE.armorSpawnPeriod.get() == 0) {
            int count = 0;
            UUID id = entity.getUUID();
            for (Entity test : ((ServerLevel) level).getAllEntities()) {
                if (test instanceof DoppelgangerEntity && id.equals(((DoppelgangerEntity) test).getOriginalId())) {
                    count++;
                }
            }
            if (count < ServerConfigs.INSTANCE.armorMaxSpawn.get()) {
                DoppelgangerEntity.spawnRandom(EntityRegistry.DOPPELGANGER.get(), (ServerPlayer) entity, entity.blockPosition(), ServerConfigs.INSTANCE.travelerMinRange.get(), ServerConfigs.INSTANCE.travelerMaxRange.get());
            }
        }
    }

}
