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

public class CommandPing {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> pingCmd = Commands.literal("ping")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.ping", 0))
            .executes(context -> executePing(context))
        ;
        com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> pingCmdNode = dispatcher.register(pingCmd);
        dispatcher.register(Commands.literal("echo").requires(pingCmdNode.getRequirement()).redirect(pingCmdNode));
        dispatcher.register(Commands.literal("eecho").requires(pingCmdNode.getRequirement()).redirect(pingCmdNode));
        dispatcher.register(Commands.literal("eping").requires(pingCmdNode.getRequirement()).redirect(pingCmdNode));
        dispatcher.register(Commands.literal("pong").requires(pingCmdNode.getRequirement()).redirect(pingCmdNode));
        dispatcher.register(Commands.literal("epong").requires(pingCmdNode.getRequirement()).redirect(pingCmdNode));


    }

    public static int executePing(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = context.getSource().getPlayerOrException();
            context.getSource().sendSystemMessage(Component.literal("Pong! (" + player.connection.latency() + "ms)"));
        } catch (CommandSyntaxException e) {
            context.getSource().sendSystemMessage(Component.literal("Pong!"));
        }
        return 1;
    }

}
