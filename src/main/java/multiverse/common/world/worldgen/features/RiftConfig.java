package multiverse.common.world.worldgen.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class RiftConfig implements FeatureConfiguration {

    public static final Codec<RiftConfig> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            BlockState.CODEC.fieldOf("block").forGetter(config -> config.block),
            Codec.doubleRange(0, Double.MAX_VALUE).fieldOf("min_width").forGetter(size -> size.minWidth),
            Codec.doubleRange(0, Double.MAX_VALUE).fieldOf("max_width").forGetter(size -> size.maxWidth),
            Codec.doubleRange(0, Double.MAX_VALUE).fieldOf("min_height").forGetter(size -> size.minHeight),
            Codec.doubleRange(0, Double.MAX_VALUE).fieldOf("max_height").forGetter(size -> size.maxHeight)
    ).apply(builder, RiftConfig::new));
    private final BlockState block;
    private final double minWidth, maxWidth, minHeight, maxHeight;

    public RiftConfig(BlockState block, double minWidth, double maxWidth, double minHeight, double maxHeight) {
        this.block = block;
        this.minWidth = minWidth;
        this.maxWidth = maxWidth;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
    }

    public BlockState getBlockState() {
        return block;
    }

    public double getWidth(RandomSource random) {
        return random.nextDouble() * (maxWidth - minWidth) + minWidth;
    }

    public double getHeight(RandomSource random) {
        return random.nextDouble() * (maxHeight - minHeight) + minHeight;
    }

}
