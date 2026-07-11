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

public class CommandPweather {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> pweatherCmd = Commands.literal("pweather")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.pweather", 2))
            .then(Commands.literal("reset")
                .executes(context -> executePweather(context, -1))
            )
            .then(Commands.literal("clear")
                .executes(context -> executePweather(context, 0))
            )
            .then(Commands.literal("rain")
                .executes(context -> executePweather(context, 1))
            );
        dispatcher.register(pweatherCmd);
        dispatcher.register(Commands.literal("playerweather").executes(pweatherCmd.getCommand()).redirect(pweatherCmd.build()));
        dispatcher.register(Commands.literal("eplayerweather").executes(pweatherCmd.getCommand()).redirect(pweatherCmd.build()));
        dispatcher.register(Commands.literal("epweather").executes(pweatherCmd.getCommand()).redirect(pweatherCmd.build()));

    }

    public static int executePweather(CommandContext<CommandSourceStack> context, int type) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        if (type == -1) {
            if (player.level().isRaining()) {
                player.connection.send(new net.minecraft.network.protocol.game.ClientboundGameEventPacket(net.minecraft.network.protocol.game.ClientboundGameEventPacket.START_RAINING, 0.0F));
                player.connection.send(new net.minecraft.network.protocol.game.ClientboundGameEventPacket(net.minecraft.network.protocol.game.ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, player.level().getRainLevel(1.0F)));
            } else {
                player.connection.send(new net.minecraft.network.protocol.game.ClientboundGameEventPacket(net.minecraft.network.protocol.game.ClientboundGameEventPacket.STOP_RAINING, 0.0F));
            }
            context.getSource().sendSystemMessage(Component.literal("Player weather reset."));
        } else if (type == 0) {
            player.connection.send(new net.minecraft.network.protocol.game.ClientboundGameEventPacket(net.minecraft.network.protocol.game.ClientboundGameEventPacket.STOP_RAINING, 0.0F));
            context.getSource().sendSystemMessage(Component.literal("Player weather set to clear."));
        } else if (type == 1) {
            player.connection.send(new net.minecraft.network.protocol.game.ClientboundGameEventPacket(net.minecraft.network.protocol.game.ClientboundGameEventPacket.START_RAINING, 0.0F));
            player.connection.send(new net.minecraft.network.protocol.game.ClientboundGameEventPacket(net.minecraft.network.protocol.game.ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, 1.0F));
            context.getSource().sendSystemMessage(Component.literal("Player weather set to rain."));
        }
        return 1;
    }

}
