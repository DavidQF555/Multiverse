package io.github.davidqf555.minecraft.multiverse.common.items;

import io.github.davidqf555.minecraft.multiverse.common.world.gen.RiftConfig;
import io.github.davidqf555.minecraft.multiverse.common.world.gen.RiftFeature;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Optional;

public class DimensionSlasherItem extends SwordItem {

    private static final int COOLDOWN = 10;

    public DimensionSlasherItem(int attack, float speed) {
        super(ItemTier.IRON, attack, speed, new Properties().rarity(Rarity.EPIC).tab(ItemGroup.TAB_COMBAT));
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (world instanceof ServerWorld) {
            CooldownTracker cooldowns = player.getCooldowns();
            if (cooldowns.isOnCooldown(this)) {
                return ActionResult.pass(stack);
            }
            Vector3d eye = player.getEyePosition(1);
            BlockPos center = world.clip(new RayTraceContext(eye, eye.add(player.getLookAngle().scale(4)), RayTraceContext.BlockMode.VISUAL, RayTraceContext.FluidMode.NONE, null)).getBlockPos();
            RiftFeature.INSTANCE.place((ServerWorld) world, ((ServerWorld) world).getChunkSource().getGenerator(), player.getRandom(), center, createRiftConfig(player));
            cooldowns.addCooldown(this, COOLDOWN);
        }
        return ActionResult.success(stack);
    }

    protected RiftConfig createRiftConfig(PlayerEntity player) {
        MinMaxBounds.IntBound width = MinMaxBounds.IntBound.exactly(6);
        MinMaxBounds.IntBound height = MinMaxBounds.IntBound.exactly(1);
        float yRot = player.getYHeadRot();
        MinMaxBounds.FloatBound yaw = new MinMaxBounds.FloatBound(yRot, yRot);
        MinMaxBounds.FloatBound pitch = new MinMaxBounds.FloatBound(player.xRot, player.xRot);
        MinMaxBounds.FloatBound roll = new MinMaxBounds.FloatBound(0f, 0f);
        return new RiftConfig(Optional.empty(), width, height, yaw, pitch, roll, true, false);
    }

}
