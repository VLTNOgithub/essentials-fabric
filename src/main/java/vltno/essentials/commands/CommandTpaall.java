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

public class CommandTpaall {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
                for (String alias : new String[]{"tpaall", "etpaall"}) {
            dispatcher.register(Commands.literal(alias)
            .requires(vltno.essentials.EssentialsCommands.require("essentials.tpaall", 2))
        .executes(context -> executeTpaall(context))
    );
        }


    }

    public static int executeTpaall(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer sender = context.getSource().getPlayerOrException();
            int count = 0;
            for (ServerPlayer target : context.getSource().getServer().getPlayerList().getPlayers()) {
                UserData targetData = UserCache.getUser(target);
                if (target != sender && !targetData.tptoggle) {
                    if (targetData.tpauto) {
                        target.teleportTo(sender.level(), sender.getX(), sender.getY(), sender.getZ(), java.util.Collections.emptySet(), target.getYRot(), target.getXRot(), false);
                    } else {
                        pendingRequests.put(target.getUUID(), new TeleportRequest(sender.getUUID(), true));
                        target.sendSystemMessage(Component.literal(sender.getName().getString() + " has requested that you teleport to them. Type /tpaccept to accept or /tpdeny to deny."));
                    }
                    count++;
                }
            }
            context.getSource().sendSystemMessage(Component.literal("Teleport here requests sent to " + count + " players."));
            return count;
        }

}
