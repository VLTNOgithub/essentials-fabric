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

public class CommandIgnore {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("ignore")
            .executes(context -> executeIgnore(context))
        );
        dispatcher.register(Commands.literal("eignore")
            .executes(context -> executeIgnore(context))
        );
        dispatcher.register(Commands.literal("unignore")
            .executes(context -> executeIgnore(context))
        );
        dispatcher.register(Commands.literal("eunignore")
            .executes(context -> executeIgnore(context))
        );
        dispatcher.register(Commands.literal("delignore")
            .executes(context -> executeIgnore(context))
        );
        dispatcher.register(Commands.literal("edelignore")
            .executes(context -> executeIgnore(context))
        );
        dispatcher.register(Commands.literal("remignore")
            .executes(context -> executeIgnore(context))
        );
        dispatcher.register(Commands.literal("eremignore")
            .executes(context -> executeIgnore(context))
        );
        dispatcher.register(Commands.literal("rmignore")
            .executes(context -> executeIgnore(context))
        );
        dispatcher.register(Commands.literal("ermignore")
            .executes(context -> executeIgnore(context))
        );

    }

    public static int executeIgnore(CommandContext<CommandSourceStack> context) {
            context.getSource().sendSystemMessage(Component.literal("Usage: /ignore <player>"));
            return 0;
        }

}
