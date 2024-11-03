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

    public final ForgeConfigSpec.DoubleValue travelerSpawnChance, minRiftWidth, maxRiftWidth, fireworkRate, fireRate, minSpawnRadius, maxSpawnRadius, spawnOffset;
    public final ForgeConfigSpec.IntValue maxDimensions, boundlessBladeCooldown, riftRange, minRiftHeight, maxRiftHeight, spawnPeriod, spawnCount, slowFalling;

    public ServerConfigs(ForgeConfigSpec.Builder builder) {
        builder.comment("Multiverse server-side configuration").push("Dimensions");
        maxDimensions = builder.comment("This is the number of Multiverse dimensions that rifts will generate for. ")
                .defineInRange("max", 25, 1, Integer.MAX_VALUE);
        builder.pop().push("Rifts");
        riftRange = builder.comment("This is the range that is scanned for existing rifts using points of interest. ")
                .defineInRange("range", 128, 0, Integer.MAX_VALUE);
        slowFalling = builder.comment("This is the number of ticks that players get slow falling for after exiting a rift. Set to 0 if don't want slow falling. ")
                .defineInRange("slowFalling", 600, 0, Integer.MAX_VALUE);
        builder.push("Size of artificially placed rifts (Modify configured/placed feature for naturally generated rifts)");
        minRiftWidth = builder.comment("This is the minimum width of artificially placed rifts. ")
                .defineInRange("minWidth", 1, 0, Double.MAX_VALUE);
        maxRiftWidth = builder.comment("This is the maximum width of artificially placed rifts. This should be greater or equal to minWidth. ")
                .defineInRange("maxWidth", 4, 0, Double.MAX_VALUE);
        minRiftHeight = builder.comment("This is the minimum height of artificially placed rifts. ")
                .defineInRange("minHeight", 16, 0, Integer.MAX_VALUE);
        maxRiftHeight = builder.comment("This is the maximum height of artificially placed rifts. This should be greater or equal to minHeight. ")
                .defineInRange("maxHeight", 48, 0, Integer.MAX_VALUE);
        builder.pop(2);
        builder.push("KaleiditeCrossbow");
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
        boundlessBladeCooldown = builder.comment("This is the cooldown of the Boundless Blade item in ticks. ")
                .defineInRange("boundlessBladeCooldown", 500, 0, Integer.MAX_VALUE);
        travelerSpawnChance = builder.comment("This is the chance that a Traveler spawns per random tick for each rift block. ")
                .defineInRange("travelerSpawnChance", 0.0001, 0, 1);
        builder.pop();
    }

}
