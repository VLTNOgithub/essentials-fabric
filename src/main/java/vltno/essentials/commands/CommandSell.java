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

public class CommandSell {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> sellCmd = Commands.literal("sell")
            .then(Commands.literal("hand")
                .executes(context -> executeSellHand(context)))
            .then(Commands.literal("inventory")
                .executes(context -> executeSellInventory(context)));
        dispatcher.register(sellCmd);
        dispatcher.register(Commands.literal("esell").redirect(sellCmd.build()));

    }

    public static int executeSellHand(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
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
        double price = itemWorth.get(itemName) * hand.getCount();
        UserData data = UserCache.getUser(player);
        data.money += price;
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, net.minecraft.world.item.ItemStack.EMPTY);
        context.getSource().sendSystemMessage(Component.literal("Sold item for $" + String.format("%.2f", price) + ". New balance: $" + String.format("%.2f", data.money)));
        return 1;
    }

    public static int executeSellInventory(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        double total = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            net.minecraft.world.item.ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty()) {
                String itemName = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
                if (itemWorth.containsKey(itemName)) {
                    total += itemWorth.get(itemName) * stack.getCount();
                    player.getInventory().setItem(i, net.minecraft.world.item.ItemStack.EMPTY);
                }
            }
        }
        if (total > 0) {
            UserData data = UserCache.getUser(player);
            data.money += total;
            context.getSource().sendSystemMessage(Component.literal("Sold inventory items for $" + String.format("%.2f", total) + ". New balance: $" + String.format("%.2f", data.money)));
        } else {
            context.getSource().sendSystemMessage(Component.literal("No sellable items in inventory."));
        }
        return 1;
    }

}
