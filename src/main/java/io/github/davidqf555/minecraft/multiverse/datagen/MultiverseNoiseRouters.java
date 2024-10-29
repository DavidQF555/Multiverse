package io.github.davidqf555.minecraft.multiverse.datagen;

import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public final class MultiverseNoiseRouters {

    private MultiverseNoiseRouters() {
    }

    public static NoiseRouter noodles(HolderGetter<DensityFunction> density, HolderGetter<NormalNoise.NoiseParameters> noise, int minY, int height) {
        NoiseRouter original = NoiseRouterData.floatingIslands(density, noise);
        DensityFunction ridgeA = DensityFunctions.noise(noise.getOrThrow(Noises.NOODLE_RIDGE_A), 2, 2);
        DensityFunction ridgeB = DensityFunctions.noise(noise.getOrThrow(Noises.NOODLE_RIDGE_B), 2, 2);
        DensityFunction noodle = DensityFunctions.add(ridgeA.square(), ridgeB.square());
        DensityFunction yFactor = DensityFunctions.yClampedGradient(minY, minY + height, -0.25, 0.25).square();
        DensityFunction finalDensity = DensityFunctions.rangeChoice(DensityFunctions.add(noodle, yFactor), 0, 0.02, DensityFunctions.constant(64), DensityFunctions.constant(-64));
        return new NoiseRouter(original.barrierNoise(), original.fluidLevelFloodednessNoise(), original.fluidLevelSpreadNoise(), original.lavaNoise(), original.temperature(), original.vegetation(), original.continents(), original.erosion(), original.depth(), original.ridges(), original.initialDensityWithoutJaggedness(), finalDensity, original.veinToggle(), original.veinRidged(), original.veinGap());
    }

    public static NoiseRouter blobs(HolderGetter<DensityFunction> density, HolderGetter<NormalNoise.NoiseParameters> noise, int minY, int height) {
        NoiseRouter original = NoiseRouterData.floatingIslands(density, noise);

        DensityFunction yFactor = DensityFunctions.add(DensityFunctions.mul(DensityFunctions.yClampedGradient(minY, minY + height, -1, 1).square(), DensityFunctions.constant(-1)), DensityFunctions.constant(1));
        DensityFunction blobs = DensityFunctions.noise(noise.getOrThrow(Noises.NOODLE), 4, 4).square();

        DensityFunction finalDensity = DensityFunctions.rangeChoice(DensityFunctions.mul(blobs, yFactor), -1000000, 0.3, DensityFunctions.constant(-64), DensityFunctions.constant(64));
        return new NoiseRouter(original.barrierNoise(), original.fluidLevelFloodednessNoise(), original.fluidLevelSpreadNoise(), original.lavaNoise(), original.temperature(), original.vegetation(), original.continents(), original.erosion(), original.depth(), original.ridges(), original.initialDensityWithoutJaggedness(), finalDensity, original.veinToggle(), original.veinRidged(), original.veinGap());
    }

}
