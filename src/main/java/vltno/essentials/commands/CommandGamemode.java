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
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> gmCmd = Commands.literal("gamemode")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.gamemode", 2))
            .then(Commands.argument("mode", net.minecraft.commands.arguments.GameModeArgument.gameMode())
                .executes(context -> executeGamemode(context, java.util.Collections.singletonList(context.getSource().getPlayerOrException()), net.minecraft.commands.arguments.GameModeArgument.getGameMode(context, "mode")))
                .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.players())
                    .executes(context -> executeGamemode(context, net.minecraft.commands.arguments.EntityArgument.getPlayers(context, "targets"), net.minecraft.commands.arguments.GameModeArgument.getGameMode(context, "mode")))
                )
            );
        dispatcher.register(gmCmd);
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> advCmd = Commands.literal("adventure")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.adventure", 0))
            .executes(context -> executeGamemode(context, java.util.Collections.singletonList(context.getSource().getPlayerOrException()), net.minecraft.world.level.GameType.ADVENTURE))
            .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.players())
                .executes(context -> executeGamemode(context, net.minecraft.commands.arguments.EntityArgument.getPlayers(context, "targets"), net.minecraft.world.level.GameType.ADVENTURE))
            );
        dispatcher.register(advCmd);
        dispatcher.register(Commands.literal("eadventure").executes(advCmd.getCommand()).redirect(advCmd.build()));
        dispatcher.register(Commands.literal("adventuremode").executes(advCmd.getCommand()).redirect(advCmd.build()));
        dispatcher.register(Commands.literal("eadventuremode").executes(advCmd.getCommand()).redirect(advCmd.build()));
        dispatcher.register(Commands.literal("gma").executes(advCmd.getCommand()).redirect(advCmd.build()));
        dispatcher.register(Commands.literal("egma").executes(advCmd.getCommand()).redirect(advCmd.build()));
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> creCmd = Commands.literal("creative")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.creative", 0))
            .executes(context -> executeGamemode(context, java.util.Collections.singletonList(context.getSource().getPlayerOrException()), net.minecraft.world.level.GameType.CREATIVE))
            .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.players())
                .executes(context -> executeGamemode(context, net.minecraft.commands.arguments.EntityArgument.getPlayers(context, "targets"), net.minecraft.world.level.GameType.CREATIVE))
            );
        dispatcher.register(creCmd);
        dispatcher.register(Commands.literal("ecreative").executes(creCmd.getCommand()).redirect(creCmd.build()));
        dispatcher.register(Commands.literal("eecreative").executes(creCmd.getCommand()).redirect(creCmd.build()));
        dispatcher.register(Commands.literal("creativemode").executes(creCmd.getCommand()).redirect(creCmd.build()));
        dispatcher.register(Commands.literal("ecreativemode").executes(creCmd.getCommand()).redirect(creCmd.build()));
        dispatcher.register(Commands.literal("gmc").executes(creCmd.getCommand()).redirect(creCmd.build()));
        dispatcher.register(Commands.literal("egmc").executes(creCmd.getCommand()).redirect(creCmd.build()));

        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> surCmd = Commands.literal("survival")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.survival", 0))
            .executes(context -> executeGamemode(context, java.util.Collections.singletonList(context.getSource().getPlayerOrException()), net.minecraft.world.level.GameType.SURVIVAL))
            .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.players())
                .executes(context -> executeGamemode(context, net.minecraft.commands.arguments.EntityArgument.getPlayers(context, "targets"), net.minecraft.world.level.GameType.SURVIVAL))
            );
        dispatcher.register(surCmd);
        dispatcher.register(Commands.literal("esurvival").executes(surCmd.getCommand()).redirect(surCmd.build()));
        dispatcher.register(Commands.literal("survivalmode").executes(surCmd.getCommand()).redirect(surCmd.build()));
        dispatcher.register(Commands.literal("esurvivalmode").executes(surCmd.getCommand()).redirect(surCmd.build()));
        dispatcher.register(Commands.literal("gms").executes(surCmd.getCommand()).redirect(surCmd.build()));
        dispatcher.register(Commands.literal("egms").executes(surCmd.getCommand()).redirect(surCmd.build()));

        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> specCmd = Commands.literal("spectator")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.spectator", 0))
            .executes(context -> executeGamemode(context, java.util.Collections.singletonList(context.getSource().getPlayerOrException()), net.minecraft.world.level.GameType.SPECTATOR))
            .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.players())
                .executes(context -> executeGamemode(context, net.minecraft.commands.arguments.EntityArgument.getPlayers(context, "targets"), net.minecraft.world.level.GameType.SPECTATOR))
            );
        dispatcher.register(specCmd);
        dispatcher.register(Commands.literal("gmsp").executes(specCmd.getCommand()).redirect(specCmd.build()));
        dispatcher.register(Commands.literal("sp").executes(specCmd.getCommand()).redirect(specCmd.build()));
        dispatcher.register(Commands.literal("egmsp").executes(specCmd.getCommand()).redirect(specCmd.build()));
        dispatcher.register(Commands.literal("spec").executes(specCmd.getCommand()).redirect(specCmd.build()));

        dispatcher.register(Commands.literal("gm").executes(gmCmd.getCommand()).redirect(gmCmd.build()));
        dispatcher.register(Commands.literal("egm").executes(gmCmd.getCommand()).redirect(gmCmd.build()));
        dispatcher.register(Commands.literal("egamemode").executes(gmCmd.getCommand()).redirect(gmCmd.build()));

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
