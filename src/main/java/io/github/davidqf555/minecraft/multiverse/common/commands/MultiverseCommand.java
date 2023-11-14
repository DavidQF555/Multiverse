package io.github.davidqf555.minecraft.multiverse.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.davidqf555.minecraft.multiverse.common.Multiverse;
import io.github.davidqf555.minecraft.multiverse.common.ServerConfigs;
import io.github.davidqf555.minecraft.multiverse.common.worldgen.DimensionHelper;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public final class MultiverseCommand {

    private static final Component NO_DIMENSIONS = new TranslatableComponent(Util.makeDescriptionId("command", new ResourceLocation(Multiverse.MOD_ID, "multiverse.no_dimensions")));
    private static final Component INDICES_HEADER = new TranslatableComponent(Util.makeDescriptionId("command", new ResourceLocation(Multiverse.MOD_ID, "multiverse.indices_header")));
    private static final String INDEX = Util.makeDescriptionId("command", new ResourceLocation(Multiverse.MOD_ID, "multiverse.index"));
    private static final String OUT_OF_BOUNDS = Util.makeDescriptionId("command", new ResourceLocation(Multiverse.MOD_ID, "multiverse.out_of_bounds"));

    private MultiverseCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("multiverse")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("list")
                        .executes(context -> list(context.getSource()))
                )
                .then(Commands.literal("teleport")
                        .then(Commands.argument("index", IntegerArgumentType.integer(0))
                                .executes(context -> teleport(context.getSource(), IntegerArgumentType.getInteger(context, "index")))
                        )
                )
        );
    }

    private static int list(CommandSourceStack stack) {
        List<Integer> indices = new ArrayList<>();
        for (ResourceKey<Level> world : stack.getServer().levelKeys()) {
            int index = DimensionHelper.getIndex(world);
            if (index > 0) {
                indices.add(index);
            }
        }
        if (indices.isEmpty()) {
            stack.sendSuccess(NO_DIMENSIONS, false);
        } else {
            stack.sendSuccess(INDICES_HEADER, false);
            indices.forEach(index -> stack.sendSuccess(new TranslatableComponent(INDEX, index), false));
        }
        return 1;
    }

    private static int teleport(CommandSourceStack stack, int index) throws CommandSyntaxException {
        Entity entity = stack.getEntityOrException();
        Optional<ServerLevel> op = DimensionHelper.getWorld(stack.getServer(), index);
        if (op.isEmpty() && index > ServerConfigs.INSTANCE.maxDimensions.get()) {
            stack.sendFailure(new TranslatableComponent(OUT_OF_BOUNDS, index, ServerConfigs.INSTANCE.maxDimensions.get()));
            return 0;
        }
        ServerLevel world = op.orElseGet(() -> DimensionHelper.getOrCreateWorld(stack.getServer(), index));
        Vec3 pos = DimensionHelper.translate(entity.position(), entity.level.dimensionType(), world.dimensionType(), true);
        world.getChunkAt(new BlockPos(pos));
        entity.changeDimension(world, new ITeleporter() {
            @Override
            public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
                return new PortalInfo(pos, Vec3.ZERO, entity.getYRot(), entity.getXRot());
            }
        });
        return 1;
    }

}
