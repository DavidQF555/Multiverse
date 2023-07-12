package io.github.davidqf555.minecraft.multiverse.common.worldgen;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashSet;
import java.util.Set;

public class MultiverseExistingData extends SavedData {

    private static final String NAME = "MultiverseExisting";
    private final Set<Integer> existing = new HashSet<>();

    protected MultiverseExistingData(CompoundTag tag) {
        this();
        if (tag.contains("Existing", Tag.TAG_LIST)) {
            for (Tag nbt : tag.getList("Existing", Tag.TAG_INT)) {
                existing.add(((IntTag) nbt).getAsInt());
            }
        }
    }

    protected MultiverseExistingData() {
    }

    public static MultiverseExistingData getOrCreate(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(MultiverseExistingData::new, MultiverseExistingData::new, NAME);
    }

    public Set<Integer> getExisting() {
        return existing;
    }

    public void add(int index) {
        existing.add(index);
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag all = new ListTag();
        existing.forEach(val -> all.add(IntTag.valueOf(val)));
        tag.put("Existing", all);
        return tag;
    }

}
