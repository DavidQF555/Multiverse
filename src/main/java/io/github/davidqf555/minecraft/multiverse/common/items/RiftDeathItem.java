package io.github.davidqf555.minecraft.multiverse.common.items;

import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftBlock;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.features.RiftConfig;
import io.github.davidqf555.minecraft.multiverse.registration.BlockRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.worldgen.FeatureRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import java.util.Optional;

public class RiftDeathItem extends Item implements IDeathEffect {

    private final MobEffectInstance effect;

    public RiftDeathItem(Properties properties, int amp) {
        super(properties);
        effect = new MobEffectInstance(MobEffects.ABSORPTION, 400, amp - 1);
    }

    @Override
    public boolean onDeath(LivingEntity entity, ItemStack stack) {
        entity.setHealth(2);
        entity.addEffect(effect);
        int min = entity.level().getMinBuildHeight();
        if (entity.getY() < min) {
            entity.teleportTo(entity.getX(), min + 2, entity.getZ());
        }
        Optional<Integer> target = entity.level().dimension().equals(Level.OVERWORLD) ? Optional.empty() : Optional.of(0);
        FeatureRegistry.RIFT.get().place(new FeaturePlaceContext<>(Optional.empty(), (ServerLevel) entity.level(), ((ServerLevel) entity.level()).getChunkSource().getGenerator(), entity.level().getRandom(), entity.blockPosition(), RiftConfig.of(target, BlockRegistry.RIFT.get().defaultBlockState().setValue(RiftBlock.TEMPORARY, true), false)));
        return true;
    }

}
