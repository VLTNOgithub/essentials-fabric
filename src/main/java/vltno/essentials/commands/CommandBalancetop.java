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

public class CommandBalancetop {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> balancetopCmd = Commands.literal("balancetop")
        .executes(context -> executeBalancetop(context, 1))
        .then(Commands.argument("page", com.mojang.brigadier.arguments.IntegerArgumentType.integer(1))
            .executes(context -> executeBalancetop(context, com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(context, "page")))
        )
    ;
        dispatcher.register(balancetopCmd);
        dispatcher.register(Commands.literal("ebalancetop").redirect(balancetopCmd.build()));
        dispatcher.register(Commands.literal("baltop").redirect(balancetopCmd.build()));
        dispatcher.register(Commands.literal("ebaltop").redirect(balancetopCmd.build()));


    }

    public static int executeBalancetop(CommandContext<CommandSourceStack> context) {
            return executeBalancetop(context, 1);
        }

    public static int executeBalancetop(CommandContext<CommandSourceStack> context, int page) {
            java.util.List<ServerPlayer> players = new java.util.ArrayList<>(context.getSource().getServer().getPlayerList().getPlayers());
            players.sort((a, b) -> Double.compare(UserCache.getUser(b).money, UserCache.getUser(a).money));
            context.getSource().sendSystemMessage(Component.literal("--- Balance Top ---"));
            int start = (page - 1) * 10;
            for (int i = start; i < Math.min(start + 10, players.size()); i++) {
                ServerPlayer p = players.get(i);
                context.getSource().sendSystemMessage(Component.literal((i + 1) + ". " + p.getName().getString() + " - $" + String.format("%.2f", UserCache.getUser(p).money)));
            }
            return 1;
        }

}
