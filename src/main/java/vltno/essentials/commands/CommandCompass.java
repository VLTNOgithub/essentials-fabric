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

public class CommandCompass {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> compassCmd = Commands.literal("compass")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.compass", 0))
            .executes(context -> executeCompass(context))
        ;
        dispatcher.register(compassCmd);
        dispatcher.register(Commands.literal("ecompass").redirect(compassCmd.build()));
        dispatcher.register(Commands.literal("direction").redirect(compassCmd.build()));
        dispatcher.register(Commands.literal("edirection").redirect(compassCmd.build()));


    }

    public static int executeCompass(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            int bearing = (int) (player.getYRot() + 180 + 360) % 360;
            String dir;
            if (bearing < 23) dir = "North";
            else if (bearing < 68) dir = "North-East";
            else if (bearing < 113) dir = "East";
            else if (bearing < 158) dir = "South-East";
            else if (bearing < 203) dir = "South";
            else if (bearing < 248) dir = "South-West";
            else if (bearing < 293) dir = "West";
            else if (bearing < 338) dir = "North-West";
            else dir = "North";
            context.getSource().sendSystemMessage(Component.literal("Bearing: " + dir + " (" + bearing + " degrees)."));
            return 1;
        }

}
