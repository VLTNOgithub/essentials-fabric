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

public class CommandPowertooltoggle {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("powertooltoggle")
            .executes(context -> executePowertooltoggle(context))
        );
        dispatcher.register(Commands.literal("epowertooltoggle")
            .executes(context -> executePowertooltoggle(context))
        );
        dispatcher.register(Commands.literal("ptt")
            .executes(context -> executePowertooltoggle(context))
        );
        dispatcher.register(Commands.literal("eptt")
            .executes(context -> executePowertooltoggle(context))
        );
        dispatcher.register(Commands.literal("pttoggle")
            .executes(context -> executePowertooltoggle(context))
        );
        dispatcher.register(Commands.literal("epttoggle")
            .executes(context -> executePowertooltoggle(context))
        );

    }

    public static int executePowertooltoggle(CommandContext<CommandSourceStack> context) {
            context.getSource().sendSystemMessage(Component.literal("Powertools toggled."));
            return 1;
        }

}
