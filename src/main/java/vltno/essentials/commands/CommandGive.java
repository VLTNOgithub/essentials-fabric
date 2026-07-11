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

public class CommandGive {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> giveCmd = Commands.literal("give")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.give", 2))
            .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
                .then(Commands.argument("item", net.minecraft.commands.arguments.item.ItemArgument.item(registryAccess))
                    .executes(context -> executeGive(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target"), net.minecraft.commands.arguments.item.ItemArgument.getItem(context, "item"), 1))
                    .then(Commands.argument("count", com.mojang.brigadier.arguments.IntegerArgumentType.integer(1))
                        .executes(context -> executeGive(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target"), net.minecraft.commands.arguments.item.ItemArgument.getItem(context, "item"), com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(context, "count")))
                    )
                )
            );
        com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> giveCmdNode = dispatcher.register(giveCmd);
        dispatcher.register(Commands.literal("egive").requires(giveCmdNode.getRequirement()).redirect(giveCmdNode));

    }

    public static int executeGive(CommandContext<CommandSourceStack> context, ServerPlayer target, net.minecraft.commands.arguments.item.ItemInput item, int count) throws CommandSyntaxException {
        net.minecraft.world.item.ItemStack stack = item.createItemStack(count, false);
        boolean success = target.getInventory().add(stack);
        if (success && stack.isEmpty()) {
            stack.setCount(1);
            net.minecraft.world.entity.item.ItemEntity drop = target.drop(stack, false);
            if (drop != null) {
                drop.makeFakeItem();
            }
            target.level().playSound(null, target.getX(), target.getY(), target.getZ(), net.minecraft.sounds.SoundEvents.ITEM_PICKUP, net.minecraft.sounds.SoundSource.PLAYERS, 0.2F, ((target.getRandom().nextFloat() - target.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            target.inventoryMenu.broadcastChanges();
        } else {
            net.minecraft.world.entity.item.ItemEntity drop = target.drop(stack, false);
            if (drop != null) {
                drop.setNoPickUpDelay();
                drop.setTarget(target.getUUID());
            }
        }
        context.getSource().sendSystemMessage(Component.literal("Gave " + count + " of " + stack.getDisplayName().getString() + " to " + target.getName().getString()));
        return count;
    }

}
