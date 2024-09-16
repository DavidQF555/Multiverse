package io.github.davidqf555.minecraft.multiverse.common.items;

import io.github.davidqf555.minecraft.multiverse.client.ClientHelper;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftBlock;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftHelper;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftTileEntity;
import io.github.davidqf555.minecraft.multiverse.common.entities.CollectorEntity;
import io.github.davidqf555.minecraft.multiverse.registration.BlockRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.EntityRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;

@ParametersAreNonnullByDefault
public class SpawnCollectorItem extends TimerItem {

    private static final double PARTICLE_RANGE = 3;
    private Component lore;

    public SpawnCollectorItem(Properties properties, int timer) {
        super(properties.stacksTo(1), timer);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> text, TooltipFlag flag) {
        super.appendHoverText(stack, world, text, flag);
        text.add(getLore());
    }

    protected Component getLore() {
        if (lore == null) {
            lore = new TranslatableComponent(getDescriptionId() + ".lore").withStyle(ChatFormatting.GOLD);
        }
        return lore;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    protected void doTimerEffect(ItemStack stack, ItemEntity entity) {
        BlockPos center = entity.blockPosition();
        CollectorEntity boss = EntityRegistry.COLLECTOR.get().spawn((ServerLevel) entity.level, null, null, null, center, MobSpawnType.MOB_SUMMONED, false, false);
        if (boss != null) {
            boss.setPortalCooldown();
            RiftHelper.placeExplosion((ServerLevel) entity.level, entity.level.getRandom(), BlockRegistry.RIFT.get().defaultBlockState().setValue(RiftBlock.TEMPORARY, true), Optional.empty(), Optional.empty(), entity.position(), true);
            BlockEntity tile = entity.level.getBlockEntity(center);
            if (tile instanceof RiftTileEntity) {
                boss.setFrom(((RiftTileEntity) tile).getTarget());
            }
        }
    }

    @Override
    protected void doTickEffect(ItemStack stack, ItemEntity entity) {
        int period = 5 + 100 / (1 + entity.tickCount);
        if (entity.level.getGameTime() % period == 0) {
            Random random = entity.level.getRandom();
            Vec3 pos = entity.position().add(random.nextGaussian() * PARTICLE_RANGE, random.nextGaussian() * PARTICLE_RANGE, random.nextGaussian() * PARTICLE_RANGE);
            ClientHelper.addRiftParticles(OptionalInt.empty(), pos);
        }
    }

}
