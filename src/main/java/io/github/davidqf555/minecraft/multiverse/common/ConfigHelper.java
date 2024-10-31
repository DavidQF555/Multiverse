package io.github.davidqf555.minecraft.multiverse.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes.MultiverseBiomes;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes.VanillaMultiverseBiomes;
import net.minecraft.world.phys.Vec3;

public final class ConfigHelper {

    public static final Codec<Vec3> VEC_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.DOUBLE.fieldOf("x").forGetter(Vec3::x),
            Codec.DOUBLE.fieldOf("y").forGetter(Vec3::y),
            Codec.DOUBLE.fieldOf("z").forGetter(Vec3::z)
    ).apply(inst, Vec3::new));
    public static MultiverseBiomes biomes = VanillaMultiverseBiomes.INSTANCE;

    private ConfigHelper() {
    }

}
