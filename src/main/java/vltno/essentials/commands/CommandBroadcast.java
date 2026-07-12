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
                for (String alias : new String[]{"broadcast", "bc", "ebc", "bcast", "ebcast", "ebroadcast", "shout", "eshout"}) {
            dispatcher.register(Commands.literal(alias)
            .requires(vltno.essentials.EssentialsCommands.require("essentials.broadcast", 2))
        .then(Commands.argument("message", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
            .executes(context -> executeBroadcast(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "message")))
        ));
        }

    }

    public static int executeBroadcast(CommandContext<CommandSourceStack> context, String message) {
            context.getSource().getServer().getPlayerList().broadcastSystemMessage(Component.literal("[Broadcast] " + message).withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE), false);
            return 1;
        }

}
