package io.github.davidqf555.minecraft.multiverse.common.worldgen.features;

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
            Rotation.CODEC.fieldOf("rotation").forGetter(config -> config.rotation)
    ).apply(builder, RiftConfig::new));
    private final Optional<Integer> target;
    private final BlockState block;
    private final boolean natural;
    private final Size size;
    private final Rotation rotation;

    public RiftConfig(Optional<Integer> target, BlockState block, boolean natural, Size size, Rotation rotation) {
        this.target = target;
        this.block = block;
        this.natural = natural;
        this.size = size;
        this.rotation = rotation;
    }

    public static RiftConfig of(Optional<Integer> target, BlockState block, boolean natural) {
        return new RiftConfig(target, block, natural, new Size(ServerConfigs.INSTANCE.minRiftWidth.get(), ServerConfigs.INSTANCE.maxRiftWidth.get(), ServerConfigs.INSTANCE.minRiftHeight.get(), ServerConfigs.INSTANCE.maxRiftHeight.get()), new Rotation(0, 180, 0, 180, 0, 180));
    }

    public static RiftConfig fixed(Optional<Integer> target, BlockState block, boolean natural, int width, int height, float xRot, float yRot, float zRot) {
        return new RiftConfig(target, block, natural, new Size(width, width, height, height), new Rotation(xRot, xRot, yRot, yRot, zRot, zRot));
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

    public Rotation getRotation() {
        return rotation;
    }

    public static class Size {

        public static final Codec<Size> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                ExtraCodecs.POSITIVE_INT.fieldOf("minWidth").forGetter(size -> size.minWidth),
                ExtraCodecs.POSITIVE_INT.fieldOf("maxWidth").forGetter(size -> size.maxWidth),
                ExtraCodecs.POSITIVE_INT.fieldOf("minHeight").forGetter(size -> size.minHeight),
                ExtraCodecs.POSITIVE_INT.fieldOf("maxHeight").forGetter(size -> size.maxHeight)
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
                Codec.FLOAT.fieldOf("minX").forGetter(rot -> rot.minX),
                Codec.FLOAT.fieldOf("maxX").forGetter(rot -> rot.maxX),
                Codec.FLOAT.fieldOf("minY").forGetter(rot -> rot.minY),
                Codec.FLOAT.fieldOf("maxY").forGetter(rot -> rot.maxY),
                Codec.FLOAT.fieldOf("minZ").forGetter(rot -> rot.minZ),
                Codec.FLOAT.fieldOf("maxZ").forGetter(rot -> rot.maxZ)
        ).apply(builder, Rotation::new));

        private final float minX, maxX, minY, maxY, minZ, maxZ;

        public Rotation(float minX, float maxX, float minY, float maxY, float minZ, float maxZ) {
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
            this.minZ = minZ;
            this.maxZ = maxZ;
        }


        public float getRotX(Random random) {
            return minX + random.nextFloat() * (maxX - minX);
        }

        public float getRotY(Random random) {
            return minY + random.nextFloat() * (maxY - minY);
        }

        public float getRotZ(Random random) {
            return minZ + random.nextFloat() * (maxZ - minZ);
        }
    }

}
