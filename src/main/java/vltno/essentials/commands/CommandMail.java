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

public class CommandMail {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> mailCmd = Commands.literal("mail")
            .then(Commands.literal("read")
                .executes(context -> executeMailRead(context)))
            .then(Commands.literal("clear")
                .executes(context -> executeMailClear(context)))
            .then(Commands.literal("send")
                .then(Commands.argument("target", com.mojang.brigadier.arguments.StringArgumentType.word())
                    .then(Commands.argument("message", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(context -> executeMailSend(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "target"), com.mojang.brigadier.arguments.StringArgumentType.getString(context, "message")))
                    )
                )
            );
        dispatcher.register(mailCmd);
        dispatcher.register(Commands.literal("email").redirect(mailCmd.build()));
        dispatcher.register(Commands.literal("eemail").redirect(mailCmd.build()));
        dispatcher.register(Commands.literal("memo").redirect(mailCmd.build()));
        dispatcher.register(Commands.literal("ememo").redirect(mailCmd.build()));

    }

    public static int executeMailRead(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        UserData data = UserCache.getUser(player);
        if (data.mail.isEmpty()) {
            context.getSource().sendSystemMessage(Component.literal("You have no mail."));
        } else {
            context.getSource().sendSystemMessage(Component.literal("Mail (" + data.mail.size() + "):"));
            for (String m : data.mail) {
                context.getSource().sendSystemMessage(Component.literal(m));
            }
        }
        return 1;
    }

    public static int executeMailClear(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        UserData data = UserCache.getUser(player);
        data.mail.clear();
        UserCache.saveUser(player.getUUID());
        context.getSource().sendSystemMessage(Component.literal("Mail cleared."));
        return 1;
    }

    public static int executeMailSend(CommandContext<CommandSourceStack> context, String targetName, String message) {
        // Try to find the target player (online or offline)
        java.util.UUID targetUuid = null;
        for (ServerPlayer p : context.getSource().getServer().getPlayerList().getPlayers()) {
            if (p.getName().getString().equalsIgnoreCase(targetName)) {
                targetUuid = p.getUUID();
                break;
            }
        }

        if (targetUuid == null) {
            // Fallback: search the user cache by name or assume it's offline
            // For simplicity, we loop all cached UUIDs (in a real scenario, use a name->uuid map)
            for (java.util.Map.Entry<java.util.UUID, UserData> entry : UserCache.getLoadedUsers().entrySet()) {
                if (entry.getValue().nickname != null && entry.getValue().nickname.equalsIgnoreCase(targetName)) {
                    targetUuid = entry.getKey();
                    break;
                }
            }
        }

        if (targetUuid == null) {
            // If still not found, we cannot send mail safely unless we know the UUID.
            context.getSource().sendSystemMessage(Component.literal("Player not found. Mail not sent.").withStyle(net.minecraft.ChatFormatting.RED));
            return 0;
        }

        UserData targetData = UserCache.getUser(targetUuid);
        String senderName = context.getSource().getTextName();
        targetData.mail.add("From " + senderName + ": " + message);
        UserCache.saveUser(targetUuid);
        context.getSource().sendSystemMessage(Component.literal("Mail sent to " + targetName + "."));

        ServerPlayer onlineTarget = context.getSource().getServer().getPlayerList().getPlayer(targetUuid);
        if (onlineTarget != null) {
            onlineTarget.sendSystemMessage(Component.literal("You have new mail! Type /mail read to view it.").withStyle(net.minecraft.ChatFormatting.YELLOW));
        }

        return 1;
    }

}
