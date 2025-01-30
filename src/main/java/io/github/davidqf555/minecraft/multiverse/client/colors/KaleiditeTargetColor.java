package io.github.davidqf555.minecraft.multiverse.client.colors;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.world.items.MultiversalToolHelper;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class KaleiditeTargetColor implements ItemTintSource {

    public static final ResourceLocation LOCATION = ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "kaleidite_target");
    public static final MapCodec<KaleiditeTargetColor> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ExtraCodecs.ARGB_COLOR_CODEC.fieldOf("default").forGetter(color -> color.defaultColor)
    ).apply(inst, KaleiditeTargetColor::new));
    private final int defaultColor;

    protected KaleiditeTargetColor(int defaultColor) {
        this.defaultColor = defaultColor;
    }

    @Override
    public int calculate(ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity) {
        if (world != null) {
            return MultiverseColorHelper.getColors(MultiversalToolHelper.getTarget(stack), 1)[0];
        }
        return defaultColor;
    }

    @Override
    public MapCodec<? extends ItemTintSource> type() {
        return CODEC;
    }

}
