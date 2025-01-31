package io.github.davidqf555.minecraft.multiverse.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.world.DimensionHelper;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;

public final class MultiverseCommand {

    private static final String OUT_OF_BOUNDS = Util.makeDescriptionId("command", ResourceLocation.fromNamespaceAndPath(Multiverse.MOD_ID, "multiverse.out_of_bounds"));

    private MultiverseCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("multiverse")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("index", IntegerArgumentType.integer(0))
                        .executes(context -> teleport(context.getSource(), IntegerArgumentType.getInteger(context, "index")))
                )
        );
    }

    private static int teleport(CommandSourceStack stack, int index) throws CommandSyntaxException {
        Entity entity = stack.getEntityOrException();
        ServerLevel world = stack.getServer().getLevel(DimensionHelper.getRegistryKey(index));
        if (world == null) {
            stack.sendFailure(Component.translatable(OUT_OF_BOUNDS, index, ServerConfigs.INSTANCE.maxDimensions.get()));
            return 0;
        }
        Vec3 pos = DimensionHelper.translate(entity.position(), entity.level().dimensionType(), world.dimensionType(), true);
        world.getChunkAt(BlockPos.containing(pos));
        entity.changeDimension(new DimensionTransition(world, pos, Vec3.ZERO, entity.getYRot(), entity.getXRot(), DimensionTransition.DO_NOTHING));
        return 1;
    }

}
