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

public class CommandItemlore {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("itemlore")
            .executes(context -> executeItemlore(context))
        );
        dispatcher.register(Commands.literal("lore")
            .executes(context -> executeItemlore(context))
        );
        dispatcher.register(Commands.literal("elore")
            .executes(context -> executeItemlore(context))
        );
        dispatcher.register(Commands.literal("ilore")
            .executes(context -> executeItemlore(context))
        );
        dispatcher.register(Commands.literal("eilore")
            .executes(context -> executeItemlore(context))
        );
        dispatcher.register(Commands.literal("eitemlore")
            .executes(context -> executeItemlore(context))
        );

    }

    public static int executeItemlore(CommandContext<CommandSourceStack> context) {
            context.getSource().sendSystemMessage(Component.literal("Usage: /itemlore <add|set|clear> <text>"));
            return 0;
        }

}
