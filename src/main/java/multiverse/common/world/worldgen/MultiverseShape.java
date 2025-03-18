package multiverse.common.world.worldgen;

import com.mojang.serialization.Codec;
import multiverse.common.world.worldgen.generators.DimensionGenerator;
import multiverse.registration.custom.generator.MultiverseShapeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class MultiverseShape extends ForgeRegistryEntry<MultiverseShape> {

    public static final Codec<MultiverseShape> DIRECT_CODEC = DimensionGenerator.CODEC.xmap(MultiverseShape::new, MultiverseShape::getDimensionProvider);
    public static final Codec<Holder<MultiverseShape>> CODEC = RegistryFileCodec.create(MultiverseShapeRegistry.LOCATION, DIRECT_CODEC);
    private final DimensionGenerator dimension;

    public MultiverseShape(DimensionGenerator dimension) {
        this.dimension = dimension;
    }

    public DimensionGenerator getDimensionProvider() {
        return dimension;
    }

}
