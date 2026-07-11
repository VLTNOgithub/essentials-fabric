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

public class CommandPowertooltoggle {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> powertooltoggleCmd = Commands.literal("powertooltoggle")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.powertooltoggle", 2))
            .executes(context -> executePowertooltoggle(context))
        ;
        com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> powertooltoggleCmdNode = dispatcher.register(powertooltoggleCmd);
        dispatcher.register(Commands.literal("epowertooltoggle").requires(powertooltoggleCmdNode.getRequirement()).redirect(powertooltoggleCmdNode));
        dispatcher.register(Commands.literal("ptt").requires(powertooltoggleCmdNode.getRequirement()).redirect(powertooltoggleCmdNode));
        dispatcher.register(Commands.literal("eptt").requires(powertooltoggleCmdNode.getRequirement()).redirect(powertooltoggleCmdNode));
        dispatcher.register(Commands.literal("pttoggle").requires(powertooltoggleCmdNode.getRequirement()).redirect(powertooltoggleCmdNode));
        dispatcher.register(Commands.literal("epttoggle").requires(powertooltoggleCmdNode.getRequirement()).redirect(powertooltoggleCmdNode));


    }

    public static int executePowertooltoggle(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        net.minecraft.server.level.ServerPlayer player = context.getSource().getPlayerOrException();
        vltno.essentials.UserData data = vltno.essentials.UserCache.getUser(player);
        data.powertoolEnabled = !data.powertoolEnabled;
        vltno.essentials.UserCache.saveUser(player.getUUID());
        context.getSource().sendSystemMessage(Component.literal("Powertools enabled: " + data.powertoolEnabled));
        return 1;
    }

}
