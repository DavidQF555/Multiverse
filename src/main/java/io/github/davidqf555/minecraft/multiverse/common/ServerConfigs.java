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

    public final ForgeConfigSpec.DoubleValue travelerSpawnChance, minRiftWidth, maxRiftWidth, fireworkRate, fireRate, minSpawnRadius, maxSpawnRadius, spawnOffset, swordMinWidth, swordMaxWidth, swordWidthRate, swordMinHeight, swordMaxHeight, swordHeightRate, swordSpawnDistance, coreRange;
    public final ForgeConfigSpec.IntValue maxDimensions, riftRange, minRiftHeight, maxRiftHeight, spawnPeriod, spawnCount, slowFalling, swordMinCharge, swordCooldown, armorMinOffset, armorMaxOffset, armorMaxSpawn, armorSpawnPeriod, doppelTimeout, travelerMaxDoppel, travelerDoppelPeriod, travelerMinRange, travelerMaxRange;

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
        builder.pop();
        builder.push("Prismatic Sword");
        swordCooldown = builder.comment("This is the cooldown of the sword's rift spawning in ticks. ")
                .defineInRange("swordCooldown", 500, 0, Integer.MAX_VALUE);
        swordSpawnDistance = builder.comment("This is the distance in blocks from the player's eyes in the direction they look in that the center of the rift spawns from the sword. ")
                .defineInRange("swordSpawnDistance", 2.5, -Double.MAX_VALUE, Double.MAX_VALUE);
        swordMinCharge = builder.comment("This is the minimum number of ticks that the sword needs to be charged in order to spawn a rift. ")
                .defineInRange("swordMinCharge", 20, 0, Integer.MAX_VALUE);
        swordMinWidth = builder.comment("This is the initial width of rifts spawned by the sword when charged the minimum charge. ")
                .defineInRange("swordMinWidth", 1, 0, Double.MAX_VALUE);
        swordMaxWidth = builder.comment("This is the max width of rifts spawned by the sword. Should be at least swordMinWidth. ")
                .defineInRange("swordMaxWidth", 8, 0, Double.MAX_VALUE);
        swordWidthRate = builder.comment("This is the rate that the width of rifts spawned by the sword grow in blocks per tick charged. ")
                .defineInRange("swordWidthRate", 7.0 / 600, 0, Double.MAX_VALUE);
        swordMinHeight = builder.comment("This is the initial height of rifts spawned by the sword when charged the minimum charge. ")
                .defineInRange("swordMinHeight", 16, 0, Double.MAX_VALUE);
        swordMaxHeight = builder.comment("This is the max height of rifts spawned by the sword. Should be at least swordMinHeight. ")
                .defineInRange("swordMaxHeight", 64, 0, Double.MAX_VALUE);
        swordHeightRate = builder.comment("This is the rate that the height of rifts spawned by the sword grow in blocks per tick charged. ")
                .defineInRange("swordHeightRate", 2.0 / 25, 0, Double.MAX_VALUE);
        builder.pop();
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
        builder.pop();
        builder.push("Kaleidite Chestplate");
        armorMinOffset = builder.comment("This is the minimum distance in blocks that a doppelganger will spawn from the wearer. ")
                .defineInRange("armorMinOffset", 1, 0, Integer.MAX_VALUE);
        armorMaxOffset = builder.comment("This is the maximum distance in blocks that a doppelganger will spawn from the wearer. This should be at least armorMinOffset. ")
                .defineInRange("armorMaxOffset", 8, 0, Integer.MAX_VALUE);
        armorMaxSpawn = builder.comment("This is the maximum number of doppelgangers the wearer can spawn at once. ")
                .defineInRange("armorMaxSpawn", 8, 0, Integer.MAX_VALUE);
        armorSpawnPeriod = builder.comment("This is the period in ticks that the wearer spawns doppelgangers when in combat. ")
                .defineInRange("armorSpawnPeriod", 40, 1, Integer.MAX_VALUE);
        builder.pop().push("Traveler");
        travelerMinRange = builder.comment("This is the minimum distance in blocks that the traveler spawns doppelgangers and teleports when hurt. ")
                .defineInRange("travelerMinRange", 8, 0, Integer.MAX_VALUE);
        travelerMaxRange = builder.comment("This is the maximum distance in blocks that the traveler spawns doppelgangers and teleports when hurt. This should be at least travelerMinRange. ")
                .defineInRange("travelerMaxRange", 16, 0, Integer.MAX_VALUE);
        travelerMaxDoppel = builder.comment("This is the maximum number of doppelgangers a traveler can spawn at once. ")
                .defineInRange("travelerMaxDoppel", 10, 0, Integer.MAX_VALUE);
        travelerDoppelPeriod = builder.comment("This is the period in ticks that the traveler spawns doppelgangers. ")
                .defineInRange("travelerDoppelPeriod", 20, 1, Integer.MAX_VALUE);
        builder.pop().push("Miscellaneous");
        travelerSpawnChance = builder.comment("This is the chance that a Traveler spawns per random tick for each rift block. ")
                .defineInRange("travelerSpawnChance", 0.0001, 0, 1);
        doppelTimeout = builder.comment("This is the time in ticks after exiting combat that before doppelgangers despawn. ")
                .defineInRange("doppelTimeout", 600, 0, Integer.MAX_VALUE);
        coreRange = builder.comment("This is the distance in blocks that the kaleidite core searches for connected rifts to remove. ")
                .defineInRange("coreRange", 50, 0, Double.MAX_VALUE);
        builder.pop(2);
    }

}