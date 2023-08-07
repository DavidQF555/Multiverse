package io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.registration.worldgen.BiomeSourceRegistry;
import net.minecraft.core.Holder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;

import java.util.List;
import java.util.Optional;

/**
 * Required because TerraBlender clears features using terrablender.api.mixin.MixinBiomeSource.skipInitialFeaturesPerStep()
 */
public class MultiverseBiomeSource extends MultiNoiseBiomeSource {

    public static final Codec<MultiverseBiomeSource> CODEC = RecordCodecBuilder.<MultiverseBiomeSource>mapCodec(inst -> inst.group(
            ExtraCodecs.nonEmptyList(RecordCodecBuilder.<Pair<Climate.ParameterPoint, Holder<Biome>>>create(builder -> builder.group(
                    Climate.ParameterPoint.CODEC.fieldOf("parameters").forGetter(Pair::getFirst),
                    Biome.CODEC.fieldOf("biome").forGetter(Pair::getSecond)).apply(builder, Pair::of)).listOf()
            ).xmap(Climate.ParameterList::new, Climate.ParameterList::values).fieldOf("biomes").forGetter(source -> source.parameters)
    ).apply(inst, MultiverseBiomeSource::new)).codec();

    public MultiverseBiomeSource(Climate.ParameterList<Holder<Biome>> parameters) {
        super(parameters, Optional.empty());
        featuresPerStep = Suppliers.memoize(() -> buildFeaturesPerStep(List.copyOf(possibleBiomes()), true));
    }

    @Override
    protected Codec<? extends BiomeSource> codec() {
        return BiomeSourceRegistry.MULTIVERSE.get();
    }

}
