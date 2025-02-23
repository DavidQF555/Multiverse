package multiverse.common.world.capabilities;

import net.minecraft.nbt.ByteTag;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.INBTSerializable;

public class SummonedData implements INBTSerializable<ByteTag> {

    public static final Capability<SummonedData> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    private boolean summoned = false;

    public static boolean isSummoned(Mob entity) {
        return entity.getCapability(CAPABILITY).map(SummonedData::isSummoned).orElse(false);
    }

    public static void setSummoned(Mob entity, boolean summoned) {
        entity.getCapability(CAPABILITY).ifPresent(data -> data.setSummoned(summoned));
    }

    public boolean isSummoned() {
        return summoned;
    }

    public void setSummoned(boolean summoned) {
        this.summoned = summoned;
    }

    @Override
    public ByteTag serializeNBT() {
        return ByteTag.valueOf(isSummoned());
    }

    @Override
    public void deserializeNBT(ByteTag nbt) {
        setSummoned(nbt.getAsByte() != 0);
    }

}
