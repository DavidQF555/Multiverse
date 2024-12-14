package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.util.ConfigHelper;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.function.Supplier;

public class LazyMultiverseSurfaceRuleSource implements SurfaceRules.RuleSource {

    public static final Codec<LazyMultiverseSurfaceRuleSource> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.BOOL.fieldOf("floor").forGetter(source -> source.floor),
            Codec.BOOL.fieldOf("ceiling").forGetter(source -> source.ceiling),
            Codec.STRING.fieldOf("multiverse_type").xmap(MultiverseType::byName, MultiverseType::getName).forGetter(source -> source.type)
    ).apply(inst, LazyMultiverseSurfaceRuleSource::new));
    private final boolean floor, ceiling;
    private final MultiverseType type;
    private final Supplier<SurfaceRules.RuleSource> rule;

    public LazyMultiverseSurfaceRuleSource(boolean floor, boolean ceiling, MultiverseType type) {
        this.floor = floor;
        this.ceiling = ceiling;
        this.type = type;
        rule = Suppliers.memoize(() -> ConfigHelper.biomes.createSurface(floor, ceiling, type));
    }

    @Override
    public Codec<? extends LazyMultiverseSurfaceRuleSource> codec() {
        return CODEC;
    }

    @Override
    public SurfaceRules.SurfaceRule apply(SurfaceRules.Context context) {
        return rule.get().apply(context);
    }

}
