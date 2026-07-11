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

public class CommandList {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> listCmd = Commands.literal("list")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.list", 0))
            .executes(context -> executeList(context))
        ;
        com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> listCmdNode = dispatcher.register(listCmd);
        dispatcher.register(Commands.literal("elist").requires(listCmdNode.getRequirement()).redirect(listCmdNode));
        dispatcher.register(Commands.literal("online").requires(listCmdNode.getRequirement()).redirect(listCmdNode));
        dispatcher.register(Commands.literal("eonline").requires(listCmdNode.getRequirement()).redirect(listCmdNode));
        dispatcher.register(Commands.literal("playerlist").requires(listCmdNode.getRequirement()).redirect(listCmdNode));
        dispatcher.register(Commands.literal("eplayerlist").requires(listCmdNode.getRequirement()).redirect(listCmdNode));
        dispatcher.register(Commands.literal("plist").requires(listCmdNode.getRequirement()).redirect(listCmdNode));
        dispatcher.register(Commands.literal("eplist").requires(listCmdNode.getRequirement()).redirect(listCmdNode));
        dispatcher.register(Commands.literal("who").requires(listCmdNode.getRequirement()).redirect(listCmdNode));
        dispatcher.register(Commands.literal("ewho").requires(listCmdNode.getRequirement()).redirect(listCmdNode));


    }

    public static int executeList(CommandContext<CommandSourceStack> context) {
            java.util.List<ServerPlayer> players = context.getSource().getServer().getPlayerList().getPlayers();
            String names = players.stream().map(p -> p.getName().getString()).collect(java.util.stream.Collectors.joining(", "));
            context.getSource().sendSystemMessage(Component.literal("There are " + players.size() + "/" + context.getSource().getServer().getMaxPlayers() + " players online: \n" + names));
            return 1;
        }

}
