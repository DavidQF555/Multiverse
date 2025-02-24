package multiverse.common.world.worldgen.biomes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import multiverse.registration.worldgen.BiomeModifierRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

public record AddFeaturesAllBiomeModifier(HolderSet<PlacedFeature> features,
                                          GenerationStep.Decoration step) implements BiomeModifier {

    public static final Codec<AddFeaturesAllBiomeModifier> CODEC = RecordCodecBuilder.create(inst -> inst.group(
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
    public Codec<? extends AddFeaturesAllBiomeModifier> codec() {
        return BiomeModifierRegistry.ADD_ALL.get();
    }

}
