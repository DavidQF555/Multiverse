package io.github.davidqf555.minecraft.multiverse.common.world.items;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.world.RiftHelper;
import io.github.davidqf555.minecraft.multiverse.common.world.RiftPlacementHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RiftSwordItem extends SwordItem {

    private static final Component HOLD = Component.literal(" ").append(Component.translatable(Util.makeDescriptionId("item", new ResourceLocation(Multiverse.MOD_ID, "prismatic_sword.use"))).withStyle(ChatFormatting.AQUA));

    public RiftSwordItem(Tier tier, int damage, float speed, Properties properties) {
        super(tier, damage, speed, properties);
    }

    public static void slash(ServerLevel level, Vec3 start, Vec3 look, double dist, double width, double height, float angle, ResourceKey<Level> target) {
        look = look.normalize();
        Vec3 center = start.add(look.scale(dist));
        RiftHelper.placeRandomRift(level, target, ServerConfigs.INSTANCE.riftSwordTemporary.get(), width, height, center, look, angle, RiftPlacementHelper.ReplacementType.DESTROY);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> text, TooltipFlag flag) {
        super.appendHoverText(stack, world, text, flag);
        text.add(Component.empty());
        text.add(MultiversalToolHelper.getHoldRightHeader());
        text.add(HOLD);
        text.add(MultiversalToolHelper.getShiftRightHeader());
        text.add(MultiversalToolHelper.SELECT_CURRENT);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity entity, int remaining) {
        if (world instanceof ServerLevel) {
            int duration = getUseDuration(stack) - remaining - ServerConfigs.INSTANCE.swordMinCharge.get();
            if (duration >= 0) {
                double width = Math.min(ServerConfigs.INSTANCE.swordMinWidth.get() + ServerConfigs.INSTANCE.swordWidthRate.get() * duration, ServerConfigs.INSTANCE.swordMaxWidth.get());
                double height = Math.min(ServerConfigs.INSTANCE.swordMinHeight.get() + ServerConfigs.INSTANCE.swordHeightRate.get() * duration, ServerConfigs.INSTANCE.swordMaxHeight.get());
                HumanoidArm used = entity.getMainArm();
                if (entity.getUsedItemHand() == InteractionHand.OFF_HAND) {
                    used = used.getOpposite();
                }
                float angle = used == HumanoidArm.RIGHT ? 45 : -45;
                Vec3 look = entity.getLookAngle();
                Vec3 start = entity.getEyePosition();
                slash((ServerLevel) world, start, look, ServerConfigs.INSTANCE.swordSpawnDistance.get(), width, height, angle, MultiversalToolHelper.getTarget(stack));
                if (entity instanceof Player && !((Player) entity).isCreative()) {
                    ((Player) entity).getCooldowns().addCooldown(this, ServerConfigs.INSTANCE.swordCooldown.get());
                }
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
        if (player.isShiftKeyDown()) {
            if (!world.isClientSide()) {
                MultiversalToolHelper.setRandomTarget(world, stack);
            }
        } else if (MultiversalToolHelper.getTarget(stack).equals(world.dimension())) {
            return InteractionResultHolder.pass(stack);
        } else {
            player.startUsingItem(hand);
        }
        return InteractionResultHolder.consume(stack);
    }

}
