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

public class CommandBan {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
                for (String alias : new String[]{"ban", "eban"}) {
            dispatcher.register(Commands.literal(alias)
            .requires(vltno.essentials.EssentialsCommands.require("essentials.ban", 2))
        .executes(context -> executeBan(context, Collections.emptyList(), null))
        .then(Commands.argument("targets", net.minecraft.commands.arguments.GameProfileArgument.gameProfile())
            .executes(context -> executeBan(context, net.minecraft.commands.arguments.GameProfileArgument.getGameProfiles(context, "targets"), null))
            .then(Commands.argument("reason", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                .executes(context -> executeBan(context, net.minecraft.commands.arguments.GameProfileArgument.getGameProfiles(context, "targets"), com.mojang.brigadier.arguments.StringArgumentType.getString(context, "reason")))
            )
        )
    );
        }


    }

    public static int executeBan(CommandContext<CommandSourceStack> context, Collection<net.minecraft.server.players.NameAndId> targets, String reason) {
            if (targets.isEmpty()) {
                context.getSource().sendSystemMessage(Component.literal("Please specify a player to ban."));
                return 0;
            }
            net.minecraft.server.players.UserBanList banList = context.getSource().getServer().getPlayerList().getBans();
            for (net.minecraft.server.players.NameAndId profile : targets) {
                net.minecraft.server.players.UserBanListEntry entry = new net.minecraft.server.players.UserBanListEntry(profile, null, context.getSource().getTextName(), null, reason != null ? reason : "Banned by an operator.");
                banList.add(entry);
                ServerPlayer player = context.getSource().getServer().getPlayerList().getPlayer(profile.id());
                if (player != null) {
                    player.connection.disconnect(Component.literal(reason != null ? reason : "Banned by an operator."));
                }
            }
            context.getSource().sendSystemMessage(Component.literal("Banned " + targets.size() + " players."));
            return targets.size();
        }

}
