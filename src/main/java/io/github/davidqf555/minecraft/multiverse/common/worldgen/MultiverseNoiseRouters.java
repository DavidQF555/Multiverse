package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public final class MultiverseNoiseRouters {

    private MultiverseNoiseRouters() {
    }

    public static NoiseRouterWithOnlyNoises noodles(NoiseSettings noise, int minY, int height) {
        NoiseRouterWithOnlyNoises original = NoiseRouterData.overworldWithoutCaves(noise);
        DensityFunction ridgeA = DensityFunctions.noise(getNoise(Noises.NOODLE_RIDGE_A), 2, 2);
        DensityFunction ridgeB = DensityFunctions.noise(getNoise(Noises.NOODLE_RIDGE_B), 2, 2);
        DensityFunction noodle = DensityFunctions.add(ridgeA.square(), ridgeB.square());
        DensityFunction yFactor = DensityFunctions.yClampedGradient(minY, minY + height, -0.25, 0.25).square();
        DensityFunction finalDensity = DensityFunctions.rangeChoice(DensityFunctions.add(noodle, yFactor), 0, 0.02, DensityFunctions.constant(64), DensityFunctions.constant(-64));
        return new NoiseRouterWithOnlyNoises(original.barrierNoise(), original.fluidLevelFloodednessNoise(), original.fluidLevelSpreadNoise(), original.lavaNoise(), original.temperature(), original.vegetation(), original.continents(), original.erosion(), original.depth(), original.ridges(), original.initialDensityWithoutJaggedness(), finalDensity, original.veinToggle(), original.veinRidged(), original.veinGap());
    }

    public static NoiseRouterWithOnlyNoises blobs(NoiseSettings noise, int minY, int height) {
        NoiseRouterWithOnlyNoises original = NoiseRouterData.overworldWithoutCaves(noise);

        DensityFunction yFactor = DensityFunctions.add(DensityFunctions.mul(DensityFunctions.yClampedGradient(minY, minY + height, -1, 1).square(), DensityFunctions.constant(-1)), DensityFunctions.constant(1));
        DensityFunction blobs = DensityFunctions.noise(getNoise(Noises.NOODLE), 4, 4).square();

        DensityFunction finalDensity = DensityFunctions.rangeChoice(DensityFunctions.mul(blobs, yFactor), -1000000, 0.3, DensityFunctions.constant(-64), DensityFunctions.constant(64));
        return new NoiseRouterWithOnlyNoises(original.barrierNoise(), original.fluidLevelFloodednessNoise(), original.fluidLevelSpreadNoise(), original.lavaNoise(), original.temperature(), original.vegetation(), original.continents(), original.erosion(), original.depth(), original.ridges(), original.initialDensityWithoutJaggedness(), finalDensity, original.veinToggle(), original.veinRidged(), original.veinGap());
    }

    private static Holder<NormalNoise.NoiseParameters> getNoise(ResourceKey<NormalNoise.NoiseParameters> loc) {
        return BuiltinRegistries.NOISE.getHolderOrThrow(loc);
    }

}
