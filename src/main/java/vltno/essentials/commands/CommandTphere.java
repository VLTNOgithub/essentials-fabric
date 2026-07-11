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

public class CommandTphere {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> tphereCmd = Commands.literal("tphere")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.tphere", 0))
        .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.entities())
            .executes(context -> executeTphere(context, net.minecraft.commands.arguments.EntityArgument.getEntities(context, "targets")))
        )
    ;
        dispatcher.register(tphereCmd);
        dispatcher.register(Commands.literal("s").redirect(tphereCmd.build()));
        dispatcher.register(Commands.literal("etphere").redirect(tphereCmd.build()));


    }

    public static int executeTphere(CommandContext<CommandSourceStack> context, Collection<? extends net.minecraft.world.entity.Entity> targets) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            for (net.minecraft.world.entity.Entity target : targets) {
                if (target instanceof ServerPlayer pTarget) {
                    saveBackLocation(pTarget);
                    pTarget.teleportTo(player.level(), player.getX(), player.getY(), player.getZ(), java.util.Collections.emptySet(), player.getYRot(), player.getXRot(), false);
                } else {
                    target.teleportTo(player.getX(), player.getY(), player.getZ());
                }
            }
            context.getSource().sendSystemMessage(Component.literal("Teleported " + targets.size() + " entities to you."));
            return targets.size();
        }

}
