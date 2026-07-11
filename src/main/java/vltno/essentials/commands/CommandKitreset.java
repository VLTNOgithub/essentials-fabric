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

public class CommandKitreset {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("kitreset")
            .executes(context -> executeKitreset(context))
        );
        dispatcher.register(Commands.literal("ekitreset")
            .executes(context -> executeKitreset(context))
        );
        dispatcher.register(Commands.literal("kitr")
            .executes(context -> executeKitreset(context))
        );
        dispatcher.register(Commands.literal("ekitr")
            .executes(context -> executeKitreset(context))
        );
        dispatcher.register(Commands.literal("resetkit")
            .executes(context -> executeKitreset(context))
        );
        dispatcher.register(Commands.literal("eresetkit")
            .executes(context -> executeKitreset(context))
        );

    }

    public static int executeKitreset(CommandContext<CommandSourceStack> context) {
            context.getSource().sendSystemMessage(Component.literal("Usage: /kitreset <player> <kit>"));
            return 0;
        }

}
