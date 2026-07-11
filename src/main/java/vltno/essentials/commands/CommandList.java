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
        dispatcher.register(listCmd);
        dispatcher.register(Commands.literal("elist").redirect(listCmd.build()));
        dispatcher.register(Commands.literal("online").redirect(listCmd.build()));
        dispatcher.register(Commands.literal("eonline").redirect(listCmd.build()));
        dispatcher.register(Commands.literal("playerlist").redirect(listCmd.build()));
        dispatcher.register(Commands.literal("eplayerlist").redirect(listCmd.build()));
        dispatcher.register(Commands.literal("plist").redirect(listCmd.build()));
        dispatcher.register(Commands.literal("eplist").redirect(listCmd.build()));
        dispatcher.register(Commands.literal("who").redirect(listCmd.build()));
        dispatcher.register(Commands.literal("ewho").redirect(listCmd.build()));


    }

    public static int executeList(CommandContext<CommandSourceStack> context) {
            java.util.List<ServerPlayer> players = context.getSource().getServer().getPlayerList().getPlayers();
            String names = players.stream().map(p -> p.getName().getString()).collect(java.util.stream.Collectors.joining(", "));
            context.getSource().sendSystemMessage(Component.literal("There are " + players.size() + "/" + context.getSource().getServer().getMaxPlayers() + " players online: \n" + names));
            return 1;
        }

}
