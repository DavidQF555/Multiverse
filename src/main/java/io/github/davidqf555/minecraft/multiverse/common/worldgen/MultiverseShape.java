package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.FlatSeaLevelSelector;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.IntRange;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.SeaLevelSelector;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.sea.aquifers.SerializableFluidPicker;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.*;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

public enum MultiverseShape {

    NORMAL("normal", false, true, -64, 384, 1, 2, registry -> NoiseRouterData.overworld(registry, false, false)),
    ISLANDS("islands", false, false, 0, 256, 2, 1, NoiseRouterData::floatingIslands),
    ROOFED("roofed", true, true, 0, 128, 1, 2, NoiseRouterData::nether);

    private final String name;
    private final NoiseSettings noise;
    private final Function<Registry<DensityFunction>, NoiseRouter> router;
    private final boolean floor, ceiling;
    private final int height, minY;
    private SeaLevelSelector sea = new FlatSeaLevelSelector(IntRange.of(0, 0));

    MultiverseShape(String name, boolean ceiling, boolean floor, int minY, int height, int sizeHorizontal, int sizeVertical, Function<Registry<DensityFunction>, NoiseRouter> router) {
        this.minY = minY;
        this.name = name;
        this.floor = floor;
        this.ceiling = ceiling;
        this.height = height;
        this.router = router;
        noise = NoiseSettings.create(this.minY, this.height, sizeHorizontal, sizeVertical);
    }

    @Nullable
    public static MultiverseShape byName(String name) {
        for (MultiverseShape type : values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return null;
    }

    public int getMinY() {
        return minY;
    }

    public String getName() {
        return name;
    }

    public ResourceKey<NoiseGeneratorSettings> getNoiseSettingsKey(MultiverseType type) {
        return ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation(Multiverse.MOD_ID, getLocation(type)));
    }

    public String getLocation(MultiverseType type) {
        return type.getName() + "/" + getName();
    }

    public NoiseGeneratorSettings createNoiseSettings(MultiverseType type, Registry<DensityFunction> registry) {
        return new NoiseGeneratorSettings(noise, type.getDefaultBlock(), type.getDefaultFluid(), router.apply(registry), SurfaceRules.state(Blocks.AIR.defaultBlockState()), List.of(), 0, false, true, true, false);
    }

    public boolean hasFloor() {
        return floor;
    }

    public boolean hasCeiling() {
        return ceiling;
    }

    public int getHeight() {
        return height;
    }

    public SerializableFluidPicker getSea(BlockState block, long seed, int index) {
        return sea.getSeaLevel(block, seed, index);
    }

    public void setSeaLevelSelector(SeaLevelSelector sea) {
        this.sea = sea;
    }

}
