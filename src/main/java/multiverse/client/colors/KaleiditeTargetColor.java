package multiverse.client.colors;

import multiverse.common.world.items.MultiversalToolHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class KaleiditeTargetColor implements ItemColor {

    public static final KaleiditeTargetColor INSTANCE = new KaleiditeTargetColor();

    protected KaleiditeTargetColor() {
    }

    @Override
    public int getColor(@Nonnull ItemStack stack, int layer) {
        if (layer == 0) {
            ClientLevel world = Minecraft.getInstance().level;
            if (world != null) {
                return MultiverseColorHelper.getColors(world, MultiversalToolHelper.getTarget(stack), 1)[0];
            }
        }
        return 0xFFFFFFFF;
    }
}
