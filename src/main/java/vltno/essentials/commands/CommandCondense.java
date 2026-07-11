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

public class CommandCondense {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("condense")
            .executes(context -> executeCondense(context))
        );
        dispatcher.register(Commands.literal("econdense")
            .executes(context -> executeCondense(context))
        );
        dispatcher.register(Commands.literal("compact")
            .executes(context -> executeCondense(context))
        );
        dispatcher.register(Commands.literal("ecompact")
            .executes(context -> executeCondense(context))
        );
        dispatcher.register(Commands.literal("blocks")
            .executes(context -> executeCondense(context))
        );
        dispatcher.register(Commands.literal("eblocks")
            .executes(context -> executeCondense(context))
        );
        dispatcher.register(Commands.literal("toblocks")
            .executes(context -> executeCondense(context))
        );
        dispatcher.register(Commands.literal("etoblocks")
            .executes(context -> executeCondense(context))
        );

    }

    public static int executeCondense(CommandContext<CommandSourceStack> context) {
            context.getSource().sendSystemMessage(Component.literal("Condense requires iterating the entire recipe book, skipping for now."));
            return 1;
        }

}
