package io.github.davidqf555.minecraft.multiverse.common.items;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.packets.RiftParticlesPacket;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.Optional;

public class WarpToolItem extends Item {

    private static final int COOLDOWN = 100;

    public WarpToolItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity, int pTimeCharged) {
        if (!pLevel.isClientSide()) {
            int time = getUseDuration(pStack) - pTimeCharged;
            if (time < 0) {
                return;
            }
            Vec3 target = getTargetPosition(pLivingEntity, time);
            Multiverse.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> pLivingEntity), new RiftParticlesPacket(Optional.empty(), pLivingEntity.getEyePosition()));
            pLivingEntity.teleportTo(target.x(), target.y(), target.z());
            Multiverse.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> pLivingEntity), new RiftParticlesPacket(Optional.empty(), pLivingEntity.getEyePosition()));
            if (pLivingEntity instanceof Player) {
                if (!((Player) pLivingEntity).isCreative()) {
                    ((Player) pLivingEntity).getCooldowns().addCooldown(this, COOLDOWN);
                }
                ((Player) pLivingEntity).awardStat(Stats.ITEM_USED.get(this));
            }
        }
    }

    public Vec3 getTargetPosition(LivingEntity entity, int count) {
        Vec3 start = entity.getEyePosition();
        Vec3 to = start.add(entity.getLookAngle().normalize().scale(getDistance(count)));
        BlockHitResult hit = entity.level.clip(new ClipContext(start, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
        return hit.getLocation();
    }

    protected double getDistance(int count) {
        return Math.min(count / 2, 256);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BOW;
    }

}
