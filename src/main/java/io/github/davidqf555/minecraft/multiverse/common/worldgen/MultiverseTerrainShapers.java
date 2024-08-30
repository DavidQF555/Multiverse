package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import net.minecraft.data.worldgen.TerrainProvider;
import net.minecraft.util.CubicSpline;
import net.minecraft.util.ToFloatFunction;
import net.minecraft.world.level.biome.TerrainShaper;

public final class MultiverseTerrainShapers {

    private static final ToFloatFunction<Float> NO_TRANSFORM = val -> val;

    private MultiverseTerrainShapers() {
    }

    public static TerrainShaper mountains() {
        ToFloatFunction<Float> $$1 = val -> val <= 0 ? val : val * 5;
        ToFloatFunction<Float> $$2 = val -> 3 - 6.25f / (val + 5);
        ToFloatFunction<Float> $$3 = val -> val * 2;
        CubicSpline<TerrainShaper.Point> $$4 = TerrainShaper.buildErosionOffsetSpline(-0.15f, 0, 0, 0.1f, 0, -0.03f, false, false, $$1);
        CubicSpline<TerrainShaper.Point> $$5 = TerrainShaper.buildErosionOffsetSpline(-0.1f, 0.03f, 0.1f, 0.1f, 0.01f, -0.03f, false, false, $$1);
        CubicSpline<TerrainShaper.Point> $$6 = TerrainShaper.buildErosionOffsetSpline(-0.1f, 0.03f, 0.1f, 0.7f, 0.01f, -0.03f, true, true, $$1);
        CubicSpline<TerrainShaper.Point> $$7 = TerrainShaper.buildErosionOffsetSpline(-0.05f, 0.03f, 0.1f, 1, 0.01f, 0.01f, true, true, $$1);
        CubicSpline<TerrainShaper.Point> $$12 = CubicSpline.builder(TerrainShaper.Coordinate.CONTINENTS, $$1)
                .addPoint(-1.1f, 0.044f, 0)
                .addPoint(-1.02f, -0.2222f, 0)
                .addPoint(-0.75f, -0.2222f, 0)
                .addPoint(-0.44f, 0, 0)
                .addPoint(-0.3f, 0.1f, 0)
                .addPoint(-0.16f, $$4, 0)
                .addPoint(-0.15f, $$4, 0)
                .addPoint(-0.05f, $$5, 0)
                .addPoint(0.35f, $$6, 0)
                .addPoint(1, $$7, 0)
                .build();
        CubicSpline<TerrainShaper.Point> $$13 = CubicSpline.builder(TerrainShaper.Coordinate.CONTINENTS, NO_TRANSFORM)
                .addPoint(-0.19f, 3.95f, 0)
                .addPoint(-0.15f, TerrainShaper.getErosionFactor(6.25f, true, NO_TRANSFORM), 0)
                .addPoint(-0.1f, TerrainShaper.getErosionFactor(5.47f, true, $$2), 0)
                .addPoint(0.03f, TerrainShaper.getErosionFactor(5.08f, true, $$2), 0)
                .addPoint(0.06f, TerrainShaper.getErosionFactor(4.69f, false, $$2), 0)
                .build();
        CubicSpline<TerrainShaper.Point> $$15 = CubicSpline.builder(TerrainShaper.Coordinate.CONTINENTS, $$3)
                .addPoint(-0.11f, 0, 0)
                .addPoint(0.03f, TerrainShaper.buildErosionJaggednessSpline(1, 0.5f, 0, 0, $$3), 0)
                .addPoint(0.65f, TerrainShaper.buildErosionJaggednessSpline(1, 1, 1, 0, $$3), 0)
                .build();
        return new TerrainShaper($$12, $$13, $$15);
    }

}