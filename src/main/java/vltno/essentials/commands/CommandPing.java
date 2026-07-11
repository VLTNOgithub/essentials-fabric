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
            .executes(context -> executePing(context))
        ;
        dispatcher.register(pingCmd);
        dispatcher.register(Commands.literal("echo").redirect(pingCmd.build()));
        dispatcher.register(Commands.literal("eecho").redirect(pingCmd.build()));
        dispatcher.register(Commands.literal("eping").redirect(pingCmd.build()));
        dispatcher.register(Commands.literal("pong").redirect(pingCmd.build()));
        dispatcher.register(Commands.literal("epong").redirect(pingCmd.build()));


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
