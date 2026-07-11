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

public class CommandBurn {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> burnCmd = Commands.literal("burn")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.burn", 2))
        .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.entities())
            .then(Commands.argument("seconds", com.mojang.brigadier.arguments.IntegerArgumentType.integer(1))
                .executes(context -> executeBurn(context, net.minecraft.commands.arguments.EntityArgument.getEntities(context, "targets"), com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(context, "seconds")))
            )
        );
        dispatcher.register(burnCmd);
        dispatcher.register(Commands.literal("eburn").executes(burnCmd.getCommand()).redirect(burnCmd.build()));

    }

    public static int executeBurn(CommandContext<CommandSourceStack> context, Collection<? extends net.minecraft.world.entity.Entity> targets, int seconds) {
            for (net.minecraft.world.entity.Entity target : targets) {
                target.igniteForSeconds(seconds);
            }
            context.getSource().sendSystemMessage(Component.literal("Ignited " + targets.size() + " entities for " + seconds + " seconds."));
            return targets.size();
        }

}
