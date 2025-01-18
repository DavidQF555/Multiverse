package io.github.davidqf555.minecraft.multiverse.registration;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class AttachmentTypeRegistry {

    public static final DeferredRegister<AttachmentType<?>> TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Multiverse.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> SUMMONED = register("summoned", AttachmentType.builder(() -> false).serialize(Codec.BOOL));

    private AttachmentTypeRegistry() {
    }

    private static <T> DeferredHolder<AttachmentType<?>, AttachmentType<T>> register(String name, AttachmentType.Builder<T> builder) {
        return TYPES.register(name, builder::build);
    }

}
