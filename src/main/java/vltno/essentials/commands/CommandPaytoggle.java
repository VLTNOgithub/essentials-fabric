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

public class CommandPaytoggle {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> paytoggleCmd = Commands.literal("paytoggle")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.paytoggle", 0))
            .executes(context -> executePaytoggle(context))
        ;
        com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> paytoggleCmdNode = dispatcher.register(paytoggleCmd);
        dispatcher.register(Commands.literal("epaytoggle").requires(paytoggleCmdNode.getRequirement()).redirect(paytoggleCmdNode));
        dispatcher.register(Commands.literal("payoff").requires(paytoggleCmdNode.getRequirement()).redirect(paytoggleCmdNode));
        dispatcher.register(Commands.literal("epayoff").requires(paytoggleCmdNode.getRequirement()).redirect(paytoggleCmdNode));
        dispatcher.register(Commands.literal("payon").requires(paytoggleCmdNode.getRequirement()).redirect(paytoggleCmdNode));
        dispatcher.register(Commands.literal("epayon").requires(paytoggleCmdNode.getRequirement()).redirect(paytoggleCmdNode));


    }

    public static int executePaytoggle(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            UserData data = UserCache.getUser(player);
            data.payToggle = !data.payToggle;
            UserCache.saveUser(player.getUUID());
            context.getSource().sendSystemMessage(Component.literal("Accepting payments set to: " + data.payToggle));
            return 1;
        }

}
