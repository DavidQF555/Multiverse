package io.github.davidqf555.minecraft.multiverse.common.worldgen.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class RiftConfig implements FeatureConfiguration {

    public static final Codec<RiftConfig> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            BlockState.CODEC.fieldOf("block").forGetter(config -> config.block),
            Size.CODEC.fieldOf("size").forGetter(config -> config.size)
    ).apply(builder, RiftConfig::new));
    private final BlockState block;
    private final Size size;

    public RiftConfig(BlockState block, Size size) {
        this.block = block;
        this.size = size;
    }

    public static RiftConfig of(BlockState block) {
        return new RiftConfig(block, new Size(ServerConfigs.INSTANCE.minRiftWidth.get(), ServerConfigs.INSTANCE.maxRiftWidth.get(), ServerConfigs.INSTANCE.minRiftHeight.get(), ServerConfigs.INSTANCE.maxRiftHeight.get()));
    }

    public BlockState getBlockState() {
        return block;
    }

    public Size getSize() {
        return size;
    }

    public static class Size {

        public static final Codec<Size> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                Codec.doubleRange(0, Double.MAX_VALUE).fieldOf("minWidth").forGetter(size -> size.minWidth),
                Codec.doubleRange(0, Double.MAX_VALUE).fieldOf("maxWidth").forGetter(size -> size.maxWidth),
                Codec.doubleRange(0, Double.MAX_VALUE).fieldOf("minHeight").forGetter(size -> size.minHeight),
                Codec.doubleRange(0, Double.MAX_VALUE).fieldOf("maxHeight").forGetter(size -> size.maxHeight)
        ).apply(builder, Size::new));

        private final double minWidth, maxWidth, minHeight, maxHeight;

        public Size(double minWidth, double maxWidth, double minHeight, double maxHeight) {
            this.minWidth = minWidth;
            this.maxWidth = maxWidth;
            this.minHeight = minHeight;
            this.maxHeight = maxHeight;
        }

        public double getWidth(RandomSource random) {
            return random.nextDouble() * (maxWidth - minWidth) + minWidth;
        }

        public double getHeight(RandomSource random) {
            return random.nextDouble() * (maxHeight - minHeight) + minHeight;
        }

    }

}
