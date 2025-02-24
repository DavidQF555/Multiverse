package multiverse.common.world.worldgen.generators;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;

public class GeneratorSettingsReader {

    private static final Codec<Integer> CODEC = ExtraCodecs.NON_NEGATIVE_INT.fieldOf("count").codec();
    private static final Gson GSON = new GsonBuilder().create();
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ResourceLocation loc;
    private int count;

    public GeneratorSettingsReader(ResourceLocation loc) {
        this.loc = loc;
    }

    public int getDimensionsCount() {
        return count;
    }

    public void load(MinecraftServer server) {
        count = 0;
        JsonElement value;
        try (Reader reader = server.getResourceManager().openAsReader(loc)) {
            value = GsonHelper.fromJson(GSON, reader, JsonElement.class);
        } catch (IOException e) {
            return;
        }
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, server.registryAccess());
        CODEC.decode(ops, value).resultOrPartial(LOGGER::error).map(Pair::getFirst).ifPresent(val -> count = val);
    }

}
