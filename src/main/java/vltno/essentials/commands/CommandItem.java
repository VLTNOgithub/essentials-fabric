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

public class CommandItem {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
                for (String alias : new String[]{"item", "i", "eitem", "ei"}) {
            dispatcher.register(Commands.literal(alias)
            .requires(vltno.essentials.EssentialsCommands.require("essentials.item", 2))
            .then(Commands.argument("item", net.minecraft.commands.arguments.item.ItemArgument.item(registryAccess))
                .executes(context -> executeItem(context, net.minecraft.commands.arguments.item.ItemArgument.getItem(context, "item"), 1))
                .then(Commands.argument("count", com.mojang.brigadier.arguments.IntegerArgumentType.integer(1))
                    .executes(context -> executeItem(context, net.minecraft.commands.arguments.item.ItemArgument.getItem(context, "item"), com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(context, "count")))
                )
            ));
        }

    }

    public static int executeItem(CommandContext<CommandSourceStack> context, net.minecraft.commands.arguments.item.ItemInput item, int count) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        return CommandGive.executeGive(context, player, item, count);
    }

}
