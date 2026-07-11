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

public class CommandSpawner {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("spawner")
            .executes(context -> executeSpawner(context))
        );
        dispatcher.register(Commands.literal("changems")
            .executes(context -> executeSpawner(context))
        );
        dispatcher.register(Commands.literal("echangems")
            .executes(context -> executeSpawner(context))
        );
        dispatcher.register(Commands.literal("espawner")
            .executes(context -> executeSpawner(context))
        );
        dispatcher.register(Commands.literal("mobspawner")
            .executes(context -> executeSpawner(context))
        );
        dispatcher.register(Commands.literal("emobspawner")
            .executes(context -> executeSpawner(context))
        );

    }

    public static int executeSpawner(CommandContext<CommandSourceStack> context) {
            context.getSource().sendSystemMessage(Component.literal("Usage: /spawner <mob>"));
            return 0;
        }

}
