package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.DimensionProvider;
import io.github.davidqf555.minecraft.multiverse.registration.custom.MultiverseShapeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;

public class MultiverseShape {

    public static final Codec<MultiverseShape> DIRECT_CODEC = DimensionProvider.CODEC.xmap(MultiverseShape::new, MultiverseShape::getDimensionProvider);
    public static final Codec<Holder<MultiverseShape>> CODEC = RegistryFileCodec.create(MultiverseShapeRegistry.LOCATION, DIRECT_CODEC);
    private final DimensionProvider dimension;

    public MultiverseShape(DimensionProvider dimension) {
        this.dimension = dimension;
    }

    public DimensionProvider getDimensionProvider() {
        return dimension;
    }

}
