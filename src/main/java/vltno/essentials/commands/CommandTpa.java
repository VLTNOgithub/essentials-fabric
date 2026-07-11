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

public class CommandTpa {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> tpaCmd = Commands.literal("tpa")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.tpa", 0))
        .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
            .executes(context -> executeTpa(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target")))
        );
        dispatcher.register(tpaCmd);
        dispatcher.register(Commands.literal("call").redirect(tpaCmd.build()));
        dispatcher.register(Commands.literal("ecall").redirect(tpaCmd.build()));
        dispatcher.register(Commands.literal("etpa").redirect(tpaCmd.build()));
        dispatcher.register(Commands.literal("tpask").redirect(tpaCmd.build()));
        dispatcher.register(Commands.literal("etpask").redirect(tpaCmd.build()));

    }

    public static int executeTpa(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /tpa <player>")); return 0; }

    public static int executeTpa(CommandContext<CommandSourceStack> context, ServerPlayer target) throws CommandSyntaxException {
            ServerPlayer sender = context.getSource().getPlayerOrException();
            if (tpTogglePlayers.contains(target.getUUID())) {
                context.getSource().sendSystemMessage(Component.literal(target.getName().getString() + " has teleportation disabled."));
                return 0;
            }
            if (tpAutoPlayers.contains(target.getUUID())) {
                sender.teleportTo(sender.level(), target.getX(), target.getY(), target.getZ(), java.util.Collections.emptySet(), sender.getYRot(), sender.getXRot(), false);
                context.getSource().sendSystemMessage(Component.literal("Teleported to " + target.getName().getString() + " (Auto-Accepted)."));
                return 1;
            }
            pendingRequests.put(target.getUUID(), new TeleportRequest(sender.getUUID(), false));
            context.getSource().sendSystemMessage(Component.literal("Teleport request sent to " + target.getName().getString() + "."));
            target.sendSystemMessage(Component.literal(sender.getName().getString() + " has requested to teleport to you. Type /tpaccept to accept or /tpdeny to deny."));
            return 1;
        }

}
