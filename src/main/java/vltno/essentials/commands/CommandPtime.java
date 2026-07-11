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

public class CommandPtime {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> ptimeCmd = Commands.literal("ptime")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.ptime", 2))
            .then(Commands.literal("reset")
                .executes(context -> executePtimeReset(context))
            )
            .then(Commands.argument("time", com.mojang.brigadier.arguments.IntegerArgumentType.integer())
                .executes(context -> executePtime(context, com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(context, "time")))
            );
        com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> ptimeCmdNode = dispatcher.register(ptimeCmd);
        dispatcher.register(Commands.literal("playertime").requires(ptimeCmdNode.getRequirement()).redirect(ptimeCmdNode));
        dispatcher.register(Commands.literal("eplayertime").requires(ptimeCmdNode.getRequirement()).redirect(ptimeCmdNode));
        dispatcher.register(Commands.literal("eptime").requires(ptimeCmdNode.getRequirement()).redirect(ptimeCmdNode));

    }

    public static int executePtime(CommandContext<CommandSourceStack> context, int time) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.connection.send(new net.minecraft.network.protocol.game.ClientboundSetTimePacket(player.level().getGameTime(), time, false));
        context.getSource().sendSystemMessage(Component.literal("Player time set to " + time));
        return 1;
    }

    public static int executePtimeReset(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.connection.send(new net.minecraft.network.protocol.game.ClientboundSetTimePacket(player.level().getGameTime(), player.level().getDayTime(), player.level().getGameRules().get(net.minecraft.world.level.gamerules.GameRules.ADVANCE_TIME)));
        context.getSource().sendSystemMessage(Component.literal("Player time reset to server time."));
        return 1;
    }

}
