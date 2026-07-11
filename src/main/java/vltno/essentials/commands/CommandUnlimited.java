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

public class CommandUnlimited {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("unlimited")
            .executes(context -> executeUnlimited(context))
        );
        dispatcher.register(Commands.literal("eunlimited")
            .executes(context -> executeUnlimited(context))
        );
        dispatcher.register(Commands.literal("ul")
            .executes(context -> executeUnlimited(context))
        );
        dispatcher.register(Commands.literal("unl")
            .executes(context -> executeUnlimited(context))
        );
        dispatcher.register(Commands.literal("eul")
            .executes(context -> executeUnlimited(context))
        );
        dispatcher.register(Commands.literal("eunl")
            .executes(context -> executeUnlimited(context))
        );

    }

    public static int executeUnlimited(CommandContext<CommandSourceStack> context) {
            context.getSource().sendSystemMessage(Component.literal("Usage: /unlimited <item>"));
            return 0;
        }

}
