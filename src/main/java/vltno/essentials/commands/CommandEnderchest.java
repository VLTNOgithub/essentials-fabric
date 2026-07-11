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

public class CommandEnderchest {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> enderchestCmd = Commands.literal("enderchest")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.enderchest", 0))
            .executes(context -> executeEnderchest(context, context.getSource().getPlayerOrException()))
            .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
                .executes(context -> executeEnderchest(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target")))
            );
        com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> enderchestCmdNode = dispatcher.register(enderchestCmd);
        dispatcher.register(Commands.literal("echest").requires(enderchestCmdNode.getRequirement()).redirect(enderchestCmdNode));
        dispatcher.register(Commands.literal("eechest").requires(enderchestCmdNode.getRequirement()).redirect(enderchestCmdNode));
        dispatcher.register(Commands.literal("eenderchest").requires(enderchestCmdNode.getRequirement()).redirect(enderchestCmdNode));
        dispatcher.register(Commands.literal("endersee").requires(enderchestCmdNode.getRequirement()).redirect(enderchestCmdNode));
        dispatcher.register(Commands.literal("eendersee").requires(enderchestCmdNode.getRequirement()).redirect(enderchestCmdNode));
        dispatcher.register(Commands.literal("ec").requires(enderchestCmdNode.getRequirement()).redirect(enderchestCmdNode));
        dispatcher.register(Commands.literal("eec").requires(enderchestCmdNode.getRequirement()).redirect(enderchestCmdNode));

    }

    public static int executeEnderchest(CommandContext<CommandSourceStack> context, ServerPlayer target) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inventory, p) -> {
            return net.minecraft.world.inventory.ChestMenu.threeRows(id, inventory, target.getEnderChestInventory());
        }, Component.literal(target.getName().getString() + "'s Ender Chest")));
        return 1;
    }

}
