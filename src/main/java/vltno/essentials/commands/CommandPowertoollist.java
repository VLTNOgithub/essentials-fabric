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

public class CommandPowertoollist {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("powertoollist")
            .executes(context -> executePowertoollist(context))
        );
        dispatcher.register(Commands.literal("epowertoollist")
            .executes(context -> executePowertoollist(context))
        );
        dispatcher.register(Commands.literal("ptlist")
            .executes(context -> executePowertoollist(context))
        );
        dispatcher.register(Commands.literal("eptlist")
            .executes(context -> executePowertoollist(context))
        );

    }

    public static int executePowertoollist(CommandContext<CommandSourceStack> context) {
            context.getSource().sendSystemMessage(Component.literal("No powertools active."));
            return 1;
        }

}
