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

public class CommandBroadcastworld {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> bcwCmd = Commands.literal("broadcastworld")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.broadcastworld", 2))
            .then(Commands.argument("world", com.mojang.brigadier.arguments.StringArgumentType.word())
                .then(Commands.argument("message", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                    .executes(context -> executeBroadcastworld(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "world"), com.mojang.brigadier.arguments.StringArgumentType.getString(context, "message")))
                )
            );
        dispatcher.register(bcwCmd);
        dispatcher.register(Commands.literal("bcw").redirect(bcwCmd.build()));
        dispatcher.register(Commands.literal("ebcw").redirect(bcwCmd.build()));
        dispatcher.register(Commands.literal("bcastw").redirect(bcwCmd.build()));
        dispatcher.register(Commands.literal("ebcastw").redirect(bcwCmd.build()));
        dispatcher.register(Commands.literal("ebroadcastworld").redirect(bcwCmd.build()));
        dispatcher.register(Commands.literal("shoutworld").redirect(bcwCmd.build()));
        dispatcher.register(Commands.literal("eshoutworld").redirect(bcwCmd.build()));

    }

    public static int executeBroadcastworld(CommandContext<CommandSourceStack> context, String worldName, String message) {
        int count = 0;
        Component comp = Component.literal("[" + worldName + "] " + message).withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE);
        for (ServerPlayer p : context.getSource().getServer().getPlayerList().getPlayers()) {
            if (p.level().dimension().identifier().toString().contains(worldName)) {
                p.sendSystemMessage(comp);
                count++;
            }
        }
        context.getSource().sendSystemMessage(Component.literal("Broadcast sent to " + count + " players in " + worldName + "."));
        return 1;
    }

}
