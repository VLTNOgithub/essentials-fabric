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

public class CommandKill {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> killCmd = Commands.literal("kill")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.kill", 2))
        .executes(context -> executeKill(context, java.util.Collections.singletonList(context.getSource().getPlayerOrException())))
        .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.entities())
            .executes(context -> executeKill(context, net.minecraft.commands.arguments.EntityArgument.getEntities(context, "targets")))
        )
    ;
        dispatcher.register(killCmd);
        dispatcher.register(Commands.literal("ekill").redirect(killCmd.build()));


    }

    public static int executeKill(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /kill <player>")); return 0; }

    public static int executeKill(CommandContext<CommandSourceStack> context, Collection<? extends net.minecraft.world.entity.Entity> targets) {
            for (net.minecraft.world.entity.Entity target : targets) {
                target.kill((net.minecraft.server.level.ServerLevel) target.level());
            }
            context.getSource().sendSystemMessage(Component.literal("Killed " + targets.size() + " entities."));
            return targets.size();
        }

}
