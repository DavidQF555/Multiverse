package io.github.davidqf555.minecraft.multiverse.common.worldgen.features;

import com.mojang.math.Vector3f;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.Optional;
import java.util.Random;

public class RiftConfig implements FeatureConfiguration {

    public static final Codec<RiftConfig> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("target").forGetter(config -> config.target),
            BlockState.CODEC.fieldOf("block").forGetter(config -> config.block),
            Codec.BOOL.fieldOf("natural").forGetter(config -> config.natural),
            Size.CODEC.fieldOf("size").forGetter(config -> config.size),
            Rotation.CODEC.optionalFieldOf("rotation").forGetter(config -> config.rotation)
    ).apply(builder, RiftConfig::new));
    private final Optional<Integer> target;
    private final BlockState block;
    private final boolean natural;
    private final Size size;
    private final Optional<Rotation> rotation;

    public RiftConfig(Optional<Integer> target, BlockState block, boolean natural, Size size, Optional<Rotation> rotation) {
        this.target = target;
        this.block = block;
        this.natural = natural;
        this.size = size;
        this.rotation = rotation;
    }

    public static RiftConfig of(Optional<Integer> target, BlockState block, boolean natural) {
        return new RiftConfig(target, block, natural, new Size(ServerConfigs.INSTANCE.minRiftWidth.get(), ServerConfigs.INSTANCE.maxRiftWidth.get(), ServerConfigs.INSTANCE.minRiftHeight.get(), ServerConfigs.INSTANCE.maxRiftHeight.get()), Optional.empty());
    }

    public static RiftConfig fixed(Optional<Integer> target, BlockState block, boolean natural, int width, int height, Optional<Rotation> rotation) {
        return new RiftConfig(target, block, natural, new Size(width, width, height, height), rotation);
    }

    public Optional<Integer> getTarget() {
        return target;
    }

    public BlockState getBlockState() {
        return block;
    }

    public boolean isNatural() {
        return natural;
    }

    public Size getSize() {
        return size;
    }

    public Rotation getRotation(Random rand) {
        return rotation.orElseGet(() -> {
            Vector3f axis = new Vector3f(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
            if (!axis.normalize()) {
                axis = Vector3f.YP;
            }
            return new Rotation(axis, rand.nextFloat(180));
        });
    }

    public static class Size {

        public static final Codec<Size> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("minWidth").forGetter(size -> size.minWidth),
                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("maxWidth").forGetter(size -> size.maxWidth),
                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("minHeight").forGetter(size -> size.minHeight),
                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("maxHeight").forGetter(size -> size.maxHeight)
        ).apply(builder, Size::new));

        private final int minWidth, maxWidth, minHeight, maxHeight;

        public Size(int minWidth, int maxWidth, int minHeight, int maxHeight) {
            this.minWidth = minWidth;
            this.maxWidth = maxWidth;
            this.minHeight = minHeight;
            this.maxHeight = maxHeight;
        }

        public int getWidth(Random random) {
            return random.nextInt(maxWidth - minWidth + 1) + minWidth;
        }

        public int getHeight(Random random) {
            return random.nextInt(maxHeight - minHeight + 1) + minHeight;
        }

    }

    public static class Rotation {

        public static final Codec<Rotation> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                Vector3f.CODEC.fieldOf("axis").forGetter(Rotation::getAxis),
                Codec.FLOAT.fieldOf("angle").forGetter(Rotation::getAngle)
        ).apply(builder, Rotation::new));
        private final Vector3f axis;
        private final float angle;

        public Rotation(Vector3f axis, float angle) {
            this.axis = axis;
            this.axis.normalize();
            this.angle = angle;
        }

        public Vector3f getAxis() {
            return axis;
        }

        public float getAngle() {
            return angle;
        }

    }

}
