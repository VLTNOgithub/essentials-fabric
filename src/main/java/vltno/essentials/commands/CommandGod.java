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

public class CommandGod {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> godCmd = Commands.literal("god")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.god", 2))
            .executes(context -> executeGod(context, Collections.singletonList(context.getSource().getPlayerOrException()), -1))
            .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.players())
                .executes(context -> executeGod(context, net.minecraft.commands.arguments.EntityArgument.getPlayers(context, "targets"), -1))
                .then(Commands.literal("on").executes(context -> executeGod(context, net.minecraft.commands.arguments.EntityArgument.getPlayers(context, "targets"), 1)))
                .then(Commands.literal("off").executes(context -> executeGod(context, net.minecraft.commands.arguments.EntityArgument.getPlayers(context, "targets"), 0)))
            );
        dispatcher.register(godCmd);
        dispatcher.register(Commands.literal("egod").executes(godCmd.getCommand()).redirect(godCmd.build()));
        dispatcher.register(Commands.literal("godmode").executes(godCmd.getCommand()).redirect(godCmd.build()));
        dispatcher.register(Commands.literal("egodmode").executes(godCmd.getCommand()).redirect(godCmd.build()));
        dispatcher.register(Commands.literal("tgm").executes(godCmd.getCommand()).redirect(godCmd.build()));
        dispatcher.register(Commands.literal("etgm").executes(godCmd.getCommand()).redirect(godCmd.build()));
    }

    public static int executeGod(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> targets, int state) {
        for (ServerPlayer player : targets) {
            boolean isGod = player.isInvulnerable();
            boolean newState = state == 1 ? true : (state == 0 ? false : !isGod);
            player.setInvulnerable(newState);
            UserData data = UserCache.getUser(player);
            data.godMode = newState;
            UserCache.saveUser(player.getUUID());
            
            if (targets.size() == 1 && player == context.getSource().getEntity()) {
                context.getSource().sendSystemMessage(Component.literal("God mode " + (newState ? "enabled" : "disabled") + "."));
            } else {
                player.sendSystemMessage(Component.literal("God mode " + (newState ? "enabled" : "disabled") + "."));
            }
        }
        if (targets.size() > 1 || (context.getSource().getEntity() != null && targets.iterator().next() != context.getSource().getEntity())) {
            context.getSource().sendSystemMessage(Component.literal("Updated god mode for " + targets.size() + " players."));
        }
        return targets.size();
    }
}