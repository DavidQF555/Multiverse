package io.github.davidqf555.minecraft.multiverse.common.items;

import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftBlock;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.features.RiftConfig;
import io.github.davidqf555.minecraft.multiverse.registration.BlockRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.worldgen.FeatureRegistry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;
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
public class RiftSwordItem extends SwordItem {

    public RiftSwordItem(Tier tier, int damage, float speed, Properties properties) {
        super(tier, damage, speed, properties);
    }

    public static boolean slash(ServerLevel level, Vec3 start, Vec3 look, double dist, double width, double height, float angle) {
        look = look.normalize();
        BlockPos center = BlockPos.containing(start.add(look.scale(dist)));
        RiftConfig.Rotation rotation = new RiftConfig.Rotation(look, angle);
        return FeatureRegistry.RIFT.get().place(new FeaturePlaceContext<>(Optional.empty(), level, level.getChunkSource().getGenerator(), level.getRandom(), center, RiftConfig.fixed(Optional.empty(), BlockRegistry.RIFT.get().defaultBlockState().setValue(RiftBlock.TEMPORARY, true), false, width, height, Optional.of(rotation))));
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity entity, int remaining) {
        if (world instanceof ServerLevel) {
            int count = Math.min(600, getUseDuration(stack) - remaining);
            int width = 1 + count / 200;
            int height = 5 + count / 20;
            HumanoidArm used = entity.getMainArm();
            if (entity.getUsedItemHand() == InteractionHand.OFF_HAND) {
                used = used.getOpposite();
            }
            float angle = used == HumanoidArm.RIGHT ? 45 : -45;
            Vec3 look = entity.getLookAngle();
            Vec3 start = entity.getEyePosition();
            if (slash((ServerLevel) world, start, look, 4, width, height, angle) && entity instanceof Player) {
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
