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

public class CommandJailedplayers {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("jailedplayers")
            .executes(context -> executeJailedplayers(context))
        );
        dispatcher.register(Commands.literal("ejailedplayers")
            .executes(context -> executeJailedplayers(context))
        );
        dispatcher.register(Commands.literal("ejailed")
            .executes(context -> executeJailedplayers(context))
        );
        dispatcher.register(Commands.literal("ejp")
            .executes(context -> executeJailedplayers(context))
        );

    }

    public static int executeJailedplayers(CommandContext<CommandSourceStack> context) {
            context.getSource().sendSystemMessage(Component.literal("Jailed Players list not fully implemented."));
            return 1;
        }

}
