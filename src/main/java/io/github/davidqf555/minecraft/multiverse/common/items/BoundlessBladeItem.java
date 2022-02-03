package io.github.davidqf555.minecraft.multiverse.common.items;

import io.github.davidqf555.minecraft.multiverse.common.RegistryHandler;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftBlock;
import io.github.davidqf555.minecraft.multiverse.common.world.gen.RiftConfig;
import io.github.davidqf555.minecraft.multiverse.common.world.gen.RiftFeature;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Optional;

public class BoundlessBladeItem extends SwordItem {

    public BoundlessBladeItem() {
        super(CrystalItemTier.INSTANCE, 2, -2.4f, new Properties().rarity(Rarity.EPIC).tab(ItemGroup.TAB_COMBAT));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity user) {
        if (super.hurtEnemy(stack, target, user)) {
            for (int i = 0; i < 16; i++) {
                double x = target.getX() + (target.getRandom().nextDouble() - 0.5) * 16;
                double y = MathHelper.clamp(target.getY() + (double) (target.getRandom().nextInt(16) - 8), 0, target.level.getHeight() - 1);
                double z = target.getZ() + (target.getRandom().nextDouble() - 0.5) * 16;
                if (target.randomTeleport(x, y, z, true)) {
                    target.level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 1, 1);
                    target.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 1, 1);
                    break;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void releaseUsing(ItemStack stack, World world, LivingEntity entity, int remaining) {
        if (world instanceof ServerWorld && (!(entity instanceof PlayerEntity) || !((PlayerEntity) entity).getCooldowns().isOnCooldown(this))) {
            int count = Math.min(600, getUseDuration(stack) - remaining);
            int width = 6 + count / 30;
            int height = 3 + count / 40;
            Vector3d look = entity.getLookAngle();
            BlockPos center = new BlockPos(entity.getEyePosition(1).add(look.scale(width + 1.5)));
            RiftFeature.INSTANCE.place((ServerWorld) world, ((ServerWorld) world).getChunkSource().getGenerator(), entity.getRandom(), center, RiftConfig.fixed(Optional.empty(), RegistryHandler.RIFT_BLOCK.get().defaultBlockState().setValue(RiftBlock.TEMPORARY, true), false, width, height, 0, 90 - entity.getYHeadRot(), -entity.getViewXRot(1)));
            if (entity instanceof PlayerEntity) {
                ((PlayerEntity) entity).getCooldowns().addCooldown(this, ServerConfigs.INSTANCE.dimensionSlasherCooldown.get());
            }
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        player.startUsingItem(hand);
        return ActionResult.consume(player.getItemInHand(hand));
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
