package vltno.essentials.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Collections;
import vltno.essentials.UserCache;
import vltno.essentials.UserData;
import vltno.essentials.EssentialsCommands;
import static vltno.essentials.EssentialsCommands.*;

public class CommandBack {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("back")
        .executes(context -> executeBack(context, java.util.Collections.singletonList(context.getSource().getPlayerOrException())))
        .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.players())
            .executes(context -> executeBack(context, net.minecraft.commands.arguments.EntityArgument.getPlayers(context, "targets")))
        )
    );
        dispatcher.register(Commands.literal("eback")
            .executes(context -> executeBack(context))
        );
        dispatcher.register(Commands.literal("return")
            .executes(context -> executeBack(context))
        );
        dispatcher.register(Commands.literal("ereturn")
            .executes(context -> executeBack(context))
        );

    }

    public static int executeBack(CommandContext<CommandSourceStack> context) throws CommandSyntaxException { return executeBack(context, java.util.Collections.singletonList(context.getSource().getPlayerOrException())); }

    public static int executeBack(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> targets) throws CommandSyntaxException {
            int count = 0;
            for (ServerPlayer target : targets) {
                HomePosition back = backPositions.get(target.getUUID());
                if (back != null) {
                    net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimKey = net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, net.minecraft.resources.Identifier.parse(back.dimension));
                    net.minecraft.server.level.ServerLevel targetLevel = context.getSource().getServer().getLevel(dimKey);
                    if (targetLevel != null) {
                        saveBackLocation(target);
                        target.teleportTo(targetLevel, back.x, back.y, back.z, java.util.Collections.emptySet(), back.yaw, back.pitch, false);
                        if (target == context.getSource().getEntity()) {
                            context.getSource().sendSystemMessage(Component.literal("Teleported back to your previous location."));
                        }
                        count++;
                    }
                } else if (target == context.getSource().getEntity()) {
                    context.getSource().sendSystemMessage(Component.literal("No previous location found."));
                }
            }
            return count;
        }

}
