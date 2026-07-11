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

public class CommandNear {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("near")
            .executes(context -> executeNear(context))
        );
        dispatcher.register(Commands.literal("enear")
            .executes(context -> executeNear(context))
        );
        dispatcher.register(Commands.literal("nearby")
            .executes(context -> executeNear(context))
        );
        dispatcher.register(Commands.literal("enearby")
            .executes(context -> executeNear(context))
        );

    }

    public static int executeNear(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            java.util.List<ServerPlayer> near = player.level().getEntitiesOfClass(ServerPlayer.class, player.getBoundingBox().inflate(100.0));
            near.remove(player);
            context.getSource().sendSystemMessage(Component.literal("Players nearby: " + near.size()));
            return 1;
        }

}
