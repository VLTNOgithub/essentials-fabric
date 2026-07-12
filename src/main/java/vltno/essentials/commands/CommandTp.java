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

public class CommandTp {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
                for (String alias : new String[]{"tp", "tele", "etele", "teleport", "eteleport", "etp", "tp2p", "etp2p"}) {
            dispatcher.register(Commands.literal(alias)
            .requires(vltno.essentials.EssentialsCommands.require("essentials.tp", 2))
        .then(Commands.argument("destination", net.minecraft.commands.arguments.EntityArgument.entity())
            .executes(context -> executeTp(context, Collections.singletonList(context.getSource().getPlayerOrException()), net.minecraft.commands.arguments.EntityArgument.getEntity(context, "destination")))
            .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.entities())
                .executes(context -> executeTp(context, net.minecraft.commands.arguments.EntityArgument.getEntities(context, "targets"), net.minecraft.commands.arguments.EntityArgument.getEntity(context, "destination")))
            )
        )
    );
        }


    }

    public static int executeTp(CommandContext<CommandSourceStack> context, Collection<? extends net.minecraft.world.entity.Entity> targets, net.minecraft.world.entity.Entity destination) throws CommandSyntaxException {
            for (net.minecraft.world.entity.Entity target : targets) {
                if (target instanceof ServerPlayer player) {
                    saveBackLocation(player);
                    player.teleportTo((net.minecraft.server.level.ServerLevel) destination.level(), destination.getX(), destination.getY(), destination.getZ(), java.util.Collections.emptySet(), destination.getYRot(), destination.getXRot(), false);
                }
            }
            if (targets.size() == 1) {
                context.getSource().sendSystemMessage(Component.literal("Teleported to " + destination.getName().getString() + "."));
            } else {
                context.getSource().sendSystemMessage(Component.literal("Teleported " + targets.size() + " entities to " + destination.getName().getString() + "."));
            }
            return targets.size();
        }

}
