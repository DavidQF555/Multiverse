package io.github.davidqf555.minecraft.multiverse.common.worldgen.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

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
        return new RiftConfig(target, block, natural, new Size(1, 3, 6, 10), Optional.empty());
    }

    public static RiftConfig fixed(Optional<Integer> target, BlockState block, boolean natural, double width, double height, Optional<Rotation> rotation) {
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

    public Rotation getRotation(RandomSource rand) {
        return rotation.orElseGet(() -> {
            Vec3 axis = new Vec3(rand.nextDouble(), rand.nextDouble(), rand.nextDouble()).normalize();
            if (axis.lengthSqr() == 0) {
                axis = new Vec3(0, 1, 0);
            }
            return new Rotation(axis, rand.nextFloat() * 180);
        });
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

    public static class Rotation {

        public static final Codec<Rotation> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                Vec3.CODEC.fieldOf("axis").forGetter(Rotation::getAxis),
                Codec.FLOAT.fieldOf("angle").forGetter(Rotation::getAngle)
        ).apply(builder, Rotation::new));
        private final Vec3 axis;
        private final float angle;

        public Rotation(Vec3 axis, float angle) {
            this.axis = axis.normalize();
            this.angle = angle;
        }

        public Vec3 getAxis() {
            return axis;
        }

        public float getAngle() {
            return angle;
        }

    }

}
