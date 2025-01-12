package io.github.davidqf555.minecraft.multiverse.common.items;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.packets.RiftParticlesPacket;
import io.github.davidqf555.minecraft.multiverse.common.util.DimensionHelper;
import io.github.davidqf555.minecraft.multiverse.common.util.WarpTeleporter;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

import java.util.Optional;

public class RiftDeathItem extends Item implements IDeathEffect {

    private final int amp;

    public RiftDeathItem(Properties properties, int amp) {
        super(properties);
        this.amp = amp;
    }

    @Override
    public boolean onDeath(LivingEntity entity, ItemStack stack) {
        if (!entity.level.isClientSide()) {
            entity.setHealth(2);
            ResourceKey<Level> current = entity.level.dimension();
            ResourceKey<Level> target = DimensionHelper.randomMultiverseDimension(entity.getRandom(), Optional.of(current));
            Multiverse.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new RiftParticlesPacket(Optional.of(target), entity.getEyePosition()));
            ServerLevel world = entity.getServer().getLevel(target);
            if (world != null) {
                Entity copy = entity.changeDimension(world, WarpTeleporter.INSTANCE);
                if (copy != null) {
                    Multiverse.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> copy), new RiftParticlesPacket(Optional.of(current), copy.position()));
                    if (copy instanceof LivingEntity) {
                        ((LivingEntity) copy).addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 400, amp - 1));
                        int duration = ServerConfigs.INSTANCE.slowFalling.get();
                        if (duration > 0) {
                            ((LivingEntity) copy).addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, duration, 1, false, true));
                        }
                    }
                }
            }
            if (entity instanceof ServerPlayer) {
                CriteriaTriggers.USED_TOTEM.trigger((ServerPlayer) entity, stack);
            }

        }
        return true;
    }

}
