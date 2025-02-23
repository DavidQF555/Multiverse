package multiverse.common.world.worldgen;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import multiverse.common.util.MultiverseConfig;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.function.Supplier;

public class LazyMultiverseSurfaceRuleSource implements SurfaceRules.RuleSource {

    public static final MapCodec<LazyMultiverseSurfaceRuleSource> DIRECT = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.BOOL.fieldOf("floor").forGetter(source -> source.floor),
            Codec.BOOL.fieldOf("ceiling").forGetter(source -> source.ceiling),
            MultiverseType.CODEC.fieldOf("multiverse_type").forGetter(source -> source.type)
    ).apply(inst, LazyMultiverseSurfaceRuleSource::new));
    public static final KeyDispatchDataCodec<LazyMultiverseSurfaceRuleSource> CODEC = KeyDispatchDataCodec.of(DIRECT);
    private final boolean floor, ceiling;
    private final MultiverseType type;
    private final Supplier<SurfaceRules.RuleSource> rule;

    public LazyMultiverseSurfaceRuleSource(boolean floor, boolean ceiling, MultiverseType type) {
        this.floor = floor;
        this.ceiling = ceiling;
        this.type = type;
        rule = Suppliers.memoize(() -> MultiverseConfig.getBiomesManager().createSurface(floor, ceiling, type));
    }

    @Override
    public SurfaceRules.SurfaceRule apply(SurfaceRules.Context context) {
        return rule.get().apply(context);
    }

    @Override
    public KeyDispatchDataCodec<? extends SurfaceRules.RuleSource> codec() {
        return CODEC;
    }

}
