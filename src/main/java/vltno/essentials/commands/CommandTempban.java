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

public class CommandTempban {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("tempban")
            .then(Commands.argument("target", net.minecraft.commands.arguments.GameProfileArgument.gameProfile())
                .then(Commands.argument("time", com.mojang.brigadier.arguments.StringArgumentType.word())
                    .executes(context -> executeTempban(context, net.minecraft.commands.arguments.GameProfileArgument.getGameProfiles(context, "target"), com.mojang.brigadier.arguments.StringArgumentType.getString(context, "time"), null))
                    .then(Commands.argument("reason", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(context -> executeTempban(context, net.minecraft.commands.arguments.GameProfileArgument.getGameProfiles(context, "target"), com.mojang.brigadier.arguments.StringArgumentType.getString(context, "time"), com.mojang.brigadier.arguments.StringArgumentType.getString(context, "reason")))
                    )
                )
            )
        );
        dispatcher.register(Commands.literal("etempban")
            .then(Commands.argument("target", net.minecraft.commands.arguments.GameProfileArgument.gameProfile())
                .then(Commands.argument("time", com.mojang.brigadier.arguments.StringArgumentType.word())
                    .executes(context -> executeTempban(context, net.minecraft.commands.arguments.GameProfileArgument.getGameProfiles(context, "target"), com.mojang.brigadier.arguments.StringArgumentType.getString(context, "time"), null))
                    .then(Commands.argument("reason", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(context -> executeTempban(context, net.minecraft.commands.arguments.GameProfileArgument.getGameProfiles(context, "target"), com.mojang.brigadier.arguments.StringArgumentType.getString(context, "time"), com.mojang.brigadier.arguments.StringArgumentType.getString(context, "reason")))
                    )
                )
            )
        );

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

    public static int executeTempban(CommandContext<CommandSourceStack> context, Collection<net.minecraft.server.players.NameAndId> targets, String timeStr, String reason) {
        if (targets.isEmpty()) {
            context.getSource().sendSystemMessage(Component.literal("Please specify a player to ban."));
            return 0;
        }
        long duration = parseTime(timeStr);
        if (duration <= 0) {
            context.getSource().sendSystemMessage(Component.literal("Invalid time format. Use something like 1d, 5h, 30m."));
            return 0;
        }
        java.util.Date expires = new java.util.Date(System.currentTimeMillis() + duration);
        net.minecraft.server.players.UserBanList banList = context.getSource().getServer().getPlayerList().getBans();
        for (net.minecraft.server.players.NameAndId profile : targets) {
            net.minecraft.server.players.UserBanListEntry entry = new net.minecraft.server.players.UserBanListEntry(profile, null, context.getSource().getTextName(), expires, reason != null ? reason : "Banned by an operator.");
            banList.add(entry);
            ServerPlayer player = context.getSource().getServer().getPlayerList().getPlayer(profile.id());
            if (player != null) {
                player.connection.disconnect(Component.literal(reason != null ? reason : "Banned by an operator."));
            }
        }
        context.getSource().sendSystemMessage(Component.literal("Temporarily banned " + targets.size() + " players until " + expires.toString() + "."));
        return targets.size();
    }

}
