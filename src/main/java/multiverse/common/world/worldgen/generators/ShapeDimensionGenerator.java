package multiverse.common.world.worldgen.generators;

import multiverse.common.world.worldgen.ShapesReader;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.RandomSource;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;

import java.util.List;

public class ShapeDimensionGenerator {

    private final List<ShapesReader.Entry> entries;

    public ShapeDimensionGenerator(List<ShapesReader.Entry> entries) {
        this.entries = entries;
    }

    public LevelStem createDimension(RegistryAccess access, long seed, RandomSource random) {
        int total = entries.stream().mapToInt(ShapesReader.Entry::weight).sum();
        if (total <= 0) {
            throw new IllegalStateException("Total shape weights cannot be 0 when generating multiverse dimensions");
        }
        int rand = random.nextInt(total);
        for (ShapesReader.Entry entry : entries) {
            total -= entry.weight();
            if (total <= rand) {
                return entry.shape().value().getDimensionProvider().createDimension(access, seed, random);
            }
        }
        throw new RuntimeException("Should never get here");
    }

    public LevelStem createDimension(RegistryAccess access, long base, int index) {
        long seed = GeneratorHelper.getSeed(base, index);
        RandomSource random = new SingleThreadedRandomSource(seed);
        return createDimension(access, seed, random);
    }

}
