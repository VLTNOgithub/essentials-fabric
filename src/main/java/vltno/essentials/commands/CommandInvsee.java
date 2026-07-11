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

public class CommandInvsee {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> invseeCmd = Commands.literal("invsee")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.invsee", 2))
        .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
            .executes(context -> executeInvsee(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target")))
        )
    ;
        com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> invseeCmdNode = dispatcher.register(invseeCmd);
        dispatcher.register(Commands.literal("einvsee").requires(invseeCmdNode.getRequirement()).redirect(invseeCmdNode));


    }

    public static int executeInvsee(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /invsee <player>")); return 0; }

    public static int executeInvsee(CommandContext<CommandSourceStack> context, ServerPlayer target) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inv, p) -> {
                return new net.minecraft.world.inventory.ChestMenu(net.minecraft.world.inventory.MenuType.GENERIC_9x4, id, inv, target.getInventory(), 4);
            }, Component.literal(target.getName().getString() + "'s Inventory")));
            return 1;
        }

}
