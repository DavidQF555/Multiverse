package io.github.davidqf555.minecraft.multiverse.common.world.items;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.world.entities.DoppelgangerEntity;
import io.github.davidqf555.minecraft.multiverse.registration.EntityRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class BeaconArmorItem extends ArmorItem {

    private static final Component LORE = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation(Multiverse.MOD_ID, "beacon_chestplate.lore"))).withStyle(ChatFormatting.GOLD);

    public BeaconArmorItem(ArmorMaterial material, ArmorItem.Type slot, Properties properties) {
        super(material, slot, properties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(LORE);
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        super.onArmorTick(stack, level, player);
        if (level instanceof ServerLevel && player.getLastHurtByMob() != null && player.tickCount - player.getLastHurtByMobTimestamp() < ServerConfigs.INSTANCE.doppelTimeout.get() && level.getGameTime() % ServerConfigs.INSTANCE.armorSpawnPeriod.get() == 0) {
            int count = 0;
            UUID id = player.getUUID();
            for (Entity test : ((ServerLevel) level).getAllEntities()) {
                if (test instanceof DoppelgangerEntity && id.equals(((DoppelgangerEntity) test).getOriginalId())) {
                    count++;
                }
            }
            if (count < ServerConfigs.INSTANCE.armorMaxSpawn.get()) {
                DoppelgangerEntity.spawnRandom(EntityRegistry.DOPPELGANGER.get(), (ServerPlayer) player, player.blockPosition(), ServerConfigs.INSTANCE.armorMinOffset.get(), ServerConfigs.INSTANCE.armorMaxOffset.get());
            }
        }
    }

}
