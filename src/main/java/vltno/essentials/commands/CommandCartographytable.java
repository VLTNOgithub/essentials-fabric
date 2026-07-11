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

public class CommandCartographytable {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> cartographytableCmd = Commands.literal("cartographytable")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.cartographytable", 0))
            .executes(context -> executeCartographytable(context))
        ;
        com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> cartographytableCmdNode = dispatcher.register(cartographytableCmd);
        dispatcher.register(Commands.literal("ecartographytable").requires(cartographytableCmdNode.getRequirement()).redirect(cartographytableCmdNode));
        dispatcher.register(Commands.literal("carttable").requires(cartographytableCmdNode.getRequirement()).redirect(cartographytableCmdNode));
        dispatcher.register(Commands.literal("ecarttable").requires(cartographytableCmdNode.getRequirement()).redirect(cartographytableCmdNode));


    }

    public static int executeCartographytable(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inventory, p) -> {
                return new net.minecraft.world.inventory.CartographyTableMenu(id, inventory, net.minecraft.world.inventory.ContainerLevelAccess.NULL) {
                    @Override
                    public boolean stillValid(net.minecraft.world.entity.player.Player p) { return true; }
                };
            }, Component.literal("Cartography Table")));
            return 1;
        }

}
