package io.github.davidqf555.minecraft.multiverse.common.world.items;

import io.github.davidqf555.minecraft.multiverse.common.world.entities.KaleiditeCoreEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

import java.util.List;

public class RiftCoreItem extends Item implements ProjectileItem {

    private Component lore;

    public RiftCoreItem(Properties properties) {
        super(properties);
        DispenserBlock.registerBehavior(this, new ProjectileDispenseBehavior(this));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);
        lines.add(getLore());
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_PEARL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!world.isClientSide()) {
            KaleiditeCoreEntity proj = new KaleiditeCoreEntity(player, world, stack);
            proj.setOwner(player);
            proj.setItem(stack);
            proj.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1.5f, 1);
            world.addFreshEntity(proj);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return world.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
    }

    protected Component getLore() {
        if (lore == null) {
            lore = Component.translatable(getDescriptionId() + ".lore").withStyle(ChatFormatting.LIGHT_PURPLE);
        }
        return lore;
    }

    @Override
    public Projectile asProjectile(Level world, Position pos, ItemStack stack, Direction dir) {
        KaleiditeCoreEntity core = new KaleiditeCoreEntity(pos.x(), pos.y(), pos.z(), world, stack);
        core.setItem(stack);
        return core;
    }

}
