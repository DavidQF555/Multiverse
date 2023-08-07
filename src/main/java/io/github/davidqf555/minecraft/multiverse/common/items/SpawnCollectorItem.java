package io.github.davidqf555.minecraft.multiverse.common.items;

import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftBlock;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftTileEntity;
import io.github.davidqf555.minecraft.multiverse.common.entities.CollectorEntity;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.features.RiftConfig;
import io.github.davidqf555.minecraft.multiverse.registration.BlockRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.EntityRegistry;
import io.github.davidqf555.minecraft.multiverse.registration.worldgen.FeatureRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class SpawnCollectorItem extends TimerItem {

    private Component lore;

    public SpawnCollectorItem(Properties properties, int timer) {
        super(properties, timer);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> text, TooltipFlag flag) {
        super.appendHoverText(stack, world, text, flag);
        text.add(getLore());
    }

    protected Component getLore() {
        if (lore == null) {
            lore = Component.translatable(getDescriptionId() + ".lore").withStyle(ChatFormatting.GOLD);
        }
        return lore;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    protected void doTimerEffect(ItemStack stack, ItemEntity entity) {
        CollectorEntity boss = EntityRegistry.COLLECTOR.get().spawn((ServerLevel) entity.level(), entity.blockPosition(), MobSpawnType.MOB_SUMMONED);
        if (boss != null) {
            boss.setPortalCooldown();
            BlockPos center = entity.blockPosition();
            FeatureRegistry.RIFT.get().place(new FeaturePlaceContext<>(Optional.empty(), (ServerLevel) entity.level(), ((ServerLevel) entity.level()).getChunkSource().getGenerator(), entity.level().getRandom(), center, RiftConfig.of(Optional.empty(), BlockRegistry.RIFT.get().defaultBlockState().setValue(RiftBlock.TEMPORARY, true), false)));
            BlockEntity tile = entity.level().getBlockEntity(center);
            if (tile instanceof RiftTileEntity) {
                boss.setFrom(((RiftTileEntity) tile).getTarget());
            }
        }
    }

    @Override
    protected void doTickEffect(ItemStack stack, ItemEntity entity) {
        int period = 1 + 100 / (1 + entity.tickCount);
        if (entity.level().getGameTime() % period == 0) {
            RandomSource random = entity.level().getRandom();
            entity.level().addParticle(ParticleTypes.END_ROD, entity.getRandomX(1), entity.getRandomY(), entity.getRandomZ(1), random.nextGaussian() * 0.005, random.nextGaussian() * 0.005, random.nextGaussian() * 0.005);
        }
    }

}
