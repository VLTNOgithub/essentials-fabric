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

public class CommandIgnore {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> ignoreCmd = Commands.literal("ignore")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.ignore", 0))
            .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
                .executes(context -> executeIgnore(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target")))
            );
        com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> ignoreCmdNode = dispatcher.register(ignoreCmd);
        dispatcher.register(Commands.literal("eignore").requires(ignoreCmdNode.getRequirement()).redirect(ignoreCmdNode));
        dispatcher.register(Commands.literal("unignore").requires(ignoreCmdNode.getRequirement()).redirect(ignoreCmdNode));
        dispatcher.register(Commands.literal("eunignore").requires(ignoreCmdNode.getRequirement()).redirect(ignoreCmdNode));
        dispatcher.register(Commands.literal("delignore").requires(ignoreCmdNode.getRequirement()).redirect(ignoreCmdNode));
        dispatcher.register(Commands.literal("edelignore").requires(ignoreCmdNode.getRequirement()).redirect(ignoreCmdNode));
        dispatcher.register(Commands.literal("remignore").requires(ignoreCmdNode.getRequirement()).redirect(ignoreCmdNode));
        dispatcher.register(Commands.literal("eremignore").requires(ignoreCmdNode.getRequirement()).redirect(ignoreCmdNode));
        dispatcher.register(Commands.literal("rmignore").requires(ignoreCmdNode.getRequirement()).redirect(ignoreCmdNode));
        dispatcher.register(Commands.literal("ermignore").requires(ignoreCmdNode.getRequirement()).redirect(ignoreCmdNode));

    }

    public static int executeIgnore(CommandContext<CommandSourceStack> context, ServerPlayer target) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        if (player == target) {
            context.getSource().sendSystemMessage(Component.literal("You cannot ignore yourself."));
            return 0;
        }
        UserData data = UserCache.getUser(player);
        if (data.ignoredPlayers.contains(target.getUUID())) {
            data.ignoredPlayers.remove(target.getUUID());
            context.getSource().sendSystemMessage(Component.literal("You are no longer ignoring " + target.getName().getString() + "."));
        } else {
            data.ignoredPlayers.add(target.getUUID());
            context.getSource().sendSystemMessage(Component.literal("You are now ignoring " + target.getName().getString() + "."));
        }
        UserCache.saveUser(player.getUUID());
        return 1;
    }

}
