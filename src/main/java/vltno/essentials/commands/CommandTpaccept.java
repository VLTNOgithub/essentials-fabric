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

public class CommandTpaccept {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("tpaccept")
        .executes(context -> executeTpaccept(context))
    );
        dispatcher.register(Commands.literal("etpaccept")
            .executes(context -> executeTpaccept(context))
        );
        dispatcher.register(Commands.literal("tpyes")
            .executes(context -> executeTpaccept(context))
        );
        dispatcher.register(Commands.literal("etpyes")
            .executes(context -> executeTpaccept(context))
        );

    }

    public static int executeTpaccept(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            TeleportRequest req = pendingRequests.remove(player.getUUID());
            if (req == null || System.currentTimeMillis() - req.timestamp > 120000) {
                context.getSource().sendSystemMessage(Component.literal("You do not have any pending teleport requests."));
                return 0;
            }
            ServerPlayer sender = context.getSource().getServer().getPlayerList().getPlayer(req.sender);
            if (sender == null) {
                context.getSource().sendSystemMessage(Component.literal("The player who sent the request is no longer online."));
                return 0;
            }
            if (req.isTpaHere) {
                player.teleportTo(sender.level(), sender.getX(), sender.getY(), sender.getZ(), java.util.Collections.emptySet(), player.getYRot(), player.getXRot(), false);
                context.getSource().sendSystemMessage(Component.literal("Teleported to " + sender.getName().getString() + "."));
                sender.sendSystemMessage(Component.literal(player.getName().getString() + " accepted your teleport request."));
            } else {
                sender.teleportTo(player.level(), player.getX(), player.getY(), player.getZ(), java.util.Collections.emptySet(), sender.getYRot(), sender.getXRot(), false);
                context.getSource().sendSystemMessage(Component.literal(sender.getName().getString() + " has been teleported to you."));
                sender.sendSystemMessage(Component.literal("Teleport request accepted."));
            }
            return 1;
        }

}
