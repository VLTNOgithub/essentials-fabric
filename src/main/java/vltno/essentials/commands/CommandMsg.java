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

public class CommandMsg {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("msg")
        .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
            .then(Commands.argument("message", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                .executes(context -> executeMsg(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target"), com.mojang.brigadier.arguments.StringArgumentType.getString(context, "message")))
            )
        )
    );
        dispatcher.register(Commands.literal("w")
            .executes(context -> executeMsg(context))
        );
        dispatcher.register(Commands.literal("m")
            .executes(context -> executeMsg(context))
        );
        dispatcher.register(Commands.literal("t")
            .executes(context -> executeMsg(context))
        );
        dispatcher.register(Commands.literal("pm")
            .executes(context -> executeMsg(context))
        );
        dispatcher.register(Commands.literal("emsg")
            .executes(context -> executeMsg(context))
        );
        dispatcher.register(Commands.literal("epm")
            .executes(context -> executeMsg(context))
        );
        dispatcher.register(Commands.literal("tell")
            .executes(context -> executeMsg(context))
        );
        dispatcher.register(Commands.literal("etell")
            .executes(context -> executeMsg(context))
        );
        dispatcher.register(Commands.literal("whisper")
            .executes(context -> executeMsg(context))
        );
        dispatcher.register(Commands.literal("ewhisper")
            .executes(context -> executeMsg(context))
        );

    }

    public static int executeMsg(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /msg <player> <message>")); return 0; }

    public static int executeMsg(CommandContext<CommandSourceStack> context, ServerPlayer target, String message) throws CommandSyntaxException {
            ServerPlayer sender = context.getSource().getPlayerOrException();
            replyMap.put(sender.getUUID(), target.getUUID());
            replyMap.put(target.getUUID(), sender.getUUID());
            sender.sendSystemMessage(Component.literal("[me -> " + target.getName().getString() + "] " + message));
            target.sendSystemMessage(Component.literal("[" + sender.getName().getString() + " -> me] " + message));
            return 1;
        }

}
