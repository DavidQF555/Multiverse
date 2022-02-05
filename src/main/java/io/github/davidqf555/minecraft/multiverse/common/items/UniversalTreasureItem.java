package io.github.davidqf555.minecraft.multiverse.common.items;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.RegistryHandler;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftBlock;
import io.github.davidqf555.minecraft.multiverse.common.blocks.RiftTileEntity;
import io.github.davidqf555.minecraft.multiverse.common.entities.CollectorEntity;
import io.github.davidqf555.minecraft.multiverse.common.world.rifts.RiftConfig;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.SimpleFoiledItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class UniversalTreasureItem extends SimpleFoiledItem {

    private static final ITextComponent LORE = new TranslationTextComponent("item." + Multiverse.MOD_ID + ".universal_treasure.lore").withStyle(TextFormatting.GOLD);

    public UniversalTreasureItem() {
        super(new Properties()
                .rarity(Rarity.RARE)
                .tab(ItemGroup.TAB_MISC)
                .stacksTo(1)
                .fireResistant()
        );
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> text, ITooltipFlag flag) {
        super.appendHoverText(stack, world, text, flag);
        text.add(LORE);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (entity.level instanceof ServerWorld && entity.tickCount >= 100 && random.nextDouble() < entity.tickCount / 1000.0) {
            CollectorEntity boss = RegistryHandler.COLLECTOR_ENTITY.get().create(entity.level);
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

    @Override
    public int getEntityLifespan(ItemStack itemStack, World world) {
        return 36000;
    }

}
