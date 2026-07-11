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
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> msgCmd = Commands.literal("msg")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.msg", 0))
        .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
            .then(Commands.argument("message", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                .executes(context -> executeMsg(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target"), com.mojang.brigadier.arguments.StringArgumentType.getString(context, "message")))
            )
        );

        dispatcher.register(msgCmd);
        dispatcher.register(Commands.literal("w").executes(msgCmd.getCommand()).redirect(msgCmd.build()));
        dispatcher.register(Commands.literal("m").executes(msgCmd.getCommand()).redirect(msgCmd.build()));
        dispatcher.register(Commands.literal("t").executes(msgCmd.getCommand()).redirect(msgCmd.build()));
        dispatcher.register(Commands.literal("pm").executes(msgCmd.getCommand()).redirect(msgCmd.build()));
        dispatcher.register(Commands.literal("emsg").executes(msgCmd.getCommand()).redirect(msgCmd.build()));
        dispatcher.register(Commands.literal("epm").executes(msgCmd.getCommand()).redirect(msgCmd.build()));
        dispatcher.register(Commands.literal("tell").executes(msgCmd.getCommand()).redirect(msgCmd.build()));
        dispatcher.register(Commands.literal("etell").executes(msgCmd.getCommand()).redirect(msgCmd.build()));
        dispatcher.register(Commands.literal("whisper").executes(msgCmd.getCommand()).redirect(msgCmd.build()));
        dispatcher.register(Commands.literal("ewhisper").executes(msgCmd.getCommand()).redirect(msgCmd.build()));
    }

    public static int executeMsg(CommandContext<CommandSourceStack> context, ServerPlayer target, String message) throws CommandSyntaxException {
        ServerPlayer sender = context.getSource().getPlayerOrException();
        UserData targetData = UserCache.getUser(target);
        if (targetData.ignoredPlayers.contains(sender.getUUID())) {
            context.getSource().sendSystemMessage(Component.literal("You cannot send messages to this player."));
            return 0;
        }
        if (targetData.msgtoggle) {
            context.getSource().sendSystemMessage(Component.literal("This player is not receiving messages."));
            return 0;
        }
        replyMap.put(sender.getUUID(), target.getUUID());
        replyMap.put(target.getUUID(), sender.getUUID());
        sender.sendSystemMessage(Component.literal("[me -> " + target.getName().getString() + "] " + message));
        target.sendSystemMessage(Component.literal("[" + sender.getName().getString() + " -> me] " + message));
        for (ServerPlayer p : context.getSource().getServer().getPlayerList().getPlayers()) {
            if (p != sender && p != target) {
                UserData pData = UserCache.getUser(p);
                if (pData.socialSpy) {
                    p.sendSystemMessage(Component.literal("[SocialSpy] [" + sender.getName().getString() + " -> " + target.getName().getString() + "] " + message).withStyle(net.minecraft.ChatFormatting.GRAY));
                }
            }
        }
        return 1;
    }

}
