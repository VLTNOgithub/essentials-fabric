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

import net.minecraft.world.item.Items;
import vltno.essentials.UserCache;
import vltno.essentials.UserData;
import vltno.essentials.EssentialsCommands;
import static vltno.essentials.EssentialsCommands.*;

public class CommandCondense {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> condenseCmd = Commands.literal("condense")
            .executes(context -> executeCondense(context))
        ;
        dispatcher.register(condenseCmd);
        dispatcher.register(Commands.literal("econdense").redirect(condenseCmd.build()));
        dispatcher.register(Commands.literal("compact").redirect(condenseCmd.build()));
        dispatcher.register(Commands.literal("ecompact").redirect(condenseCmd.build()));
        dispatcher.register(Commands.literal("blocks").redirect(condenseCmd.build()));
        dispatcher.register(Commands.literal("eblocks").redirect(condenseCmd.build()));
        dispatcher.register(Commands.literal("toblocks").redirect(condenseCmd.build()));
        dispatcher.register(Commands.literal("etoblocks").redirect(condenseCmd.build()));


    }

    public static int executeCondense(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.item.crafting.RecipeManager manager = player.level().getServer().getRecipeManager();
        boolean condensed = false;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            net.minecraft.world.item.ItemStack stack = player.getInventory().getItem(i);
            if (stack.isEmpty() || stack.getCount() < 9) continue;
            // We just check common items for now since looping all recipes per item is slow in a port.
            // Real essentials has a map of blocks -> ingots.
            // A simple implementation: if the item's name is "iron_ingot", change to "iron_block".
            net.minecraft.world.item.Item item = stack.getItem();
            net.minecraft.world.item.Item result = null;
            if (item == Items.IRON_INGOT) result = Items.IRON_BLOCK;
            else if (item == Items.RAW_IRON) result = Items.RAW_IRON_BLOCK;
            else if (item == Items.IRON_NUGGET) result = Items.IRON_INGOT;
            else if (item == Items.GOLD_INGOT) result = Items.GOLD_BLOCK;
            else if (item == Items.RAW_GOLD) result = Items.RAW_GOLD_BLOCK;
            else if (item == Items.GOLD_NUGGET) result = Items.GOLD_INGOT;
            else if (item == Items.DIAMOND) result = Items.DIAMOND_BLOCK;
            else if (item == Items.EMERALD) result = Items.EMERALD_BLOCK;
            else if (item == Items.LAPIS_LAZULI) result = Items.LAPIS_BLOCK;
            else if (item == Items.REDSTONE) result = Items.REDSTONE_BLOCK;
            else if (item == Items.COAL) result = Items.COAL_BLOCK;
            else if (item == Items.COPPER_INGOT) result = Items.COPPER_BLOCK;
            else if (item == Items.RAW_COPPER) result = Items.RAW_COPPER_BLOCK;
            else if (item == Items.COPPER_NUGGET) result = Items.COPPER_INGOT;
            else if (item == Items.NETHERITE_INGOT) result = Items.NETHERITE_BLOCK;
            else if (item == Items.SLIME_BALL) result = Items.SLIME_BLOCK;
            else if (item == Items.WHEAT) result = Items.HAY_BLOCK;

            if (result != null) {
                int blocks = stack.getCount() / 9;
                int remainder = stack.getCount() % 9;
                stack.setCount(remainder);
                net.minecraft.world.item.ItemStack blockStack = new net.minecraft.world.item.ItemStack(result, blocks);
                if (!player.getInventory().add(blockStack)) {
                    player.drop(blockStack, false);
                }
                condensed = true;
            }
        }
        if (condensed) {
            context.getSource().sendSystemMessage(Component.literal("Items condensed into blocks."));
        } else {
            context.getSource().sendSystemMessage(Component.literal("No items could be condensed."));
        }
        return 1;
    }

}
