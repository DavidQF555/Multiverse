package multiverse.client.colors;

import multiverse.common.world.items.MultiversalToolHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

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
                ResourceKey<Level> target = MultiversalToolHelper.getTarget(stack);
                return MultiverseColorHelper.getColors(target == null ? world.dimension() : target, 1)[0];
            }
        }
        return 0xFFFFFFFF;
    }
}
