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

public class CommandPweather {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("pweather")
            .executes(context -> executePweather(context))
        );
        dispatcher.register(Commands.literal("playerweather")
            .executes(context -> executePweather(context))
        );
        dispatcher.register(Commands.literal("eplayerweather")
            .executes(context -> executePweather(context))
        );
        dispatcher.register(Commands.literal("epweather")
            .executes(context -> executePweather(context))
        );

    }

    public static int executePweather(CommandContext<CommandSourceStack> context) {
            context.getSource().sendSystemMessage(Component.literal("Usage: /pweather <weather>"));
            return 0;
        }

}
