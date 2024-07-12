package io.github.davidqf555.minecraft.multiverse.common.worldgen.biomes;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.davidqf555.minecraft.multiverse.registration.worldgen.BiomeModifierRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.MobSpawnSettingsBuilder;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

import java.util.List;
import java.util.function.Function;

public class CategoryAddSpawnsBiomeModifier implements BiomeModifier {

    public static final MapCodec<CategoryAddSpawnsBiomeModifier> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            MobCategory.CODEC.fieldOf("category").forGetter(modifier -> modifier.category),
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(modifier -> modifier.biomes),
            Codec.either(MobSpawnSettings.SpawnerData.CODEC.listOf(), MobSpawnSettings.SpawnerData.CODEC).xmap(
                    either -> either.map(Function.identity(), List::of),
                    list -> list.size() == 1 ? Either.right(list.get(0)) : Either.left(list)
            ).fieldOf("spawners").forGetter(modifier -> modifier.spawners)
    ).apply(inst, CategoryAddSpawnsBiomeModifier::new));
    private final HolderSet<Biome> biomes;
    private final List<MobSpawnSettings.SpawnerData> spawners;
    private final MobCategory category;

    public CategoryAddSpawnsBiomeModifier(MobCategory category, HolderSet<Biome> biomes, List<MobSpawnSettings.SpawnerData> spawners) {
        this.category = category;
        this.biomes = biomes;
        this.spawners = spawners;
    }

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD && biomes.contains(biome)) {
            MobSpawnSettingsBuilder spawns = builder.getMobSpawnSettings();
            for (MobSpawnSettings.SpawnerData spawner : spawners) {
                spawns.addSpawn(category, spawner);
            }
        }
    }

    @Override
    public MapCodec<? extends CategoryAddSpawnsBiomeModifier> codec() {
        return BiomeModifierRegistry.CATEGORY_ADD_SPAWNS.get();
    }

}
