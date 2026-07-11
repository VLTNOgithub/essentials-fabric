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

public class CommandUnlimited {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> unlCmd = Commands.literal("unlimited")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.unlimited", 2))
            .executes(context -> executeUnlimited(context, ""))
            .then(Commands.argument("item", net.minecraft.commands.arguments.item.ItemArgument.item(registryAccess))
                .executes(context -> executeUnlimited(context, net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(net.minecraft.commands.arguments.item.ItemArgument.getItem(context, "item").getItem()).toString()))
            );
        com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> unlCmdNode = dispatcher.register(unlCmd);
        dispatcher.register(Commands.literal("eunlimited").requires(unlCmdNode.getRequirement()).redirect(unlCmdNode));
        dispatcher.register(Commands.literal("ul").requires(unlCmdNode.getRequirement()).redirect(unlCmdNode));
        dispatcher.register(Commands.literal("unl").requires(unlCmdNode.getRequirement()).redirect(unlCmdNode));
        dispatcher.register(Commands.literal("eul").requires(unlCmdNode.getRequirement()).redirect(unlCmdNode));
        dispatcher.register(Commands.literal("eunl").requires(unlCmdNode.getRequirement()).redirect(unlCmdNode));

    }

    public static int executeUnlimited(CommandContext<CommandSourceStack> context, String item) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        vltno.essentials.UserData data = vltno.essentials.UserCache.getUser(player);
        if (item.isEmpty()) {
            context.getSource().sendSystemMessage(Component.literal("Unlimited items: " + String.join(", ", data.unlimitedItems)));
            return 1;
        }
        if (data.unlimitedItems.contains(item)) {
            data.unlimitedItems.remove(item);
            context.getSource().sendSystemMessage(Component.literal("Disabled unlimited placement for " + item));
        } else {
            data.unlimitedItems.add(item);
            context.getSource().sendSystemMessage(Component.literal("Enabled unlimited placement for " + item));
        }
        vltno.essentials.UserCache.saveUser(player.getUUID());
        return 1;
    }

}
