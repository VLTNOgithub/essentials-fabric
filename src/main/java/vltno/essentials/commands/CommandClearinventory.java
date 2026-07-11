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
        dispatcher.register(Commands.literal("ci").executes(ciCmd.getCommand()).redirect(ciCmd.build()));
        dispatcher.register(Commands.literal("eci").executes(ciCmd.getCommand()).redirect(ciCmd.build()));
        dispatcher.register(Commands.literal("clean").executes(ciCmd.getCommand()).redirect(ciCmd.build()));
        dispatcher.register(Commands.literal("eclean").executes(ciCmd.getCommand()).redirect(ciCmd.build()));
        dispatcher.register(Commands.literal("clear").executes(ciCmd.getCommand()).redirect(ciCmd.build()));
        dispatcher.register(Commands.literal("eclear").executes(ciCmd.getCommand()).redirect(ciCmd.build()));
        dispatcher.register(Commands.literal("clearinvent").executes(ciCmd.getCommand()).redirect(ciCmd.build()));
        dispatcher.register(Commands.literal("eclearinvent").executes(ciCmd.getCommand()).redirect(ciCmd.build()));
        dispatcher.register(Commands.literal("eclearinventory").executes(ciCmd.getCommand()).redirect(ciCmd.build()));

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
