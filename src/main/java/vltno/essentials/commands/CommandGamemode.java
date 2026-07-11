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
        com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> gmCmdNode = dispatcher.register(gmCmd);
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> advCmd = Commands.literal("adventure")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.adventure", 0))
            .executes(context -> executeGamemode(context, java.util.Collections.singletonList(context.getSource().getPlayerOrException()), net.minecraft.world.level.GameType.ADVENTURE))
            .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.players())
                .executes(context -> executeGamemode(context, net.minecraft.commands.arguments.EntityArgument.getPlayers(context, "targets"), net.minecraft.world.level.GameType.ADVENTURE))
            );
        com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> advCmdNode = dispatcher.register(advCmd);
        dispatcher.register(Commands.literal("eadventure").requires(advCmdNode.getRequirement()).redirect(advCmdNode));
        dispatcher.register(Commands.literal("adventuremode").requires(advCmdNode.getRequirement()).redirect(advCmdNode));
        dispatcher.register(Commands.literal("eadventuremode").requires(advCmdNode.getRequirement()).redirect(advCmdNode));
        dispatcher.register(Commands.literal("gma").requires(advCmdNode.getRequirement()).redirect(advCmdNode));
        dispatcher.register(Commands.literal("egma").requires(advCmdNode.getRequirement()).redirect(advCmdNode));
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> creCmd = Commands.literal("creative")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.creative", 0))
            .executes(context -> executeGamemode(context, java.util.Collections.singletonList(context.getSource().getPlayerOrException()), net.minecraft.world.level.GameType.CREATIVE))
            .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.players())
                .executes(context -> executeGamemode(context, net.minecraft.commands.arguments.EntityArgument.getPlayers(context, "targets"), net.minecraft.world.level.GameType.CREATIVE))
            );
        com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> creCmdNode = dispatcher.register(creCmd);
        dispatcher.register(Commands.literal("ecreative").requires(creCmdNode.getRequirement()).redirect(creCmdNode));
        dispatcher.register(Commands.literal("eecreative").requires(creCmdNode.getRequirement()).redirect(creCmdNode));
        dispatcher.register(Commands.literal("creativemode").requires(creCmdNode.getRequirement()).redirect(creCmdNode));
        dispatcher.register(Commands.literal("ecreativemode").requires(creCmdNode.getRequirement()).redirect(creCmdNode));
        dispatcher.register(Commands.literal("gmc").requires(creCmdNode.getRequirement()).redirect(creCmdNode));
        dispatcher.register(Commands.literal("egmc").requires(creCmdNode.getRequirement()).redirect(creCmdNode));

        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> surCmd = Commands.literal("survival")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.survival", 0))
            .executes(context -> executeGamemode(context, java.util.Collections.singletonList(context.getSource().getPlayerOrException()), net.minecraft.world.level.GameType.SURVIVAL))
            .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.players())
                .executes(context -> executeGamemode(context, net.minecraft.commands.arguments.EntityArgument.getPlayers(context, "targets"), net.minecraft.world.level.GameType.SURVIVAL))
            );
        com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> surCmdNode = dispatcher.register(surCmd);
        dispatcher.register(Commands.literal("esurvival").requires(surCmdNode.getRequirement()).redirect(surCmdNode));
        dispatcher.register(Commands.literal("survivalmode").requires(surCmdNode.getRequirement()).redirect(surCmdNode));
        dispatcher.register(Commands.literal("esurvivalmode").requires(surCmdNode.getRequirement()).redirect(surCmdNode));
        dispatcher.register(Commands.literal("gms").requires(surCmdNode.getRequirement()).redirect(surCmdNode));
        dispatcher.register(Commands.literal("egms").requires(surCmdNode.getRequirement()).redirect(surCmdNode));

        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> specCmd = Commands.literal("spectator")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.spectator", 0))
            .executes(context -> executeGamemode(context, java.util.Collections.singletonList(context.getSource().getPlayerOrException()), net.minecraft.world.level.GameType.SPECTATOR))
            .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.players())
                .executes(context -> executeGamemode(context, net.minecraft.commands.arguments.EntityArgument.getPlayers(context, "targets"), net.minecraft.world.level.GameType.SPECTATOR))
            );
        com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> specCmdNode = dispatcher.register(specCmd);
        dispatcher.register(Commands.literal("gmsp").requires(specCmdNode.getRequirement()).redirect(specCmdNode));
        dispatcher.register(Commands.literal("sp").requires(specCmdNode.getRequirement()).redirect(specCmdNode));
        dispatcher.register(Commands.literal("egmsp").requires(specCmdNode.getRequirement()).redirect(specCmdNode));
        dispatcher.register(Commands.literal("spec").requires(specCmdNode.getRequirement()).redirect(specCmdNode));

        dispatcher.register(Commands.literal("gm").requires(gmCmdNode.getRequirement()).redirect(gmCmdNode));
        dispatcher.register(Commands.literal("egm").requires(gmCmdNode.getRequirement()).redirect(gmCmdNode));
        dispatcher.register(Commands.literal("egamemode").requires(gmCmdNode.getRequirement()).redirect(gmCmdNode));

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
