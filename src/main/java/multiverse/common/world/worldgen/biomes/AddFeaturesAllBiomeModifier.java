package multiverse.common.world.worldgen.biomes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import multiverse.registration.worldgen.BiomeModifierRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeGenerationSettingsBuilder;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

public record AddFeaturesAllBiomeModifier(HolderSet<PlacedFeature> features,
                                          GenerationStep.Decoration step) implements BiomeModifier {

    public static final MapCodec<AddFeaturesAllBiomeModifier> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(AddFeaturesAllBiomeModifier::features),
            GenerationStep.Decoration.CODEC.fieldOf("step").forGetter(AddFeaturesAllBiomeModifier::step)
    ).apply(inst, AddFeaturesAllBiomeModifier::new));

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD) {
            BiomeGenerationSettingsBuilder settings = builder.getGenerationSettings();
            features().forEach(holder -> settings.addFeature(step(), holder));
        }
    }

    @Override
    public MapCodec<? extends AddFeaturesAllBiomeModifier> codec() {
        return BiomeModifierRegistry.ADD_ALL.get();
    }

}
