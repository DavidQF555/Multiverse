package multiverse.common.world.items;

import multiverse.client.render.WarpShieldRenderer;
import multiverse.common.ServerConfigs;
import multiverse.common.world.RiftHelper;
import multiverse.common.world.WarpTeleporter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

import java.util.function.Consumer;

public class WarpShieldItem extends SimpleLoreItem {

    private final TagKey<Item> repair;

    public WarpShieldItem(TagKey<Item> repair, ChatFormatting color, Properties pProperties) {
        super(false, color, pProperties);
        this.repair = repair;
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 72000;
    }

    @Override
    public void onUseTick(Level world, LivingEntity player, ItemStack stack, int count) {
        if (!world.isClientSide()) {
            double range = ServerConfigs.INSTANCE.shieldRange.get();
            AABB bounds = AABB.ofSize(player.getEyePosition(), range * 2, range * 2, range * 2);
            for (Projectile proj : world.getEntitiesOfClass(Projectile.class, bounds)) {
                RiftHelper.randomTargetDimension(player.getRandom(), player.level.dimension()).ifPresent(target -> WarpTeleporter.warp(proj, target));
            }
        }
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return WarpShieldRenderer.INSTANCE;
            }
        });
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        pPlayer.startUsingItem(pHand);
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public boolean isValidRepairItem(ItemStack pToRepair, ItemStack pRepair) {
        return pRepair.is(repair) || super.isValidRepairItem(pToRepair, pRepair);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return ToolActions.DEFAULT_SHIELD_ACTIONS.contains(toolAction);
    }

}
