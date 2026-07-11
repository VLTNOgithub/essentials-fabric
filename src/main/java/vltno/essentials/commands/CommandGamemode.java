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
        dispatcher.register(Commands.literal("gamemode")
        .then(Commands.argument("mode", net.minecraft.commands.arguments.GameModeArgument.gameMode())
            .executes(context -> executeGamemode(context, java.util.Collections.singletonList(context.getSource().getPlayerOrException()), net.minecraft.commands.arguments.GameModeArgument.getGameMode(context, "mode")))
            .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.players())
                .executes(context -> executeGamemode(context, net.minecraft.commands.arguments.EntityArgument.getPlayers(context, "targets"), net.minecraft.commands.arguments.GameModeArgument.getGameMode(context, "mode")))
            )
        )
    );
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> advCmd = Commands.literal("adventure")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.adventure", 0))
            .executes(context -> executeGamemode(context, java.util.Collections.singletonList(context.getSource().getPlayerOrException()), net.minecraft.world.level.GameType.ADVENTURE))
            .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.players())
                .executes(context -> executeGamemode(context, net.minecraft.commands.arguments.EntityArgument.getPlayers(context, "targets"), net.minecraft.world.level.GameType.ADVENTURE))
            );
        dispatcher.register(advCmd);
        dispatcher.register(Commands.literal("eadventure").redirect(advCmd.build()));
        dispatcher.register(Commands.literal("adventuremode").redirect(advCmd.build()));
        dispatcher.register(Commands.literal("eadventuremode").redirect(advCmd.build()));
        dispatcher.register(Commands.literal("gma").redirect(advCmd.build()));
        dispatcher.register(Commands.literal("egma").redirect(advCmd.build()));
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> creCmd = Commands.literal("creative")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.creative", 0))
            .executes(context -> executeGamemode(context, java.util.Collections.singletonList(context.getSource().getPlayerOrException()), net.minecraft.world.level.GameType.CREATIVE))
            .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.players())
                .executes(context -> executeGamemode(context, net.minecraft.commands.arguments.EntityArgument.getPlayers(context, "targets"), net.minecraft.world.level.GameType.CREATIVE))
            );
        dispatcher.register(creCmd);
        dispatcher.register(Commands.literal("ecreative").redirect(creCmd.build()));
        dispatcher.register(Commands.literal("eecreative").redirect(creCmd.build()));
        dispatcher.register(Commands.literal("creativemode").redirect(creCmd.build()));
        dispatcher.register(Commands.literal("ecreativemode").redirect(creCmd.build()));
        dispatcher.register(Commands.literal("gmc").redirect(creCmd.build()));
        dispatcher.register(Commands.literal("egmc").redirect(creCmd.build()));

        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> surCmd = Commands.literal("survival")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.survival", 0))
            .executes(context -> executeGamemode(context, java.util.Collections.singletonList(context.getSource().getPlayerOrException()), net.minecraft.world.level.GameType.SURVIVAL))
            .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.players())
                .executes(context -> executeGamemode(context, net.minecraft.commands.arguments.EntityArgument.getPlayers(context, "targets"), net.minecraft.world.level.GameType.SURVIVAL))
            );
        dispatcher.register(surCmd);
        dispatcher.register(Commands.literal("esurvival").redirect(surCmd.build()));
        dispatcher.register(Commands.literal("survivalmode").redirect(surCmd.build()));
        dispatcher.register(Commands.literal("esurvivalmode").redirect(surCmd.build()));
        dispatcher.register(Commands.literal("gms").redirect(surCmd.build()));
        dispatcher.register(Commands.literal("egms").redirect(surCmd.build()));

        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> specCmd = Commands.literal("spectator")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.spectator", 0))
            .executes(context -> executeGamemode(context, java.util.Collections.singletonList(context.getSource().getPlayerOrException()), net.minecraft.world.level.GameType.SPECTATOR))
            .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.players())
                .executes(context -> executeGamemode(context, net.minecraft.commands.arguments.EntityArgument.getPlayers(context, "targets"), net.minecraft.world.level.GameType.SPECTATOR))
            );
        dispatcher.register(specCmd);
        dispatcher.register(Commands.literal("gmsp").redirect(specCmd.build()));
        dispatcher.register(Commands.literal("sp").redirect(specCmd.build()));
        dispatcher.register(Commands.literal("egmsp").redirect(specCmd.build()));
        dispatcher.register(Commands.literal("spec").redirect(specCmd.build()));

        dispatcher.register(Commands.literal("gm").executes(context -> executeGamemode(context)));
        dispatcher.register(Commands.literal("egm").executes(context -> executeGamemode(context)));
        dispatcher.register(Commands.literal("egamemode").executes(context -> executeGamemode(context)));

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
