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

public class CommandTpahere {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> tpahereCmd = Commands.literal("tpahere")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.tpahere", 0))
        .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
            .executes(context -> executeTpahere(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target")))
        );
        dispatcher.register(tpahereCmd);
        dispatcher.register(Commands.literal("etpahere").redirect(tpahereCmd.build()));

    }

    public static int executeTpahere(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /tpahere <player>")); return 0; }

    public static int executeTpahere(CommandContext<CommandSourceStack> context, ServerPlayer target) throws CommandSyntaxException {
            ServerPlayer sender = context.getSource().getPlayerOrException();
            if (tpTogglePlayers.contains(target.getUUID())) {
                context.getSource().sendSystemMessage(Component.literal(target.getName().getString() + " has teleportation disabled."));
                return 0;
            }
            if (tpAutoPlayers.contains(target.getUUID())) {
                target.teleportTo(sender.level(), sender.getX(), sender.getY(), sender.getZ(), java.util.Collections.emptySet(), target.getYRot(), target.getXRot(), false);
                context.getSource().sendSystemMessage(Component.literal(target.getName().getString() + " was teleported to you (Auto-Accepted)."));
                return 1;
            }
            pendingRequests.put(target.getUUID(), new TeleportRequest(sender.getUUID(), true));
            context.getSource().sendSystemMessage(Component.literal("Teleport here request sent to " + target.getName().getString() + "."));
            target.sendSystemMessage(Component.literal(sender.getName().getString() + " has requested that you teleport to them. Type /tpaccept to accept or /tpdeny to deny."));
            return 1;
        }

}
