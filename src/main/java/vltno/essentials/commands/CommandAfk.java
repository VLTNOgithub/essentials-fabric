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

public class CommandAfk {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("afk")
            .executes(context -> executeAfk(context))
        );
        dispatcher.register(Commands.literal("eafk")
            .executes(context -> executeAfk(context))
        );
        dispatcher.register(Commands.literal("away")
            .executes(context -> executeAfk(context))
        );
        dispatcher.register(Commands.literal("eaway")
            .executes(context -> executeAfk(context))
        );

    }

    public static int executeAfk(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            if (afkPlayers.contains(player.getUUID())) {
                afkPlayers.remove(player.getUUID());
                context.getSource().getServer().getPlayerList().broadcastSystemMessage(Component.literal(player.getName().getString() + " is no longer AFK."), false);
            } else {
                afkPlayers.add(player.getUUID());
                context.getSource().getServer().getPlayerList().broadcastSystemMessage(Component.literal(player.getName().getString() + " is now AFK."), false);
            }
            return 1;
        }

}
