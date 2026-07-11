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

public class CommandItemname {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("itemname")
            .executes(context -> executeItemname(context))
        );
        dispatcher.register(Commands.literal("iname")
            .executes(context -> executeItemname(context))
        );
        dispatcher.register(Commands.literal("einame")
            .executes(context -> executeItemname(context))
        );
        dispatcher.register(Commands.literal("eitemname")
            .executes(context -> executeItemname(context))
        );
        dispatcher.register(Commands.literal("itemrename")
            .executes(context -> executeItemname(context))
        );
        dispatcher.register(Commands.literal("irename")
            .executes(context -> executeItemname(context))
        );
        dispatcher.register(Commands.literal("eitemrename")
            .executes(context -> executeItemname(context))
        );
        dispatcher.register(Commands.literal("eirename")
            .executes(context -> executeItemname(context))
        );

    }

    public static int executeItemname(CommandContext<CommandSourceStack> context) {
            context.getSource().sendSystemMessage(Component.literal("Usage: /itemname <name>"));
            return 0;
        }

}
