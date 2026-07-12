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

public class CommandEco {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
                for (String alias : new String[]{"eco", "eeco", "economy", "eeconomy"}) {
            dispatcher.register(Commands.literal(alias)
            .requires(vltno.essentials.EssentialsCommands.require("essentials.eco", 2))
        .then(Commands.argument("action", com.mojang.brigadier.arguments.StringArgumentType.word())
            .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
                .then(Commands.argument("amount", com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg(0))
                    .executes(context -> executeEco(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "action"), net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target"), com.mojang.brigadier.arguments.DoubleArgumentType.getDouble(context, "amount")))
                )
            )
        ));
        }

    }

    public static int executeEco(CommandContext<CommandSourceStack> context, String action, ServerPlayer target, double amount) {
            UserData data = UserCache.getUser(target);
            switch (action.toLowerCase()) {
                case "give": data.money += amount; break;
                case "take": data.money -= amount; break;
                case "set": data.money = amount; break;
                case "reset": data.money = 0.0; break;
                default:
                    context.getSource().sendSystemMessage(Component.literal("Invalid action. Use give, take, set, or reset."));
                    return 0;
            }
            UserCache.saveUser(target.getUUID());
            context.getSource().sendSystemMessage(Component.literal("Economy for " + target.getName().getString() + " updated. New balance: $" + String.format("%.2f", data.money)));
            return 1;
        }

}
