package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public final class MultiverseNoiseRouters {

    private MultiverseNoiseRouters() {
    }

    // reference net.minecraft.world.level.levelgen.NoiseRouterData.noodle()
    public static NoiseRouterWithOnlyNoises noodles(NoiseSettings noise, double minThick, double maxThick) {
        NoiseRouterWithOnlyNoises original = NoiseRouterData.overworldWithoutCaves(noise);
        DensityFunction noodle = DensityFunctions.noise(getNoise(Noises.NOODLE), 4, 4);
        DensityFunction base = DensityFunctions.mappedNoise(getNoise(Noises.NOODLE_THICKNESS), 1, 1, minThick, maxThick);
        DensityFunction ridgeA = DensityFunctions.noise(getNoise(Noises.NOODLE_RIDGE_A), 4, 4);
        DensityFunction ridgeB = DensityFunctions.noise(getNoise(Noises.NOODLE_RIDGE_B), 4, 4);
        DensityFunction ridge = DensityFunctions.mul(DensityFunctions.constant(-4), DensityFunctions.max(ridgeA.abs(), ridgeB.abs()));

        DensityFunction finalDensity = DensityFunctions.rangeChoice(noodle, -1000000, 0, DensityFunctions.add(base, ridge), DensityFunctions.constant(0));
        return new NoiseRouterWithOnlyNoises(original.barrierNoise(), original.fluidLevelFloodednessNoise(), original.fluidLevelSpreadNoise(), original.lavaNoise(), original.temperature(), original.vegetation(), original.continents(), original.erosion(), original.depth(), original.ridges(), original.initialDensityWithoutJaggedness(), finalDensity, original.veinToggle(), original.veinRidged(), original.veinGap());
    }

    public static NoiseRouterWithOnlyNoises blobs(NoiseSettings noise) {
        NoiseRouterWithOnlyNoises original = NoiseRouterData.overworldWithoutCaves(noise);
        DensityFunction blobs = DensityFunctions.noise(getNoise(Noises.NOODLE), 4, 4).square();

        DensityFunction finalDensity = DensityFunctions.rangeChoice(blobs, -1000000, 0.375, DensityFunctions.constant(-64), DensityFunctions.constant(64));
        return new NoiseRouterWithOnlyNoises(original.barrierNoise(), original.fluidLevelFloodednessNoise(), original.fluidLevelSpreadNoise(), original.lavaNoise(), original.temperature(), original.vegetation(), original.continents(), original.erosion(), original.depth(), original.ridges(), original.initialDensityWithoutJaggedness(), finalDensity, original.veinToggle(), original.veinRidged(), original.veinGap());
    }

    private static Holder<NormalNoise.NoiseParameters> getNoise(ResourceKey<NormalNoise.NoiseParameters> loc) {
        return BuiltinRegistries.NOISE.getHolderOrThrow(loc);
    }

}
