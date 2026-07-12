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

public class CommandR {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
                for (String alias : new String[]{"r", "er", "reply", "ereply"}) {
            dispatcher.register(Commands.literal(alias)
            .requires(vltno.essentials.EssentialsCommands.require("essentials.r", 0))
        .then(Commands.argument("message", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
            .executes(context -> executeR(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "message")))
        )
    );
        }


    }

    public static int executeR(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /r <message>")); return 0; }

    public static int executeR(CommandContext<CommandSourceStack> context, String message) throws CommandSyntaxException {
            ServerPlayer sender = context.getSource().getPlayerOrException();
            java.util.UUID targetId = replyMap.get(sender.getUUID());
            if (targetId == null) {
                context.getSource().sendSystemMessage(Component.literal("You have nobody to reply to."));
                return 0;
            }
            ServerPlayer target = context.getSource().getServer().getPlayerList().getPlayer(targetId);
            if (target == null) {
                context.getSource().sendSystemMessage(Component.literal("That player is offline."));
                return 0;
            }
            sender.sendSystemMessage(Component.literal("[me -> " + target.getName().getString() + "] " + message));
            target.sendSystemMessage(Component.literal("[" + sender.getName().getString() + " -> me] " + message));
            return 1;
        }

}
