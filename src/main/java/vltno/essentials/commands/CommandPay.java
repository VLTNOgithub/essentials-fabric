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

public class CommandPay {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
                for (String alias : new String[]{"pay", "epay"}) {
            dispatcher.register(Commands.literal(alias)
            .requires(vltno.essentials.EssentialsCommands.require("essentials.pay", 0))
        .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
            .then(Commands.argument("amount", com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg(0.01))
                .executes(context -> executePay(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target"), com.mojang.brigadier.arguments.DoubleArgumentType.getDouble(context, "amount")))
            )
        )
    );
        }


    }

    public static int executePay(CommandContext<CommandSourceStack> context) {
            context.getSource().sendSystemMessage(Component.literal("Usage: /pay <player> <amount>")); return 0;
        }

    public static int executePay(CommandContext<CommandSourceStack> context, ServerPlayer target, double amount) throws CommandSyntaxException {
            ServerPlayer sender = context.getSource().getPlayerOrException();
            if (sender == target) {
                context.getSource().sendSystemMessage(Component.literal("You cannot pay yourself!"));
                return 0;
            }
            UserData targetData = UserCache.getUser(target);
            if (!targetData.payToggle) {
                context.getSource().sendSystemMessage(Component.literal("That player has payments disabled."));
                return 0;
            }
            UserData senderData = UserCache.getUser(sender);
            if (senderData.money < amount) {
                context.getSource().sendSystemMessage(Component.literal("You do not have enough money."));
                return 0;
            }
            senderData.money -= amount;
            targetData.money += amount;
            UserCache.saveUser(sender.getUUID());
            UserCache.saveUser(target.getUUID());
            context.getSource().sendSystemMessage(Component.literal("You paid $" + String.format("%.2f", amount) + " to " + target.getName().getString() + "."));
            target.sendSystemMessage(Component.literal("You received $" + String.format("%.2f", amount) + " from " + sender.getName().getString() + "."));
            return 1;
        }

}
