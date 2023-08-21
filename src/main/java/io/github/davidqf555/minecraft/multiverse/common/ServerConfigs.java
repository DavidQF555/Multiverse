package io.github.davidqf555.minecraft.multiverse.common;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ServerConfigs {

    public static final ServerConfigs INSTANCE;
    public static final ForgeConfigSpec SPEC;

    static {
        Pair<ServerConfigs, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(ServerConfigs::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }

    public final ForgeConfigSpec.DoubleValue additionalBiomeTagChance, fixedTimeChance, travelerSpawnFactor, fireworkRate, fireRate, minSpawnRadius, maxSpawnRadius, spawnOffset;
    public final ForgeConfigSpec.IntValue maxDimensions, boundlessBladeCooldown, riftRange, spawnPeriod, spawnCount;

    public ServerConfigs(ForgeConfigSpec.Builder builder) {
        builder.comment("Multiverse server-side configuration").push("Dimensions");
        maxDimensions = builder.comment("This is the number of Multiverse dimensions that rifts will generate for. ")
                .defineInRange("max", 25, 1, Integer.MAX_VALUE);
        additionalBiomeTagChance = builder.comment("Each additional biome tag has this chance to be in new Multiverse dimensions. ")
                .defineInRange("biomeTypeChance", 0.025, 0, 1);
        fixedTimeChance = builder.comment("This is the chance that a Multiverse dimension has a random, fixed time. ")
                .defineInRange("fixedTimeChance", 0.25, 0, 1);
        builder.pop().push("KaleiditeCrossbow");
        fireworkRate = builder.comment("This is the chance that fireworks are spawned when shooting an arrow. ")
                .defineInRange("fireworkRate", 0.2, 0, 1);
        fireRate = builder.comment("This is the chance that a spawned arrow is on fire. ")
                .defineInRange("fireRate", 0.2, 0, 1);
        minSpawnRadius = builder.comment("This is the minimum distance in blocks that a projectile can spawn from the shooter. ")
                .defineInRange("minSpawnRadius", 4, 0, Double.MAX_VALUE);
        maxSpawnRadius = builder.comment("This is the maximum distance in blocks that a projectile can spawn from the shooter. This must be at least minSpawnRadius. ")
                .defineInRange("maxSpawnRadius", 10, 0, Double.MAX_VALUE);
        spawnOffset = builder.comment("This is the offset that projectiles are spawned relative to the direction they are shot in blocks. ")
                .defineInRange("spawnOffset", -2, -Double.MAX_VALUE, Double.MAX_VALUE);
        spawnPeriod = builder.comment("This is the period in ticks that projectiles are spawned")
                .defineInRange("spawnPeriod", 5, 1, Integer.MAX_VALUE);
        spawnCount = builder.comment("This is the number of projectiles spawned every time the crossbow is shot. ")
                .defineInRange("spawnCount", 20, 0, Integer.MAX_VALUE);
        builder.pop().push("Miscellaneous");
        riftRange = builder.comment("This is the range that is scanned for existing rifts. ")
                .defineInRange("range", 128, 0, Integer.MAX_VALUE);
        boundlessBladeCooldown = builder.comment("This is the cooldown of the Boundless Blade item in ticks. ")
                .defineInRange("boundlessBladeCooldown", 500, 0, Integer.MAX_VALUE);
        travelerSpawnFactor = builder.comment("This is the factor from the base that Travelers spawn")
                .defineInRange("travelerSpawnFactor", 0.01, 0, 1);
        builder.pop();
    }

}
