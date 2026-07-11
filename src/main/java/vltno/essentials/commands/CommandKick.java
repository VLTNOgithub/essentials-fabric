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

public class CommandKick {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> kickCmd = Commands.literal("kick")
        .executes(context -> executeKick(context, Collections.emptyList(), null))
        .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.players())
            .executes(context -> executeKick(context, net.minecraft.commands.arguments.EntityArgument.getPlayers(context, "targets"), null))
            .then(Commands.argument("reason", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                .executes(context -> executeKick(context, net.minecraft.commands.arguments.EntityArgument.getPlayers(context, "targets"), com.mojang.brigadier.arguments.StringArgumentType.getString(context, "reason")))
            )
        )
    ;
        dispatcher.register(kickCmd);
        dispatcher.register(Commands.literal("ekick").redirect(kickCmd.build()));


    }

    public static int executeKick(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> targets, String reason) {
            if (targets.isEmpty()) {
                context.getSource().sendSystemMessage(Component.literal("Please specify a player to kick."));
                return 0;
            }
            Component reasonComp = Component.literal(reason != null ? reason : "Kicked by an operator.");
            for (ServerPlayer target : targets) {
                target.connection.disconnect(reasonComp);
            }
            context.getSource().sendSystemMessage(Component.literal("Kicked " + targets.size() + " players."));
            return targets.size();
        }

}
