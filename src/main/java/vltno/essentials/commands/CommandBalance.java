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

public class CommandBalance {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> balanceCmd = Commands.literal("balance")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.balance", 0))
        .executes(context -> executeBalance(context, context.getSource().getPlayerOrException()))
        .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
            .executes(context -> executeBalance(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target")))
        )
    ;
        dispatcher.register(balanceCmd);
        dispatcher.register(Commands.literal("bal").executes(balanceCmd.getCommand()).redirect(balanceCmd.build()));
        dispatcher.register(Commands.literal("ebal").executes(balanceCmd.getCommand()).redirect(balanceCmd.build()));
        dispatcher.register(Commands.literal("ebalance").executes(balanceCmd.getCommand()).redirect(balanceCmd.build()));
        dispatcher.register(Commands.literal("money").executes(balanceCmd.getCommand()).redirect(balanceCmd.build()));
        dispatcher.register(Commands.literal("emoney").executes(balanceCmd.getCommand()).redirect(balanceCmd.build()));


    }

    public static int executeBalance(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            return executeBalance(context, context.getSource().getPlayerOrException());
        }

    public static int executeBalance(CommandContext<CommandSourceStack> context, ServerPlayer target) {
            UserData data = UserCache.getUser(target);
            context.getSource().sendSystemMessage(Component.literal(target.getName().getString() + "'s balance: $" + String.format("%.2f", data.money)));
            return 1;
        }

}
