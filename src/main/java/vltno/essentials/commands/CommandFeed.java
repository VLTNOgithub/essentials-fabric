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

public class CommandFeed {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
                com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> feedCmd = Commands.literal("feed")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.feed", 0))
            .executes(context -> executeFeed(context, Collections.singletonList(context.getSource().getPlayerOrException())))
            .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.players())
                .executes(context -> executeFeed(context, net.minecraft.commands.arguments.EntityArgument.getPlayers(context, "targets")))
            );
        dispatcher.register(feedCmd);
        dispatcher.register(Commands.literal("eat").executes(feedCmd.getCommand()).redirect(feedCmd.build()));
        dispatcher.register(Commands.literal("eeat").executes(feedCmd.getCommand()).redirect(feedCmd.build()));
        dispatcher.register(Commands.literal("efeed").executes(feedCmd.getCommand()).redirect(feedCmd.build()));


    }

    public static int executeFeed(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> targets) {
        for (ServerPlayer player : targets) {
            player.getFoodData().setFoodLevel(20);
            player.getFoodData().setSaturation(20.0F);
            if (targets.size() == 1 && player == context.getSource().getEntity()) {
                context.getSource().sendSystemMessage(Component.literal("You have been fed."));
            } else {
                player.sendSystemMessage(Component.literal("You have been fed."));
            }
        }
        if (targets.size() > 1 || targets.iterator().next() != context.getSource().getEntity()) {
            context.getSource().sendSystemMessage(Component.literal("Fed " + targets.size() + " players."));
        }
        return targets.size();
    }

}
