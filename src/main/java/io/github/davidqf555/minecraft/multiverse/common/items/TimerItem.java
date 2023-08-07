package io.github.davidqf555.minecraft.multiverse.common.items;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class TimerItem extends Item {

    private final int timer;

    public TimerItem(Properties properties, int timer) {
        super(properties);
        this.timer = timer;
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (entity.level() instanceof ServerLevel) {
            if (entity.tickCount >= timer) {
                doTimerEffect(stack, entity);
                entity.remove(Entity.RemovalReason.KILLED);
                return true;
            }
        } else {
            doTickEffect(stack, entity);
        }
        return false;
    }

    protected abstract void doTimerEffect(ItemStack stack, ItemEntity entity);

    protected abstract void doTickEffect(ItemStack stack, ItemEntity entity);

    @Override
    public int getEntityLifespan(ItemStack itemStack, Level world) {
        return 36000;
    }
}
