package io.github.davidqf555.minecraft.multiverse.common.items;

import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftBlock;
import io.github.davidqf555.minecraft.multiverse.common.registration.BlockRegistry;
import io.github.davidqf555.minecraft.multiverse.common.registration.FeatureRegistry;
import io.github.davidqf555.minecraft.multiverse.common.world.gen.features.RiftConfig;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BoundlessBladeItem extends SwordItem {

    public BoundlessBladeItem(Tier tier, int damage, float speed, Properties properties) {
        super(tier, damage, speed, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity user) {
        if (super.hurtEnemy(stack, target, user)) {
            for (int i = 0; i < 16; i++) {
                double x = target.getX() + (target.getRandom().nextDouble() - 0.5) * 16;
                double y = Mth.clamp(target.getY() + (double) (target.getRandom().nextInt(16) - 8), 0, target.level.getHeight() - 1);
                double z = target.getZ() + (target.getRandom().nextDouble() - 0.5) * 16;
                if (target.randomTeleport(x, y, z, true)) {
                    target.level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 1, 1);
                    target.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 1, 1);
                    break;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity entity, int remaining) {
        if (world instanceof ServerLevel) {
            int count = Math.min(600, getUseDuration(stack) - remaining);
            int width = 6 + count / 30;
            int height = 3 + count / 40;
            Vec3 look = entity.getLookAngle();
            BlockPos center = new BlockPos(entity.getEyePosition(1).add(look.scale(width + 1.5)));
            FeatureRegistry.RIFT.get().place(new FeaturePlaceContext<>(Optional.empty(), (ServerLevel) world, ((ServerLevel) world).getChunkSource().getGenerator(), entity.getRandom(), center, RiftConfig.fixed(Optional.empty(), BlockRegistry.RIFT.get().defaultBlockState().setValue(RiftBlock.TEMPORARY, true), false, width, height, 0, 90 - entity.getYHeadRot(), -entity.getViewXRot(1))));
            if (entity instanceof Player) {
                ((Player) entity).getCooldowns().addCooldown(this, ServerConfigs.INSTANCE.boundlessBladeCooldown.get());
            }
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.pass(stack);
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }
}
