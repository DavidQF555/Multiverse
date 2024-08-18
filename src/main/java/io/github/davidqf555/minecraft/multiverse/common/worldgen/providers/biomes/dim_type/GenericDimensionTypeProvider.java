package io.github.davidqf555.minecraft.multiverse.common.worldgen.providers.biomes.dim_type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.MultiverseType;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.data.EffectsManager;
import io.github.davidqf555.minecraft.multiverse.registration.custom.biomes.BiomeDimensionTypeProviderTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.RandomSource;

import java.util.Map;
import java.util.OptionalLong;
import java.util.Set;

public class GenericDimensionTypeProvider implements BiomeDimensionTypeProvider {

    public static final Codec<GenericDimensionTypeProvider> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.BOOL.fieldOf("has_ceiling").forGetter(val -> val.ceiling),
            Codec.INT.fieldOf("min_y").forGetter(val -> val.minY),
            Codec.INT.fieldOf("height").forGetter(val -> val.height)
    ).apply(inst, GenericDimensionTypeProvider::new));
    private final boolean ceiling;
    private final int minY, height;

    public GenericDimensionTypeProvider(boolean ceiling, int minY, int height) {
        this.ceiling = ceiling;
        this.minY = minY;
        this.height = height;
    }

    @Override
    public Holder<DimensionType> provide(RegistryAccess access, long seed, RandomSource random, MultiverseType type, Set<ResourceKey<Biome>> biomes) {
        OptionalLong time = ceiling ? OptionalLong.of(18000) : randomTime(random);
        float lighting = ceiling ? random.nextFloat() * 0.5f + 0.1f : random.nextFloat() * 0.2f;
        ResourceLocation effect = randomEffect(random);
        return Holder.direct(DimensionType.create(time, !ceiling, ceiling, type.isUltrawarm(), type.isNatural(), 1, false, type.isPiglinSafe(), true, true, type.hasRaids(), minY, height, height, type.getInfiniburn(), effect, lighting));
    }

    protected OptionalLong randomTime(RandomSource random) {
        if (random.nextDouble() < ServerConfigs.INSTANCE.fixedTimeChance.get()) {
            return OptionalLong.of(random.nextInt(24000));
        }
        return OptionalLong.empty();
    }

    protected ResourceLocation randomEffect(RandomSource random) {
        Map<ResourceLocation, Integer> effects = EffectsManager.INSTANCE.getEffects();
        int total = effects.values().stream().mapToInt(Integer::intValue).sum();
        int rand = random.nextInt(total);
        for (ResourceLocation type : effects.keySet()) {
            total -= effects.get(type);
            if (rand >= total) {
                return type;
            }
        }
        throw new RuntimeException();
    }

    @Override
    public BiomeDimensionTypeProviderType getType() {
        return BiomeDimensionTypeProviderTypeRegistry.GENERIC.get();
    }

}
