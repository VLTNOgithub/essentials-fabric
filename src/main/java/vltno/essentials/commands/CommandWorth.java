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

public class CommandWorth {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
                for (String alias : new String[]{"worth", "eprice", "price", "eworth"}) {
            dispatcher.register(Commands.literal(alias)
            .requires(vltno.essentials.EssentialsCommands.require("essentials.worth", 0))
            .executes(context -> executeWorthHand(context))
            .then(Commands.argument("item", net.minecraft.commands.arguments.item.ItemArgument.item(registryAccess))
                .executes(context -> executeWorthItem(context, net.minecraft.commands.arguments.item.ItemArgument.getItem(context, "item"), 1))
                .then(Commands.argument("count", com.mojang.brigadier.arguments.IntegerArgumentType.integer(1))
                    .executes(context -> executeWorthItem(context, net.minecraft.commands.arguments.item.ItemArgument.getItem(context, "item"), com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(context, "count")))
                )
            ));
        }

    }

    public static int executeWorthHand(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.item.ItemStack hand = player.getMainHandItem();
        if (hand.isEmpty()) {
            context.getSource().sendSystemMessage(Component.literal("You are not holding an item."));
            return 0;
        }
        String itemName = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(hand.getItem()).toString();
        if (!itemWorth.containsKey(itemName)) {
            context.getSource().sendSystemMessage(Component.literal("That item has no set worth."));
            return 0;
        }
        double price = itemWorth.get(itemName);
        context.getSource().sendSystemMessage(Component.literal("Worth of " + hand.getDisplayName().getString() + " is $" + String.format("%.2f", price) + " each, $" + String.format("%.2f", price * hand.getCount()) + " for stack of " + hand.getCount()));
        return 1;
    }

    public static int executeWorthItem(CommandContext<CommandSourceStack> context, net.minecraft.commands.arguments.item.ItemInput item, int count) {
        String itemName = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item.getItem()).toString();
        if (!itemWorth.containsKey(itemName)) {
            context.getSource().sendSystemMessage(Component.literal("That item has no set worth."));
            return 0;
        }
        double price = itemWorth.get(itemName);
        context.getSource().sendSystemMessage(Component.literal("Worth of " + itemName + " is $" + String.format("%.2f", price) + " each, $" + String.format("%.2f", price * count) + " for stack of " + count));
        return 1;
    }

}
