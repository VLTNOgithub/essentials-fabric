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

public class CommandTempbanip {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> tempbanipCmd = Commands.literal("tempbanip")
            .then(Commands.argument("target", com.mojang.brigadier.arguments.StringArgumentType.word())
                .then(Commands.argument("time", com.mojang.brigadier.arguments.StringArgumentType.word())
                    .executes(context -> executeTempbanip(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "target"), com.mojang.brigadier.arguments.StringArgumentType.getString(context, "time"), null))
                    .then(Commands.argument("reason", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(context -> executeTempbanip(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "target"), com.mojang.brigadier.arguments.StringArgumentType.getString(context, "time"), com.mojang.brigadier.arguments.StringArgumentType.getString(context, "reason")))
                    )
                )
            )
        ;
        dispatcher.register(tempbanipCmd);
        dispatcher.register(Commands.literal("etempbanip").redirect(tempbanipCmd.build()));


    }

    public static long parseTime(String time) {
        long ms = 0;
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+)([dhms])").matcher(time);
        while (m.find()) {
            long val = Long.parseLong(m.group(1));
            String unit = m.group(2);
            if (unit.equals("d")) ms += val * 86400000L;
            else if (unit.equals("h")) ms += val * 3600000L;
            else if (unit.equals("m")) ms += val * 60000L;
            else if (unit.equals("s")) ms += val * 1000L;
        }
        return ms;
    }

    public static int executeTempbanip(CommandContext<CommandSourceStack> context, String target, String timeStr, String reason) {
        long duration = parseTime(timeStr);
        if (duration <= 0) {
            context.getSource().sendSystemMessage(Component.literal("Invalid time format. Use something like 1d, 5h, 30m."));
            return 0;
        }
        java.util.Date expires = new java.util.Date(System.currentTimeMillis() + duration);

        String ip = target;
        ServerPlayer p = context.getSource().getServer().getPlayerList().getPlayerByName(target);
        if (p != null) {
            java.net.SocketAddress addr = p.connection.getRemoteAddress();
            if (addr instanceof java.net.InetSocketAddress) {
                ip = ((java.net.InetSocketAddress) addr).getAddress().getHostAddress();
            }
        }
        net.minecraft.server.players.IpBanList banList = context.getSource().getServer().getPlayerList().getIpBans();
        banList.add(new net.minecraft.server.players.IpBanListEntry(ip, null, context.getSource().getTextName(), expires, reason != null ? reason : "Temporarily banned by an operator."));
        for (ServerPlayer player : context.getSource().getServer().getPlayerList().getPlayers()) {
            java.net.SocketAddress addr = player.connection.getRemoteAddress();
            if (addr instanceof java.net.InetSocketAddress) {
                String pIp = ((java.net.InetSocketAddress) addr).getAddress().getHostAddress();
                if (pIp.equals(ip)) {
                    player.connection.disconnect(Component.literal(reason != null ? reason : "Temporarily banned by an operator."));
                }
            }
        }
        context.getSource().sendSystemMessage(Component.literal("Temporarily banned IP: " + ip + " until " + expires.toString()));
        return 1;
    }

}
