package io.github.davidqf555.minecraft.multiverse.common.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NBTCapabilityProvider<T extends Tag, M extends INBTSerializable<T>> implements ICapabilitySerializable<T> {

    private final Capability<M> capability;
    private final M instance;

    public NBTCapabilityProvider(Capability<M> capability, M instance) {
        this.capability = capability;
        this.instance = instance;
    }

    @NotNull
    @Override
    public <C> LazyOptional<C> getCapability(@NotNull Capability<C> cap, @Nullable Direction side) {
        return cap == capability ? LazyOptional.of(() -> instance).cast() : LazyOptional.empty();
    }

    @Override
    public T serializeNBT() {
        return instance.serializeNBT();
    }

    @Override
    public void deserializeNBT(T nbt) {
        instance.deserializeNBT(nbt);
    }

}
