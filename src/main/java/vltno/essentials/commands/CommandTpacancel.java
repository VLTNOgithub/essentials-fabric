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

public class CommandTpacancel {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> tpacancelCmd = Commands.literal("tpacancel")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.tpacancel", 0))
        .executes(context -> executeTpacancel(context))
    ;
        dispatcher.register(tpacancelCmd);
        dispatcher.register(Commands.literal("etpacancel").executes(tpacancelCmd.getCommand()).redirect(tpacancelCmd.build()));


    }

    public static int executeTpacancel(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            boolean canceled = false;
            java.util.Iterator<java.util.Map.Entry<java.util.UUID, TeleportRequest>> it = pendingRequests.entrySet().iterator();
            while (it.hasNext()) {
                java.util.Map.Entry<java.util.UUID, TeleportRequest> entry = it.next();
                if (entry.getValue().sender.equals(player.getUUID())) {
                    it.remove();
                    canceled = true;
                    ServerPlayer target = context.getSource().getServer().getPlayerList().getPlayer(entry.getKey());
                    if (target != null) {
                       target.sendSystemMessage(Component.literal(player.getName().getString() + " canceled their teleport request."));
                    }
                }
            }
            if (canceled) {
                context.getSource().sendSystemMessage(Component.literal("Teleport request canceled."));
            } else {
                context.getSource().sendSystemMessage(Component.literal("You have no pending outgoing teleport requests."));
            }
            return 1;
        }

}
