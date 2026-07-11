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

public class CommandWhois {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> whoisCmd = Commands.literal("whois")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.whois", 2))
            .then(Commands.argument("target", com.mojang.brigadier.arguments.StringArgumentType.word())
                .executes(context -> executeWhois(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "target")))
            );
        com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> whoisCmdNode = dispatcher.register(whoisCmd);
        dispatcher.register(Commands.literal("ewhois").requires(whoisCmdNode.getRequirement()).redirect(whoisCmdNode));

    }

    public static int executeWhois(CommandContext<CommandSourceStack> context, String targetName) {
        // Try to find the user in loaded cache or online
        java.util.UUID uuid = null;
        net.minecraft.server.level.ServerPlayer onlinePlayer = context.getSource().getServer().getPlayerList().getPlayerByName(targetName);
        vltno.essentials.UserData data = null;
        if (onlinePlayer != null) {
            uuid = onlinePlayer.getUUID();
            data = vltno.essentials.UserCache.getUser(onlinePlayer);
        } else {
            for (java.util.Map.Entry<java.util.UUID, vltno.essentials.UserData> entry : vltno.essentials.UserCache.getLoadedUsers().entrySet()) {
                if (entry.getValue().nickname != null && entry.getValue().nickname.equalsIgnoreCase(targetName)) {
                    uuid = entry.getKey();
                    data = entry.getValue();
                    break;
                }
            }
        }

        if (data == null) {
            context.getSource().sendSystemMessage(Component.literal("Player not found.").withStyle(net.minecraft.ChatFormatting.RED));
            return 0;
        }

        String name = onlinePlayer != null ? onlinePlayer.getName().getString() : targetName;
        context.getSource().sendSystemMessage(Component.literal("--- Whois: " + name + " ---"));
        if (data.nickname != null) context.getSource().sendSystemMessage(Component.literal(" - Nickname: " + data.nickname));
        context.getSource().sendSystemMessage(Component.literal(" - Money: $" + String.format("%.2f", data.money)));
        if (onlinePlayer != null) {
            context.getSource().sendSystemMessage(Component.literal(" - Health: " + String.format("%.1f", onlinePlayer.getHealth()) + " / " + String.format("%.1f", onlinePlayer.getMaxHealth())));
            context.getSource().sendSystemMessage(Component.literal(" - Hunger: " + onlinePlayer.getFoodData().getFoodLevel() + " / 20"));
            context.getSource().sendSystemMessage(Component.literal(" - Location: " + onlinePlayer.level().dimension().identifier().toString() + " " + String.format("%.1f, %.1f, %.1f", onlinePlayer.getX(), onlinePlayer.getY(), onlinePlayer.getZ())));
        }
        context.getSource().sendSystemMessage(Component.literal(" - Muted: " + (data.isMuted ? "Yes" : "No")));
        context.getSource().sendSystemMessage(Component.literal(" - Jailed: " + (data.jail != null ? data.jail : "No")));
        context.getSource().sendSystemMessage(Component.literal(" - God mode: " + (data.godMode ? "Yes" : "No")));

        return 1;
    }

}
