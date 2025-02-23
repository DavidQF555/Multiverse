package multiverse.common.world.items;

import multiverse.common.world.entities.KaleiditeCoreEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class RiftCoreItem extends SimpleLoreItem implements ProjectileItem {

    public RiftCoreItem(ChatFormatting formatting, Properties properties) {
        super(false, formatting, properties);
        DispenserBlock.registerBehavior(this, new ProjectileDispenseBehavior(this));
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

    @Override
    public Projectile asProjectile(Level world, Position pos, ItemStack stack, Direction dir) {
        KaleiditeCoreEntity core = new KaleiditeCoreEntity(pos.x(), pos.y(), pos.z(), world, stack);
        core.setItem(stack);
        return core;
    }

}
