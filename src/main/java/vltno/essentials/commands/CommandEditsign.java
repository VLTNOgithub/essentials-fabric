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

public class CommandEditsign {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("editsign")
            .executes(context -> executeEditsign(context))
        );
        dispatcher.register(Commands.literal("sign")
            .executes(context -> executeEditsign(context))
        );
        dispatcher.register(Commands.literal("esign")
            .executes(context -> executeEditsign(context))
        );
        dispatcher.register(Commands.literal("eeditsign")
            .executes(context -> executeEditsign(context))
        );

    }

    public static int executeEditsign(CommandContext<CommandSourceStack> context) {
            context.getSource().sendSystemMessage(Component.literal("Usage: /editsign <set|clear> <line> <text>"));
            return 0;
        }

}
