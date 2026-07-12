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

public class CommandRest {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
                for (String alias : new String[]{"rest", "erest"}) {
            dispatcher.register(Commands.literal(alias)
            .requires(vltno.essentials.EssentialsCommands.require("essentials.rest", 0))
            .executes(context -> executeRest(context, java.util.Collections.singletonList(context.getSource().getPlayerOrException())))
            .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.players())
                .executes(context -> executeRest(context, net.minecraft.commands.arguments.EntityArgument.getPlayers(context, "targets")))
            ));
        }

    }

    public static int executeRest(CommandContext<CommandSourceStack> context, java.util.Collection<ServerPlayer> targets) {
        for (ServerPlayer target : targets) {
            target.resetStat(net.minecraft.stats.Stats.CUSTOM.get(net.minecraft.stats.Stats.TIME_SINCE_REST));
            if (target == context.getSource().getEntity()) {
                context.getSource().sendSystemMessage(Component.literal("You are now fully rested."));
            } else {
                target.sendSystemMessage(Component.literal("You have been fully rested."));
            }
        }
        if (targets.size() > 1 || targets.iterator().next() != context.getSource().getEntity()) {
            context.getSource().sendSystemMessage(Component.literal("Rested " + targets.size() + " players."));
        }
        return targets.size();
    }

}
