package io.github.davidqf555.minecraft.multiverse.common.worldgen.providers;

import io.github.davidqf555.minecraft.multiverse.common.worldgen.DimensionHelper;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.data.ShapesManager;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.RandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;

import java.util.List;

public class ShapeDimensionProvider {

    public static final ShapeDimensionProvider INSTANCE = new ShapeDimensionProvider();

    protected ShapeDimensionProvider() {
    }

    public LevelStem createDimension(RegistryAccess access, long seed, RandomSource random) {
        List<ShapesManager.Entry> all = ShapesManager.INSTANCE.getShapes();
        int total = all.stream().mapToInt(ShapesManager.Entry::weight).sum();
        int rand = random.nextInt(total);
        for (ShapesManager.Entry entry : all) {
            total -= entry.weight();
            if (total <= rand) {
                return entry.shape().value().getDimensionProvider().createDimension(access, seed, random);
            }
        }
        throw new RuntimeException("Should never get here");
    }

    public LevelStem createDimension(RegistryAccess access, long seed, int index) {
        WorldgenRandom random = new WorldgenRandom(new XoroshiroRandomSource(DimensionHelper.getSeed(seed, index, false)));
        return createDimension(access, seed, random);
    }

}
