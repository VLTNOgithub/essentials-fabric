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

public class CommandClearinventory {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> ciCmd = Commands.literal("clearinventory")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.clearinventory", 2))
            .executes(context -> executeClearinventory(context, Collections.singletonList(context.getSource().getPlayerOrException())))
            .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.players())
                .executes(context -> executeClearinventory(context, net.minecraft.commands.arguments.EntityArgument.getPlayers(context, "targets")))
            );
        dispatcher.register(ciCmd);
        dispatcher.register(Commands.literal("ci").redirect(ciCmd.build()));
        dispatcher.register(Commands.literal("eci").redirect(ciCmd.build()));
        dispatcher.register(Commands.literal("clean").redirect(ciCmd.build()));
        dispatcher.register(Commands.literal("eclean").redirect(ciCmd.build()));
        dispatcher.register(Commands.literal("clear").redirect(ciCmd.build()));
        dispatcher.register(Commands.literal("eclear").redirect(ciCmd.build()));
        dispatcher.register(Commands.literal("clearinvent").redirect(ciCmd.build()));
        dispatcher.register(Commands.literal("eclearinvent").redirect(ciCmd.build()));
        dispatcher.register(Commands.literal("eclearinventory").redirect(ciCmd.build()));

    }

    public static int executeClearinventory(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> targets) throws CommandSyntaxException {
        for (ServerPlayer player : targets) {
            player.getInventory().clearContent();
            if (targets.size() == 1 && player == context.getSource().getEntity()) {
                context.getSource().sendSystemMessage(Component.literal("Inventory cleared."));
            } else {
                context.getSource().sendSystemMessage(Component.literal("Inventory of " + player.getName().getString() + " cleared."));
            }
        }
        return targets.size();
    }

}
