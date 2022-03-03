package io.github.davidqf555.minecraft.multiverse.common.items;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.RegistryHandler;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftBlock;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftTileEntity;
import io.github.davidqf555.minecraft.multiverse.common.entities.CollectorEntity;
import io.github.davidqf555.minecraft.multiverse.common.world.gen.rifts.RiftConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@ParametersAreNonnullByDefault
public class UniversalTreasureItem extends SimpleFoiledItem {

    private static final Component LORE = new TranslatableComponent("item." + Multiverse.MOD_ID + ".universal_treasure.lore").withStyle(ChatFormatting.GOLD);

    public UniversalTreasureItem() {
        super(new Properties()
                .rarity(Rarity.RARE)
                .tab(CreativeModeTab.TAB_MISC)
                .stacksTo(1)
                .fireResistant()
        );
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> text, TooltipFlag flag) {
        super.appendHoverText(stack, world, text, flag);
        text.add(LORE);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        Random random = entity.getLevel().random;
        if (entity.level instanceof ServerLevel) {
            if (entity.tickCount >= 200 && random.nextDouble() < entity.tickCount / 2000.0) {
                CollectorEntity boss = RegistryHandler.COLLECTOR_ENTITY.get().create(entity.level);
                if (boss != null) {
                    boss.setPos(entity.getX(), entity.getY(), entity.getZ());
                    boss.setPortalCooldown();
                    BlockPos center = entity.blockPosition();
                    RegistryHandler.RIFT_FEATURE.get().place(new FeaturePlaceContext<>(Optional.empty(), (ServerLevel) entity.level, ((ServerLevel) entity.level).getChunkSource().getGenerator(), random, center, RiftConfig.of(Optional.empty(), RegistryHandler.RIFT_BLOCK.get().defaultBlockState().setValue(RiftBlock.TEMPORARY, true), false)));
                    BlockEntity tile = entity.level.getBlockEntity(center);
                    if (tile instanceof RiftTileEntity) {
                        boss.setFrom(((RiftTileEntity) tile).getTarget());
                    }
                    entity.level.addFreshEntity(boss);
                    entity.remove(Entity.RemovalReason.DISCARDED);
                    return true;
                }
            }
        } else {
            int period = 1 + 100 / (1 + entity.tickCount);
            if (entity.level.getGameTime() % period == 0) {
                entity.level.addParticle(ParticleTypes.END_ROD, entity.getRandomX(1), entity.getRandomY(), entity.getRandomZ(1), random.nextGaussian() * 0.005, random.nextGaussian() * 0.005, random.nextGaussian() * 0.005);
            }
        }
        return false;
    }

    @Override
    public int getEntityLifespan(ItemStack itemStack, Level world) {
        return 36000;
    }

}
