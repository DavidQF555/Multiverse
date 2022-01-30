package io.github.davidqf555.minecraft.multiverse.common.items;

import io.github.davidqf555.minecraft.multiverse.common.RegistryHandler;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftBlock;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftTileEntity;
import io.github.davidqf555.minecraft.multiverse.common.entities.DimensionBossEntity;
import io.github.davidqf555.minecraft.multiverse.common.world.gen.RiftConfig;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.SimpleFoiledItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.Optional;

public class UniversalTreasureItem extends SimpleFoiledItem {

    public UniversalTreasureItem() {
        super(new Properties()
                .rarity(Rarity.RARE)
                .tab(ItemGroup.TAB_MISC)
                .stacksTo(1)
        );
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (entity.level instanceof ServerWorld && entity.tickCount >= 100 && random.nextDouble() < entity.tickCount / 1000.0) {
            DimensionBossEntity boss = RegistryHandler.DIMENSION_BOSS_ENTITY.get().create(entity.level);
            if (boss != null) {
                boss.setPos(entity.getX(), entity.getY(), entity.getZ());
                boss.setPortalCooldown();
                BlockPos center = entity.blockPosition();
                RegistryHandler.RIFT_FEATURE.get().place((ServerWorld) entity.level, ((ServerWorld) entity.level).getChunkSource().getGenerator(), random, center, RiftConfig.of(Optional.empty(), RegistryHandler.RIFT_BLOCK.get().defaultBlockState().setValue(RiftBlock.TEMPORARY, true), false));
                TileEntity tile = entity.level.getBlockEntity(center);
                if (tile instanceof RiftTileEntity) {
                    boss.setFrom(((RiftTileEntity) tile).getTarget());
                }
                entity.level.addFreshEntity(boss);
                entity.remove();
                return true;
            }
        }
        return false;
    }
}
