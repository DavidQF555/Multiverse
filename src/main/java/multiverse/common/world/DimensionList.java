package multiverse.common.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import multiverse.registration.custom.DimensionListRegistry;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

public class DimensionList extends ForgeRegistryEntry<DimensionList> {

    public static final Codec<DimensionList> DIRECT_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceLocation.CODEC.listOf().xmap(Set::copyOf, List::copyOf).optionalFieldOf("values", Set.of()).forGetter(list -> list.values),
            Codec.BOOL.optionalFieldOf("invert", false).forGetter(list -> list.invert)
    ).apply(inst, DimensionList::new));
    public static final Codec<Holder<DimensionList>> CODEC = RegistryFileCodec.create(DimensionListRegistry.LOCATION, DIRECT_CODEC);

    private final Set<ResourceLocation> values;
    private final boolean invert;

    public DimensionList(Set<ResourceLocation> values, boolean invert) {
        this.values = values;
        this.invert = invert;
    }

    public boolean contains(ResourceKey<?> key) {
        return contains(key.location());
    }

    public boolean contains(ResourceLocation loc) {
        return invert ^ values.contains(loc);
    }

    public <T> Optional<ResourceKey<T>> selectRandom(Set<ResourceKey<T>> registry, Random random, @Nullable ResourceKey<T> exclude) {
        Stream<ResourceKey<T>> stream;
        if (invert) {
            stream = registry.stream().filter(key -> !values.contains(key.location()));
        } else {
            stream = registry.stream().filter(key -> values.contains(key.location()));
        }
        if (exclude != null) {
            stream = stream.filter(loc -> !exclude.equals(loc));
        }
        List<ResourceKey<T>> possible = stream.sorted().toList();
        if (possible.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(possible.get(random.nextInt(possible.size())));
    }

}