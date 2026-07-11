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

public class CommandTptoggle {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> tptoggleCmd = Commands.literal("tptoggle")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.tptoggle", 0))
        .executes(context -> executeTptoggle(context))
    ;
        com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> tptoggleCmdNode = dispatcher.register(tptoggleCmd);
        dispatcher.register(Commands.literal("etptoggle").requires(tptoggleCmdNode.getRequirement()).redirect(tptoggleCmdNode));


    }

    public static int executeTptoggle(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            if (tpTogglePlayers.contains(player.getUUID())) {
                tpTogglePlayers.remove(player.getUUID());
                context.getSource().sendSystemMessage(Component.literal("Teleportation requests enabled."));
            } else {
                tpTogglePlayers.add(player.getUUID());
                context.getSource().sendSystemMessage(Component.literal("Teleportation requests disabled."));
            }
            return 1;
        }

}
