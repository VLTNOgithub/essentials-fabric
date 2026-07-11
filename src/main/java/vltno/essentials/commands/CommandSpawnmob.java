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

public class CommandSpawnmob {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("spawnmob")
            .executes(context -> executeSpawnmob(context))
        );
        dispatcher.register(Commands.literal("mob")
            .executes(context -> executeSpawnmob(context))
        );
        dispatcher.register(Commands.literal("emob")
            .executes(context -> executeSpawnmob(context))
        );
        dispatcher.register(Commands.literal("spawnentity")
            .executes(context -> executeSpawnmob(context))
        );
        dispatcher.register(Commands.literal("espawnentity")
            .executes(context -> executeSpawnmob(context))
        );
        dispatcher.register(Commands.literal("espawnmob")
            .executes(context -> executeSpawnmob(context))
        );

    }

    public static int executeSpawnmob(CommandContext<CommandSourceStack> context) {
            context.getSource().sendSystemMessage(Component.literal("Usage: /spawnmob <mob> [amount]"));
            return 0;
        }

}
