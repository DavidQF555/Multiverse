package io.github.davidqf555.minecraft.multiverse.common.items.tools;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.packets.RiftParticlesPacket;
import io.github.davidqf555.minecraft.multiverse.common.util.MultiversalToolHelper;
import io.github.davidqf555.minecraft.multiverse.common.util.WarpTeleporter;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public class WarpStickItem extends Item {

    public WarpStickItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> text, TooltipFlag flag) {
        super.appendHoverText(stack, world, text, flag);
        text.add(MultiversalToolHelper.INSTRUCTIONS);
        text.add(MultiversalToolHelper.CROUCH_INSTRUCTIONS);
    }

    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        if (!pTarget.level.isClientSide() && !pStack.isEmpty()) {
            ResourceKey<Level> current = pTarget.level.dimension();
            ResourceKey<Level> target = MultiversalToolHelper.getTarget(pStack);
            if (!target.equals(current)) {
                ServerLevel world = pAttacker.getServer().getLevel(target);
                if (world != null) {
                    Multiverse.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> pTarget), new RiftParticlesPacket(Optional.of(target), pTarget.getEyePosition()));
                    Entity copy = pTarget.changeDimension(world, WarpTeleporter.INSTANCE);
                    if (copy != null) {
                        Multiverse.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> copy), new RiftParticlesPacket(Optional.of(current), copy.position()));
                        if (copy instanceof LivingEntity) {
                            int duration = ServerConfigs.INSTANCE.slowFalling.get();
                            if (duration > 0) {
                                ((LivingEntity) copy).addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, duration, 1, false, true));
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.isShiftKeyDown()) {
            if (!MultiversalToolHelper.setCurrent(world, stack)) {
                return InteractionResultHolder.pass(stack);
            }
        } else if (world instanceof ServerLevel) {
            MultiversalToolHelper.setRandomTarget(world, stack);
        }
        return InteractionResultHolder.consume(stack);
    }

}
