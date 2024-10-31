package io.github.davidqf555.minecraft.multiverse.client.colors;

import io.github.davidqf555.minecraft.multiverse.common.util.MultiversalToolHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.ItemStack;

public class KaleiditeTargetColor implements ItemColor {

    public static final KaleiditeTargetColor INSTANCE = new KaleiditeTargetColor();

    protected KaleiditeTargetColor() {
    }

    @Override
    public int getColor(ItemStack stack, int layer) {
        if (layer == 0) {
            ClientLevel world = Minecraft.getInstance().level;
            if (world != null) {
                return MultiverseColorHelper.getColor(world, MultiversalToolHelper.getTarget(stack));
            }
        }
        return 0xFFFFFF;
    }
}
