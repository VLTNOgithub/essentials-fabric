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

public class CommandKickall {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("kickall")
        .executes(context -> executeKickall(context, null))
        .then(Commands.argument("reason", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
            .executes(context -> executeKickall(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "reason")))
        )
    );
        dispatcher.register(Commands.literal("ekickall")
        .executes(context -> executeKickall(context, null))
        .then(Commands.argument("reason", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
            .executes(context -> executeKickall(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "reason")))
        )
    );

    }

    public static int executeKickall(CommandContext<CommandSourceStack> context, String reason) {
            Component reasonComp = Component.literal(reason != null ? reason : "Kicked by an operator.");
            int count = 0;
            for (ServerPlayer player : context.getSource().getServer().getPlayerList().getPlayers()) {
                if (context.getSource().getEntity() != player) {
                    player.connection.disconnect(reasonComp);
                    count++;
                }
            }
            context.getSource().sendSystemMessage(Component.literal("Kicked " + count + " players."));
            return count;
        }

}
