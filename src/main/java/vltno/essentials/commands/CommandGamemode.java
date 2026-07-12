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

public class CommandGamemode {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
                for (String alias : new String[]{"gamemode", "gm", "egm", "egamemode"}) {
            dispatcher.register(Commands.literal(alias)
            .requires(vltno.essentials.EssentialsCommands.require("essentials.gamemode", 2))
            .then(Commands.argument("mode", net.minecraft.commands.arguments.GameModeArgument.gameMode())
                .executes(context -> executeGamemode(context, java.util.Collections.singletonList(context.getSource().getPlayerOrException()), net.minecraft.commands.arguments.GameModeArgument.getGameMode(context, "mode")))
                .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.players())
                    .executes(context -> executeGamemode(context, net.minecraft.commands.arguments.EntityArgument.getPlayers(context, "targets"), net.minecraft.commands.arguments.GameModeArgument.getGameMode(context, "mode")))
                )
            ));
        }

    }

    public static int executeGamemode(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /gamemode <mode> [player]")); return 0; }

    public static int executeGamemode(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> targets, net.minecraft.world.level.GameType mode) {
            for (ServerPlayer target : targets) {
                target.setGameMode(mode);
                target.sendSystemMessage(Component.literal("Your game mode has been updated to " + mode.getName() + "."));
            }
            if (targets.size() == 1 && targets.iterator().next() == context.getSource().getEntity()) return 1;
            context.getSource().sendSystemMessage(Component.literal("Set game mode " + mode.getName() + " for " + targets.size() + " players."));
            return targets.size();
        }

}
