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

public class CommandSudo {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> sudoCmd = Commands.literal("sudo")
            .requires(source -> {
                try {
                    return source.getEntity() == null || source.getServer().getPlayerList().isOp(source.getPlayerOrException().nameAndId());
                } catch (Exception e) {
                    return true;
                }
            })
            .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
                .then(Commands.argument("command", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                    .executes(context -> executeSudo(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target"), com.mojang.brigadier.arguments.StringArgumentType.getString(context, "command")))
                )
            );
        dispatcher.register(sudoCmd);
        dispatcher.register(Commands.literal("esudo").redirect(sudoCmd.build()));

    }

    public static int executeSudo(CommandContext<CommandSourceStack> context, ServerPlayer target, String commandStr) {
        if (commandStr.startsWith("c:")) {
            // Send chat message bypassing command processing if possible, or just parse it
            // For now, since sendChat isn't straightforward without signature handling, we can't easily force them to chat.
            // We'll skip forcing chat and just process it as a command if it is one.
            context.getSource().sendSystemMessage(Component.literal("Forcing chat is restricted in 1.19+ due to chat signing.").withStyle(net.minecraft.ChatFormatting.RED));
            return 0;
        } else {
            String cmd = commandStr.startsWith("/") ? commandStr.substring(1) : commandStr;
            context.getSource().getServer().getCommands().performPrefixedCommand(target.createCommandSourceStack(), cmd);
            context.getSource().sendSystemMessage(Component.literal("Forced " + target.getName().getString() + " to execute: " + commandStr));
        }
        return 1;
    }

}
