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

public class CommandSetworth {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> setworthCmd = Commands.literal("setworth")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.setworth", 2))
            .then(Commands.argument("item", net.minecraft.commands.arguments.item.ItemArgument.item(registryAccess))
                .then(Commands.argument("price", com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg(0))
                    .executes(context -> executeSetworth(context, net.minecraft.commands.arguments.item.ItemArgument.getItem(context, "item"), com.mojang.brigadier.arguments.DoubleArgumentType.getDouble(context, "price")))
                )
            );
        com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> setworthCmdNode = dispatcher.register(setworthCmd);
        dispatcher.register(Commands.literal("esetworth").requires(setworthCmdNode.getRequirement()).redirect(setworthCmdNode));

    }

    public static int executeSetworth(CommandContext<CommandSourceStack> context, net.minecraft.commands.arguments.item.ItemInput item, double price) {
        String itemName = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item.getItem()).toString();
        itemWorth.put(itemName, price);
        vltno.essentials.EssentialsCommands.saveWorth();
        context.getSource().sendSystemMessage(Component.literal("Set worth of " + itemName + " to $" + String.format("%.2f", price) + "."));
        return 1;
    }

}
