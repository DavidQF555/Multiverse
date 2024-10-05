package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public final class MultiverseNoiseRouters {

    private MultiverseNoiseRouters() {
    }

    public static NoiseRouterWithOnlyNoises underwater(NoiseSettings noise) {
        NoiseRouterWithOnlyNoises original = NoiseRouterData.nether(noise);
        DensityFunction level = DensityFunctions.constant(1);
        return new NoiseRouterWithOnlyNoises(original.barrierNoise(), level, original.fluidLevelSpreadNoise(), original.lavaNoise(), original.temperature(), original.vegetation(), original.continents(), original.erosion(), original.depth(), original.ridges(), original.initialDensityWithoutJaggedness(), original.finalDensity(), original.veinToggle(), original.veinRidged(), original.veinGap());
    }

    // reference net.minecraft.world.level.levelgen.NoiseRouterData.noodle()
    public static NoiseRouterWithOnlyNoises noodles(NoiseSettings noise, double minThick, double maxThick) {
        NoiseRouterWithOnlyNoises original = NoiseRouterData.overworldWithoutCaves(noise);
        DensityFunction noodle = DensityFunctions.noise(getNoise(Noises.NOODLE), 1, 1);
        DensityFunction thickness = DensityFunctions.mappedNoise(getNoise(Noises.NOODLE_THICKNESS), 1, 1, minThick, maxThick);
        DensityFunction ridgeA = DensityFunctions.noise(getNoise(Noises.NOODLE_RIDGE_A), 2.6666666666666665, 2.6666666666666665);
        DensityFunction ridgeB = DensityFunctions.noise(getNoise(Noises.NOODLE_RIDGE_B), 2.6666666666666665, 2.6666666666666665);
        DensityFunction base = DensityFunctions.mul(DensityFunctions.constant(-1.5), DensityFunctions.max(ridgeA.abs(), ridgeB.abs()));

        DensityFunction finalDensity = DensityFunctions.rangeChoice(noodle, -1000000, 0, DensityFunctions.constant(-64), DensityFunctions.add(thickness, base));
        return new NoiseRouterWithOnlyNoises(original.barrierNoise(), original.fluidLevelFloodednessNoise(), original.fluidLevelSpreadNoise(), original.lavaNoise(), original.temperature(), original.vegetation(), original.continents(), original.erosion(), original.depth(), original.ridges(), original.initialDensityWithoutJaggedness(), finalDensity, original.veinToggle(), original.veinRidged(), original.veinGap());
    }

    private static Holder<NormalNoise.NoiseParameters> getNoise(ResourceKey<NormalNoise.NoiseParameters> loc) {
        return BuiltinRegistries.NOISE.getHolderOrThrow(loc);
    }

}
