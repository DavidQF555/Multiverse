package multiverse.common.world.worldgen.generators;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import multiverse.common.world.worldgen.MultiverseShape;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.RandomSource;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

public record ShapeDimensionGenerator(List<Entry> entries) {

    public static final Codec<ShapeDimensionGenerator> CODEC = Entry.CODEC.listOf().xmap(ShapeDimensionGenerator::new, ShapeDimensionGenerator::entries).fieldOf("shapes").codec();
    private static final Gson GSON = new GsonBuilder().create();
    private static final Logger LOGGER = LogUtils.getLogger();

    public static ShapeDimensionGenerator load(MinecraftServer server, ResourceLocation loc) throws IOException {
        JsonElement value;
        try (Reader reader = new BufferedReader(new InputStreamReader(server.getResourceManager().getResource(loc).getInputStream()))) {
            value = GsonHelper.fromJson(GSON, reader, JsonElement.class);
        }
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, server.registryAccess());
        return CODEC.decode(ops, value).resultOrPartial(LOGGER::error).map(Pair::getFirst).orElseThrow(IllegalStateException::new);
    }

    public LevelStem createDimension(RegistryAccess access, long seed, RandomSource random) {
        int total = entries().stream().mapToInt(Entry::weight).sum();
        if (total <= 0) {
            throw new IllegalStateException("Total shape weights cannot be 0 when generating multiverse dimensions");
        }
        int rand = random.nextInt(total);
        for (Entry entry : entries) {
            total -= entry.weight();
            if (total <= rand) {
                return entry.shape().value().getDimensionProvider().createDimension(access, seed, random);
            }
        }
        throw new RuntimeException("Should never get here");
    }

    public LevelStem createDimension(RegistryAccess access, long base, int index) {
        long seed = GeneratorHelper.getSeed(base, index);
        RandomSource random = new SingleThreadedRandomSource(seed);
        return createDimension(access, seed, random);
    }

    public record Entry(Holder<MultiverseShape> shape, int weight) {
        private static final Codec<Entry> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                MultiverseShape.CODEC.fieldOf("shape").forGetter(Entry::shape),
                ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("weight", 1).forGetter(Entry::weight)
        ).apply(inst, Entry::new));
    }

}
