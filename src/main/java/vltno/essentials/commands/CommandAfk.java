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

public class CommandAfk {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
                com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> afkCmd = Commands.literal("afk")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.afk", 0))
            .executes(context -> executeAfk(context, ""))
            .then(Commands.argument("message", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                .executes(context -> executeAfk(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "message")))
            );
        dispatcher.register(afkCmd);
        dispatcher.register(Commands.literal("eafk").redirect(afkCmd.build()));
        dispatcher.register(Commands.literal("away").redirect(afkCmd.build()));
        dispatcher.register(Commands.literal("eaway").redirect(afkCmd.build()));


    }

    public static int executeAfk(CommandContext<CommandSourceStack> context, String message) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        // Optionally, check if message is a player target instead, but AFK message is more common
        String suffix = message.isEmpty() ? "" : " : " + message;
        if (afkPlayers.contains(player.getUUID())) {
            afkPlayers.remove(player.getUUID());
            context.getSource().getServer().getPlayerList().broadcastSystemMessage(Component.literal(player.getName().getString() + " is no longer AFK."), false);
        } else {
            afkPlayers.add(player.getUUID());
            context.getSource().getServer().getPlayerList().broadcastSystemMessage(Component.literal(player.getName().getString() + " is now AFK." + suffix), false);
        }
        return 1;
    }

}
