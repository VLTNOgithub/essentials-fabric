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

public class CommandTpohere {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("tpohere")
        .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
            .executes(context -> executeTpohere(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target")))
        )
    );
        dispatcher.register(Commands.literal("etpohere")
            .executes(context -> executeTpohere(context))
        );

    }

    public static int executeTpohere(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /tpohere <player>")); return 0; }

    public static int executeTpohere(CommandContext<CommandSourceStack> context, ServerPlayer target) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            saveBackLocation(target);
            target.teleportTo(player.level(), player.getX(), player.getY(), player.getZ(), java.util.Collections.emptySet(), target.getYRot(), target.getXRot(), false);
            context.getSource().sendSystemMessage(Component.literal("Teleported " + target.getName().getString() + " to you (Override)."));
            return 1;
        }

}
