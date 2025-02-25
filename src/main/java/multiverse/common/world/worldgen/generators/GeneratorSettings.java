package multiverse.common.world.worldgen.generators;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public record GeneratorSettings(int count, double temperature, double humidity) {

    public static final Codec<GeneratorSettings> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("count", 0).forGetter(GeneratorSettings::count),
            Codec.DOUBLE.optionalFieldOf("temperature", 0.0).forGetter(GeneratorSettings::temperature),
            Codec.DOUBLE.optionalFieldOf("humidity", 0.0).forGetter(GeneratorSettings::humidity)
    ).apply(inst, GeneratorSettings::new));
    private static final Gson GSON = new GsonBuilder().create();
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final GeneratorSettings DEFAULT = new GeneratorSettings(0, 0, 0);
    private static GeneratorSettings settings = DEFAULT;

    public static GeneratorSettings getSettings() {
        return settings;
    }

    public static void load(MinecraftServer server, ResourceLocation loc) {
        settings = DEFAULT;
        JsonElement value;
        try (Reader reader = new BufferedReader(new InputStreamReader(server.getResourceManager().getResource(loc).getInputStream()))) {
            value = GsonHelper.fromJson(GSON, reader, JsonElement.class);
        } catch (IOException e) {
            return;
        }
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, server.registryAccess());
        CODEC.decode(ops, value).resultOrPartial(LOGGER::error).map(Pair::getFirst)
                .ifPresent(val -> settings = val);
    }

}
