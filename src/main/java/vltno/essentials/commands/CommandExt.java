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

public class CommandExt {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> extCmd = Commands.literal("ext")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.ext", 2))
            .executes(context -> executeExt(context, Collections.singletonList(context.getSource().getPlayerOrException())))
            .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.entities())
                .executes(context -> executeExt(context, net.minecraft.commands.arguments.EntityArgument.getEntities(context, "targets")))
            );
        com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> extCmdNode = dispatcher.register(extCmd);
        dispatcher.register(Commands.literal("eext").requires(extCmdNode.getRequirement()).redirect(extCmdNode));
        dispatcher.register(Commands.literal("extinguish").requires(extCmdNode.getRequirement()).redirect(extCmdNode));
        dispatcher.register(Commands.literal("eextinguish").requires(extCmdNode.getRequirement()).redirect(extCmdNode));

    }

    public static int executeExt(CommandContext<CommandSourceStack> context, Collection<? extends net.minecraft.world.entity.Entity> targets) throws CommandSyntaxException {
        for (net.minecraft.world.entity.Entity target : targets) {
            target.clearFire();
            if (target instanceof ServerPlayer p && p != context.getSource().getEntity()) {
                p.sendSystemMessage(Component.literal("You have been extinguished."));
            }
        }
        if (targets.size() == 1 && targets.iterator().next() == context.getSource().getEntity()) {
            context.getSource().sendSystemMessage(Component.literal("You have been extinguished."));
        } else {
            context.getSource().sendSystemMessage(Component.literal("Extinguished " + targets.size() + " entities."));
        }
        return targets.size();
    }

}
