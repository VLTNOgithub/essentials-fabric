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

public class CommandBroadcast {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> broadcastCmd = Commands.literal("broadcast")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.broadcast", 2))
        .then(Commands.argument("message", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
            .executes(context -> executeBroadcast(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "message")))
        );
        dispatcher.register(broadcastCmd);
        dispatcher.register(Commands.literal("bc").redirect(broadcastCmd.build()));
        dispatcher.register(Commands.literal("ebc").redirect(broadcastCmd.build()));
        dispatcher.register(Commands.literal("bcast").redirect(broadcastCmd.build()));
        dispatcher.register(Commands.literal("ebcast").redirect(broadcastCmd.build()));
        dispatcher.register(Commands.literal("ebroadcast").redirect(broadcastCmd.build()));
        dispatcher.register(Commands.literal("shout").redirect(broadcastCmd.build()));
        dispatcher.register(Commands.literal("eshout").redirect(broadcastCmd.build()));

    }

    public static int executeBroadcast(CommandContext<CommandSourceStack> context, String message) {
            context.getSource().getServer().getPlayerList().broadcastSystemMessage(Component.literal("[Broadcast] " + message).withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE), false);
            return 1;
        }

}
