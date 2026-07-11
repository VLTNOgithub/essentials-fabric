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

public class CommandMute {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> muteCmd = Commands.literal("mute")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.mute", 2))
            .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
                .executes(context -> executeMute(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target"), ""))
                .then(Commands.argument("time", com.mojang.brigadier.arguments.StringArgumentType.word())
                    .executes(context -> executeMute(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target"), com.mojang.brigadier.arguments.StringArgumentType.getString(context, "time")))
                )
            );
        dispatcher.register(muteCmd);
        dispatcher.register(Commands.literal("emute").executes(muteCmd.getCommand()).redirect(muteCmd.build()));
        dispatcher.register(Commands.literal("silence").executes(muteCmd.getCommand()).redirect(muteCmd.build()));
        dispatcher.register(Commands.literal("esilence").executes(muteCmd.getCommand()).redirect(muteCmd.build()));

        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> unmuteCmd = Commands.literal("unmute")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.unmute", 0))
            .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
                .executes(context -> executeUnmute(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target")))
            );
        dispatcher.register(unmuteCmd);
        dispatcher.register(Commands.literal("eunmute").executes(unmuteCmd.getCommand()).redirect(unmuteCmd.build()));

    }

    public static int executeMute(CommandContext<CommandSourceStack> context, ServerPlayer target, String timeStr) {
        UserData data = UserCache.getUser(target);
        if (data.isMuted && timeStr.isEmpty()) {
            data.isMuted = false;
            data.muteTimeout = 0;
            context.getSource().sendSystemMessage(Component.literal("Unmuted " + target.getName().getString() + "."));
            target.sendSystemMessage(Component.literal("You have been unmuted."));
            return 1;
        }

        long duration = 0;
        if (!timeStr.isEmpty()) {
            duration = CommandTempban.parseTime(timeStr);
            if (duration <= 0) {
                context.getSource().sendSystemMessage(Component.literal("Invalid time format. Use 10m, 1h, etc."));
                return 0;
            }
        }

        data.isMuted = true;
        if (duration > 0) {
            data.muteTimeout = System.currentTimeMillis() + duration;
            context.getSource().sendSystemMessage(Component.literal("Muted " + target.getName().getString() + " for " + timeStr + "."));
            target.sendSystemMessage(Component.literal("You have been muted for " + timeStr + "."));
        } else {
            data.muteTimeout = 0;
            context.getSource().sendSystemMessage(Component.literal("Muted " + target.getName().getString() + " permanently."));
            target.sendSystemMessage(Component.literal("You have been permanently muted."));
        }
        return 1;
    }

    public static int executeUnmute(CommandContext<CommandSourceStack> context, ServerPlayer target) {
        UserData data = UserCache.getUser(target);
        if (data.isMuted) {
            data.isMuted = false;
            data.muteTimeout = 0;
            context.getSource().sendSystemMessage(Component.literal("Unmuted " + target.getName().getString() + "."));
            target.sendSystemMessage(Component.literal("You have been unmuted."));
        } else {
            context.getSource().sendSystemMessage(Component.literal(target.getName().getString() + " is not muted."));
        }
        return 1;
    }

}
