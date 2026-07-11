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

public class CommandExt {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("ext")
            .executes(context -> executeExt(context))
        );
        dispatcher.register(Commands.literal("eext")
            .executes(context -> executeExt(context))
        );
        dispatcher.register(Commands.literal("extinguish")
            .executes(context -> executeExt(context))
        );
        dispatcher.register(Commands.literal("eextinguish")
            .executes(context -> executeExt(context))
        );

    }

    public static int executeExt(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            player.clearFire();
            context.getSource().sendSystemMessage(Component.literal("You have been extinguished."));
            return 1;
        }

}
