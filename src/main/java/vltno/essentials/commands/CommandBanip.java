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

public class CommandBanip {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> banipCmd = Commands.literal("banip")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.banip", 2))
            .then(Commands.argument("target", com.mojang.brigadier.arguments.StringArgumentType.word())
                .executes(context -> executeBanip(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "target"), null))
                .then(Commands.argument("reason", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                    .executes(context -> executeBanip(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "target"), com.mojang.brigadier.arguments.StringArgumentType.getString(context, "reason")))
                )
            )
        ;
        dispatcher.register(banipCmd);
        dispatcher.register(Commands.literal("ebanip").redirect(banipCmd.build()));


    }

    public static int executeBanip(CommandContext<CommandSourceStack> context, String target, String reason) {
        String ip = target;
        ServerPlayer p = context.getSource().getServer().getPlayerList().getPlayerByName(target);
        if (p != null) {
            java.net.SocketAddress addr = p.connection.getRemoteAddress();
            if (addr instanceof java.net.InetSocketAddress) {
                ip = ((java.net.InetSocketAddress) addr).getAddress().getHostAddress();
            }
        }
        net.minecraft.server.players.IpBanList banList = context.getSource().getServer().getPlayerList().getIpBans();
        banList.add(new net.minecraft.server.players.IpBanListEntry(ip, null, context.getSource().getTextName(), null, reason != null ? reason : "Banned by an operator."));
        for (ServerPlayer player : context.getSource().getServer().getPlayerList().getPlayers()) {
            java.net.SocketAddress addr = player.connection.getRemoteAddress();
            if (addr instanceof java.net.InetSocketAddress) {
                String pIp = ((java.net.InetSocketAddress) addr).getAddress().getHostAddress();
                if (pIp.equals(ip)) {
                    player.connection.disconnect(Component.literal(reason != null ? reason : "Banned by an operator."));
                }
            }
        }
        context.getSource().sendSystemMessage(Component.literal("Banned IP: " + ip));
        return 1;
    }

}
